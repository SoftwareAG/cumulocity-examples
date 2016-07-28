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

package c8y.trackeragent.tracker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.sdk.client.SDKException;
import com.google.common.collect.Iterables;

import c8y.trackeragent.context.OperationContext;
import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.device.ManagedObjectCache;
import c8y.trackeragent.devicebootstrap.DeviceBootstrapProcessor;
import c8y.trackeragent.devicebootstrap.DeviceCredentials;
import c8y.trackeragent.devicebootstrap.DeviceCredentialsRepository;
import c8y.trackeragent.exception.UnknownDeviceException;
import c8y.trackeragent.exception.UnknownTenantException;
import c8y.trackeragent.server.ConnectionDetails;
import c8y.trackeragent.service.TrackerDeviceContextService;

/**
 * Performs the communication with a connected device. Accepts reports from the
 * input stream and sends commands to the output stream.
 */
public class BaseConnectedTracker<F extends Fragment> implements ConnectedTracker {
    
    protected static Logger logger = LoggerFactory.getLogger(BaseConnectedTracker.class);

    private final char reportSeparator;
    private final String fieldSeparator;
    
    @Autowired
    protected List<F> fragments = new ArrayList<F>();
    @Autowired
    protected DeviceBootstrapProcessor bootstrapProcessor;
    @Autowired
    protected DeviceCredentialsRepository credentialsRepository;
    @Autowired
    protected TrackerDeviceContextService contextService;

    BaseConnectedTracker(char reportSeparator, String fieldSeparator, List<F> fragments,
			DeviceBootstrapProcessor bootstrapProcessor, DeviceCredentialsRepository credentialsRepository, 
			TrackerDeviceContextService contextService) {
		this.reportSeparator = reportSeparator;
		this.fieldSeparator = fieldSeparator;
		this.fragments = fragments;
		this.bootstrapProcessor = bootstrapProcessor;
		this.credentialsRepository = credentialsRepository;
		this.contextService = contextService;
	}

	public BaseConnectedTracker(char reportSeparator, String fieldSeparator) {
        this.reportSeparator = reportSeparator;
        this.fieldSeparator = fieldSeparator;
    }
    
    @Override
    public void executeReport(ConnectionDetails connectionDetails, String reportStr) {
        logger.info("Process report: {}.", reportStr);
        String[] report = reportStr.split(fieldSeparator);
        ReportContext reportContext = new ReportContext(connectionDetails, report);
        tryProcessReport(reportContext);
        
    }

    private void tryProcessReport(ReportContext reportContext) throws SDKException {
        try {
            processReport(reportContext);
        } catch (SDKException ex) {
            logger.error("Error processing report: " + reportContext, ex);
            /*
             * What might have happened here? Either the connection to the
             * platform is down or the object has been deleted from the
             * platform. We'll evict the object from the ManagedObjectCache and
             * try again after a while. If that fails, we give up.
             */
            if (reportContext.getImei() != null) {
                ManagedObjectCache.instance().evict(reportContext.getImei());
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            processReport(reportContext);
        }
    }

    void processReport(ReportContext reportContext) {
        for (final Parser parser : Iterables.filter(fragments, Parser.class)) {
            logger.debug("Using parser "+ parser.getClass());
            String imei = parser.parse(reportContext.getReport());
			if (imei == null) {
				continue;
			}
            logger.debug("Got report from IMEI: " + imei);
            DeviceCredentials deviceCredentials;
			try {
				deviceCredentials = credentialsRepository.getDeviceCredentials(imei);
			} catch (UnknownDeviceException ex) {
				logger.debug("Device with imei {} not yet bootstraped. Will try bootstrap the device.", imei);
				deviceCredentials = bootstrapProcessor.tryAccessDeviceCredentials(imei);
				if (deviceCredentials == null) {
					logger.debug("Device with imei {} not yet available. Will skip the report.", imei);
					break;
				} else {
					logger.debug("Device with imei {} available.", imei);
				}
			}
            final String tenant = deviceCredentials.getTenant();
            DeviceCredentials agentCredentials;
            try {
            	agentCredentials = credentialsRepository.getAgentCredentials(tenant);
            } catch (UnknownTenantException ex) {
            	logger.debug("Agent for tenant {} not yet bootstraped. Will try bootstrap the agent.", tenant);
            	agentCredentials = bootstrapProcessor.tryAccessAgentCredentials(tenant);
				if (agentCredentials == null) {
					logger.debug("Agent for tenant {} not yet available. Will skip the report.", tenant);
					break;
				} else {
					logger.debug("Agent for tenant {} available.", tenant);					
				}
            	
            }
            try {
            	contextService.enterContext(tenant, imei);
            	reportContext.setImei(imei);
                parser.onParsed(reportContext);
            } catch (Exception e) {
                logger.error("Error on parsing request", e);
            } finally {
				 contextService.leaveContext();
			}
        }
        logger.debug("Finished processing report");
    }

    @Override
    public void executeOperation(OperationContext operationCtx) throws IOException {
        String translation = translate(operationCtx);
        logger.debug("Executing operation\n{}\n{}", operationCtx, translation);
        if (translation == null) {
            operationCtx.getOperation().setStatus(OperationStatus.FAILED.toString());
            operationCtx.getOperation().setFailureReason("Command currently not supported");
        } else {
            operationCtx.writeOut(translation);
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

    @Override
    public String getReportSeparator() {
        return "" + reportSeparator;
    }

}
