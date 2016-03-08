package c8y.trackeragent.protocol.rfv16.parser;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.google.common.base.Strings;

import c8y.Mobile;
import c8y.trackeragent.Parser;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.TrackerDevice;
import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.protocol.rfv16.RFV16Constants;
import c8y.trackeragent.protocol.rfv16.message.RFV16ServerMessages;
import c8y.trackeragent.service.AlarmService;

@Component
public class MultiBaseStationDataRFV16Parser extends RFV16Parser implements Parser {
    
    private static Logger logger = LoggerFactory.getLogger(MultiBaseStationDataRFV16Parser.class);
    
    @Autowired
    public MultiBaseStationDataRFV16Parser(TrackerAgent trackerAgent, RFV16ServerMessages serverMessages,
            AlarmService alarmService) {
        super(trackerAgent, serverMessages, alarmService);
    }

    @Override
    public boolean onParsed(ReportContext reportCtx) throws SDKException {
        if (!isNBRReport(reportCtx)) {
            return false;
        }
        logger.debug("Process NBR report", reportCtx);
        processPositionReport(reportCtx);
        return true;
    }

    private void processPositionReport(ReportContext reportCtx) {
        TrackerDevice device = trackerAgent.getOrCreateTrackerDevice(reportCtx.getImei());
        
        createAlarms(reportCtx, device);
        
        Mobile mobile = device.getManagedObject().get(Mobile.class);
        if (mobile == null) {
            mobile = new Mobile();
        }
        
        boolean changedMobile = false;
        String mcc = reportCtx.getEntry(4);
        if (!Strings.isNullOrEmpty(mcc) && !mcc.equals(mobile.getMcc())) {
            mobile.setMcc(mcc);
            changedMobile = true;
        }
        
        String mnc = reportCtx.getEntry(5);
        if (!Strings.isNullOrEmpty(mnc) && !mnc.equals(mobile.getMnc())) {
            mobile.setMnc(mnc);
            changedMobile = true;
        }
        
        String lac = reportCtx.getEntry(8);
        if (!Strings.isNullOrEmpty(lac) && !lac.equals(mobile.getLac())) {
            mobile.setLac(lac);
            changedMobile = true;
        }
        if (changedMobile) {
            device.updateMobile(mobile);
        }
    }

    private Collection<AlarmRepresentation> createAlarms(ReportContext reportCtx, TrackerDevice device) {
        String status = reportCtx.getEntry(reportCtx.getNumberOfEntries() - 1);
        return createAlarms(reportCtx, device, status);
    }

    private boolean isNBRReport(ReportContext reportCtx) {
        return RFV16Constants.MESSAGE_TYPE_MULTI_BASE_STATION_DATA.equals(reportCtx.getEntry(2));
    }
}    
