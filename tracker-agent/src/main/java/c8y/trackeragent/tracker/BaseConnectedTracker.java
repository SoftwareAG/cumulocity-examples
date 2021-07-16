/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
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

import static java.lang.String.format;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import c8y.trackeragent.devicemapping.DeviceTenantMappingService;
import c8y.trackeragent.exception.TenantNotSubscribedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.cumulocity.sdk.client.SDKException;
import com.google.common.collect.Iterables;

import c8y.trackeragent.context.OperationContext;
import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.device.ManagedObjectCache;
import c8y.trackeragent.devicebootstrap.DeviceBootstrapProcessor;
import c8y.trackeragent.devicebootstrap.DeviceCredentials;
import c8y.trackeragent.devicebootstrap.DeviceCredentialsRepository;
import c8y.trackeragent.exception.NotBootstrapedException;
import c8y.trackeragent.exception.UnknownDeviceException;
import c8y.trackeragent.server.ConnectionDetails;
import c8y.trackeragent.service.TrackerDeviceContextService;
import c8y.trackeragent.utils.ByteHelper;

/**
 * Performs the communication with a connected device. Accepts reports from the
 * input stream and sends commands to the output stream.
 */
public abstract class BaseConnectedTracker<F extends Fragment> implements ConnectedTracker {

    protected static Logger logger = LoggerFactory.getLogger(BaseConnectedTracker.class);

    @Autowired
    protected List<F> fragments = new ArrayList<F>();

    @Autowired
    protected DeviceBootstrapProcessor bootstrapProcessor;

    @Autowired
    protected DeviceTenantMappingService deviceTenantMappingService;

    @Autowired
    protected TrackerDeviceContextService contextService;

    protected final ReportSplitter reportSplitter;

    BaseConnectedTracker(List<F> fragments,
                         DeviceBootstrapProcessor bootstrapProcessor,
                         DeviceTenantMappingService deviceTenantMappingService,
                         TrackerDeviceContextService contextService) {
        this();
        this.fragments = fragments;
        this.bootstrapProcessor = bootstrapProcessor;
        this.deviceTenantMappingService = deviceTenantMappingService;
        this.contextService = contextService;
        
    }

    public BaseConnectedTracker(ReportSplitter reportSplitter) {
        this.reportSplitter = reportSplitter;
    }

    public BaseConnectedTracker() {
        this.reportSplitter = new BaseReportSplitter(getTrackingProtocol().getReportSeparator());
    }

    @Override
    public void executeReports(ConnectionDetails connectionDetails, byte[] reportsBytes) {
        List<String> reports = reportSplitter.split(reportsBytes);
        if (reports.isEmpty()) {
            logger.warn("Report too short for connection {} : {}", connectionDetails, ByteHelper.getString(reportsBytes));
        }
        for (String report : reports) {
            executeReport(connectionDetails, report);
        }
    }

    public void executeReport(ConnectionDetails connectionDetails, String reportStr) {
        logger.info("Process report: {}.", reportStr);
        String[] report = reportStr.split(connectionDetails.getTrackingProtocol().getFieldSeparator());
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
        } catch (TenantNotSubscribedException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    void processReport(ReportContext reportContext) {
        try {
            for (final Parser parser : Iterables.filter(fragments, Parser.class)) {
                processReport(reportContext, parser);
            }
            logger.info("Finished processing report");
        } catch (NotBootstrapedException nbex) {
            logger.debug(nbex.getMessage());
        }
    }

    private void processReport(ReportContext reportContext, Parser parser) {
        logger.debug("Using parser " + parser.getClass());
        String imei = parser.parse(reportContext.getReport());
        if (imei == null) {
            return;
        }
        logger.debug("Got report from IMEI: " + imei);
        String tenant = getTenant(imei);
        contextService.executeWithContext(tenant, imei, reportContext.getTrackingProtocol(),
                () -> {
                    reportContext.setImei(imei);
                    parser.onParsed(reportContext);
                }
        );
    }

    private String getTenant(String imei) {
        String tenant;
        try {
            tenant = deviceTenantMappingService.findTenant(imei);
        } catch (UnknownDeviceException ex) {
            logger.debug("Device with imei {} not yet bootstraped. Will try bootstrap the device.", imei);
            DeviceCredentials deviceCredentials = bootstrapProcessor.tryAccessDeviceCredentials(imei);
            if (deviceCredentials == null) {
                throw new NotBootstrapedException(format("Device with imei %s not yet available. Will skip the report.", imei));
            } 
            logger.debug("Device with imei {} available.", imei);
            tenant = deviceCredentials.getTenant();
        }
        return tenant;
    }

    @Override
    public void executeOperation(OperationContext operationCtx) throws IOException {
        String translation = translateOperation(operationCtx);
        operationCtx.writeOut(translation);
    }
    
    @Override
    public String translateOperation(OperationContext operationCtx) throws IOException {
        String translation = translate(operationCtx);
        if (translation == null) {
            throw new RuntimeException("Command currently not supported!");
        }
        return translation;
    }
    
    private String translate(OperationContext operation) {
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
}
