/*
 * Copyright (C) 2013 Cumulocity GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of 
 * this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package c8y.trackeragent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.devicebootstrap.DeviceCredentials;
import c8y.trackeragent.event.TrackerAgentEvents;
import c8y.trackeragent.operations.OperationContext;

import com.cumulocity.agent.server.context.DeviceContext;
import com.cumulocity.agent.server.context.DeviceContextService;
import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.SDKException;

/**
 * Performs the communication with a connected device. Accepts reports from the
 * input stream and sends commands to the output stream.
 */
public class ConnectedTracker implements Runnable, Executor {

    protected static Logger logger = LoggerFactory.getLogger(ConnectedTracker.class);

    private final char reportSeparator;
    private final String fieldSeparator;
    private final Socket client;
    private final InputStream bis;
    private final List<Object> fragments = new ArrayList<Object>();
    private final TrackerAgent trackerAgent;
    private final DeviceContextService contextService;

    private OutputStream out;
    private String imei;

    public ConnectedTracker(Socket client, InputStream bis, char reportSeparator, String fieldSeparator, 
            TrackerAgent trackerAgent, DeviceContextService contextService) {
        this.client = client;
        this.bis = bis;
        this.reportSeparator = reportSeparator;
        this.fieldSeparator = fieldSeparator;
        this.trackerAgent = trackerAgent;
        this.contextService = contextService;
    }

    public void addFragment(Object o) {
        fragments.add(o);
    }

    @Override
    public void run() {
        if(bis == null) {
            return;
        }
        try {
            out = client.getOutputStream();
            processReports(bis);
        } catch (IOException e) {
            logger.warn("Error during communication with client device", e);
        } catch (SDKException e) {
            logger.warn("Error during communication with the platform", e);
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(bis);
            try {
                client.close();
            } catch (IOException e) {
                logger.warn("Error during closing socket", e);
            }
        }
    }

    void processReports(InputStream is) throws IOException, SDKException {
        String reportStr;
        while ((reportStr = readReport(is)) != null) {
            String[] report = reportStr.split(fieldSeparator);
            tryProcessReport(report);
        }
        if (imei != null) {
            ConnectionRegistry.instance().remove(imei);
        }
        logger.debug("Connection closed by {} {} ", client.getRemoteSocketAddress(), imei);
    }

    private void tryProcessReport(String[] report) throws SDKException {
        try {
            processReport(report);
        } catch (SDKException x) {
            logger.error("Error processing report " + Arrays.toString(report), x);
            /*
             * What might have happened here? Either the connection to the
             * platform is down or the object has been deleted from the
             * platform. We'll evict the object from the ManagedObjectCache and
             * try again after a while. If that fails, we give up.
             */
            if (imei != null) {
                ManagedObjectCache.instance().evict(imei);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            processReport(report);
        }
    }

    public String readReport(InputStream is) throws IOException {
        StringBuffer result = new StringBuffer();
        int c;

        while ((c = is.read()) != -1) {
            if ((char) c == reportSeparator) {
                break;
            }
            if ((char) c == '\n') {
                continue;
            }
            result.append((char) c);
        }

        logger.debug("Processing report: " + result.toString());

        if (c == -1) {
            return null;
        }

        return result.toString();
    }

    void processReport(String[] report) {
        for (Object fragment : fragments) {
            if (fragment instanceof Parser) {
                final Parser parser = (Parser) fragment;
                final String imei = parser.parse(report);
                if (imei != null) {
                    boolean registered = checkIfDeviceRegistered(imei);
                    if (registered) {
                        final ReportContext reportContext = new ReportContext(report, imei, out);
                        DeviceCredentials credentials = trackerAgent.getContext().getDeviceCredentials(imei);
                        try {
                            contextService.callWithinContext(new DeviceContext(credentials), new Callable<Void>() {

                                @Override
                                public Void call() throws Exception {
                                    boolean success = parser.onParsed(reportContext);
                                    if (success) {
                                        //this.imei = imei;
                                        //ConnectionRegistry.instance().put(imei, this);
                                        //break;
                                        return null;
                                    }
                                    return null;
                                }
                                
                            });
                        } catch (Exception e) {
                            logger.error("Error on parsing request", e);
                        }
                        
                    } 
                }
            }
        }
    }

    private boolean checkIfDeviceRegistered(String imei) {
        boolean registered = trackerAgent.getContext().isDeviceRegistered(imei);
        if (!registered) {
            trackerAgent.sendEvent(new TrackerAgentEvents.NewDeviceEvent(imei));
        }
        return registered;
    }

    @Override
    public void execute(OperationContext operationCtx) throws IOException {
        String translation = translate(operationCtx);
        logger.debug("Executing operation\n{}\n{}", operationCtx, translation);

        if (translation == null) {
            operationCtx.getOperation().setStatus(OperationStatus.FAILED.toString());
            operationCtx.getOperation().setFailureReason("Command currently not supported");
        } else {
            logger.debug("Write to device: {}.", translation);
            out.write(translation.getBytes("US-ASCII"));
            out.flush();
        }
    }

    public String translate(OperationContext operation) {
        for (Object fragment : fragments) {
            if (fragment instanceof Translator) {
                Translator translator = (Translator) fragment;
                String translation = translator.translate(operation);
                if (translation != null) {
                    return translation;
                }
            }
        }
        return null;
    }

    void setOut(OutputStream out) {
        this.out = out;
    }
}
