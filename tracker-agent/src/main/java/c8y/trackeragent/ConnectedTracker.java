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
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.devicebootstrap.DeviceBootstrapProcessor;
import c8y.trackeragent.utils.TrackerContext;

import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.SDKException;

/**
 * Performs the communication with a connected device. Accepts reports from the
 * input stream and sends commands to the output stream.
 */
public class ConnectedTracker implements Runnable, Executor {

    protected static Logger logger = LoggerFactory.getLogger(ConnectedTracker.class);
    
    private char reportSeparator;
    private String fieldSeparator;
    private Socket client;
    private InputStream bis;
    private List<Object> fragments = new ArrayList<Object>();
    private OutputStream out;
    private String imei;
    TrackerContext trackerContext = TrackerContext.get();
    DeviceBootstrapProcessor deviceBootstrapProcessor = DeviceBootstrapProcessor.get();
    
    public ConnectedTracker(Socket client, InputStream bis, char reportSeparator, String fieldSeparator) {
        this.client = client;
        this.bis = bis;
        this.reportSeparator = reportSeparator;
        this.fieldSeparator = fieldSeparator;
    }

    public void addFragment(Object o) {
        fragments.add(o);
    }

    @Override
    public void run() {
        OutputStream out = null;
        try {
            out = client.getOutputStream();
            setOut(out);
            processReports(bis);
        } catch (IOException e) {
            logger.warn("Error during communication with client device", e);
        } catch (SDKException e) {
            logger.warn("Error during communication with the platform", e);
        } finally {
            IOUtils.closeQuietly(out);
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

    String readReport(InputStream is) throws IOException {
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

    void processReport(String[] report) throws SDKException {
        for (Object fragment : fragments) {
            if (fragment instanceof Parser) {
                Parser parser = (Parser) fragment;
                String imei = parser.parse(report);
                if (imei != null) {
                    boolean registered = checkIfDeviceRegistered(imei);
                    if(!registered) {
                        logger.warn("Device for imei {} not registered yet; skip.", imei);
                        return;
                    }
                    boolean success = parser.onParsed(report, imei);
                    if(success) {
                        this.imei = imei;
                        ConnectionRegistry.instance().put(imei, this);
                        break;
                    }
                }
            }
        }
    }

    private boolean checkIfDeviceRegistered(String imei) {
        boolean registered = trackerContext.isDeviceRegistered(imei);
        if(!registered) {
            deviceBootstrapProcessor.startBootstaping(imei);
        }
        return registered;
    }

    @Override
    public void execute(OperationRepresentation operation) throws IOException {
        String translation = translate(operation);
        logger.debug("Executing operation\n{}\n{}", operation, translation);

        if (translation != null) {
            out.write(translation.getBytes());
            out.flush();
        } else {
            operation.setStatus(OperationStatus.FAILED.toString());
            operation.setFailureReason("Command currently not supported");
        }
    }

    public String translate(OperationRepresentation operation) {
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
