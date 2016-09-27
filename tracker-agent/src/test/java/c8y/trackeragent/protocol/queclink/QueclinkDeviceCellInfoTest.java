package c8y.trackeragent.protocol.queclink;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Matchers.anyString;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.CellInfo;
import c8y.CellTower;
import c8y.Mobile;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.device.TrackerDevice;
import c8y.trackeragent.protocol.queclink.parser.QueclinkDeviceCellInfo;
import c8y.trackeragent.server.TestConnectionDetails;
import c8y.trackeragent.service.MeasurementService;

import static c8y.trackeragent.protocol.TrackingProtocol.QUECLINK;

public class QueclinkDeviceCellInfoTest {
    
    private Logger logger = LoggerFactory.getLogger(QueclinkDeviceCellInfoTest.class);

    private TrackerAgent trackerAgent = mock(TrackerAgent.class);
    private MeasurementService measurementService = mock(MeasurementService.class);
    private TrackerDevice device = mock(TrackerDevice.class);
    
    private TestConnectionDetails connectionDetails;
    
    public final String cellInfoStr = "0244,0091,0d9f,abea,1,,0244,0091,0d9f,abea,2,,0244,0091,0d9f,abea,3,,0244,0091,0d9f,abea,4,,0244,0091,0d9f,abea,5,,0244,0091,0d9f,abea,6,,";
    public final String mobileInfoStr = "0244,0091,0d9f,abee,26";
    public final String queclinkDeviceGSM_gl300 = "+RESP:GTGSM,300400,860599001073709,FRI,"+ cellInfoStr + mobileInfoStr + ",00,20160921072832,F50F$";
    public final String[] queclinkDeviceGSMData = {queclinkDeviceGSM_gl300};
    
    public QueclinkDeviceCellInfo queclinkDeviceCellInfo = new QueclinkDeviceCellInfo(trackerAgent, measurementService);

    
    @Before
    public void setup() {
        when(trackerAgent.getOrCreateTrackerDevice(anyString())).thenReturn(device);
    }

    @Test
    public void testCellInfoSetting () {
        // generate cell info list for testing
        CellInfo cellInfo = generateCellInfo(cellInfoStr);
        
        for (int i = 0; i < queclinkDeviceGSMData.length; ++i) {
            String[] queclinkData = queclinkDeviceGSMData[i].split(QUECLINK.getFieldSeparator());
            
            String imei = queclinkDeviceCellInfo.parse(queclinkData);
            connectionDetails = new TestConnectionDetails();
            connectionDetails.setImei(imei);
            
            queclinkDeviceCellInfo.onParsed(new ReportContext(connectionDetails, queclinkData));
            
            verify(device).setCellInfo(cellInfo);
        }
    }
    
    @Test
    public void testMobileSetting () {
        // generate mobile structure for testing
        Mobile mobile = generateMobileInfo(mobileInfoStr);
        
        for (int i = 0; i < queclinkDeviceGSMData.length; ++i) {
            String[] queclinkData = queclinkDeviceGSMData[i].split(QUECLINK.getFieldSeparator());
            
            String imei = queclinkDeviceCellInfo.parse(queclinkData);
            connectionDetails = new TestConnectionDetails();
            connectionDetails.setImei(imei);
            
            queclinkDeviceCellInfo.onParsed(new ReportContext(connectionDetails, queclinkData));

            verify(device).setMobile(mobile);
        }
    }
    

    @Test
    public void testSignalStrengthMeasurement () {
        for (int i = 0; i < queclinkDeviceGSMData.length; ++i) {
            String[] queclinkData = queclinkDeviceGSMData[i].split(QUECLINK.getFieldSeparator());
            
            String imei = queclinkDeviceCellInfo.parse(queclinkData);
            connectionDetails = new TestConnectionDetails();
            connectionDetails.setImei(imei);
            
            queclinkDeviceCellInfo.onParsed(new ReportContext(connectionDetails, queclinkData));
            
            verify(measurementService).createGSMLevelMeasurement(new BigDecimal(41), device, new DateTime());
            
        }
    }
    
    private CellInfo generateCellInfo(String cellInfoStr) {
        
        String[] cellInfoData = cellInfoStr.split(QUECLINK.getFieldSeparator());
        
        List<CellTower> cellTowers = new ArrayList<CellTower>();
        CellInfo cellInfo = new CellInfo();
        
        int test_signalStrength = 1;
        for (int i = 0; i < cellInfoData.length; i += 6) {
            
            CellTower cellTower = new CellTower();
            cellTower.setRadioType("gsm");
            cellTower.setMobileCountryCode(244);
            cellTower.setMobileNetworkCode(91);
            cellTower.setLocationAreaCode(Integer.parseInt("0d9f", 16));
            cellTower.setCellId(Integer.parseInt("abea", 16));
            // signal strength from report is shifted by 110 dBm (signal strength from report 0 - 63, actual signal strength -110 - -47 dBm)
            int actualSignalStrength = (test_signalStrength++) - 110;
            cellTower.setSignalStrength(actualSignalStrength);
            
            cellTowers.add(cellTower);
        }
        
        cellInfo.setCellTowers(cellTowers);
        return cellInfo;
    }
    
    private Mobile generateMobileInfo(String mobileInfo) {
        String[] mobileData = mobileInfo.split(QUECLINK.getFieldSeparator());
        
        logger.info("mcc {}, mnc {}, lac {}, cellid {}, rxlevel {}", mobileData[0], mobileData[1], mobileData[2], mobileData[3], mobileData[4]);
        Mobile mobile = new Mobile();
        mobile.setMcc(mobileData[0]);
        mobile.setMnc(mobileData[1]);
        int lacDecimal = Integer.parseInt(mobileData[2], 16);
        mobile.setLac(String.valueOf(lacDecimal));
        int cellDecimal = Integer.parseInt(mobileData[3], 16);
        mobile.setCellId(String.valueOf(cellDecimal));
        
        return mobile;
    }

}
