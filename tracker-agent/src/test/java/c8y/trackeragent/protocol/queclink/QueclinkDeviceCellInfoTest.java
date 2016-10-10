package c8y.trackeragent.protocol.queclink;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Matchers.anyString;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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
    
    public final String[] testCellInfoStr = {
            "0244,0091,0d9f,abea,1,,0244,0091,0d9f,abea,2,,0244,0091,0d9f,abea,3,,0244,0091,0d9f,abea,4,,0244,0091,0d9f,abea,5,,0244,0091,0d9f,abea,6,,",
            "0244,0091,0d9f,abea,7,,0244,0091,0d9f,abea,8,,0244,0091,0d9f,abea,9,,0244,0091,0d9f,abea,10,,0244,0091,0d9f,abea,11,,0244,0091,0d9f,abea,12,,",
            "0244,0091,0d9f,abea,7,,0244,0091,0d9f,abea,8,,0244,0091,0d9f,abea,9,,0244,0091,0d9f,abea,10,,0244,0091,0d9f,abea,11,,0244,0091,0d9f,abea,13,,"
    };
    
    public final String cellInfoWithEmptyField = ",,,,7,,0244,0091,0d9f,abea,8,,0244,0091,0d9f,abea,9,,0244,0091,0d9f,abea,10,,0244,0091,0d9f,abea,11,,0244,0091,0d9f,abea,13,,";
    public final String[] testMobileInfoStr = {
            "0244,0091,0d9f,abee,26",
            "0244,0091,0d9f,abec,27",
            "0244,0091,0d9f,abed,28"
    };
    
    public final String queclinkDeviceGSM_gl300 = "+RESP:GTGSM,300400,860599001073709,FRI,"+ testCellInfoStr[0] + testMobileInfoStr[0] + ",00,20160921072832,F50F$";
    public final String queclinkDeviceGSM_gl505 = "+RESP:GTGSM,400100,135790246811220,CTN,"+ testCellInfoStr[1] + testMobileInfoStr[1] +",,20130316013544,034B$";
    public final String queclinkDeviceGSM_gv505 = "+RESP:GTGSM,1F0100,1357902468112201G1JC5444R7252367,,FRI," + testCellInfoStr[2] + testMobileInfoStr[2] + ",00,20090214093254,11F0$";
    public final String[] queclinkDeviceGSMData = {queclinkDeviceGSM_gl300, queclinkDeviceGSM_gl505, queclinkDeviceGSM_gv505};
    
    public final String queclinkDeviceGSMWithEmptyField = "+RESP:GTGSM,300400,860599001073709,FRI,"+ cellInfoWithEmptyField + testMobileInfoStr[0] + ",00,20160921072832,F50F$";

    public QueclinkDeviceCellInfo queclinkDeviceCellInfo = new QueclinkDeviceCellInfo(trackerAgent, measurementService);

    
    @Before
    public void setup() {
        when(trackerAgent.getOrCreateTrackerDevice(anyString())).thenReturn(device);
    }

    @Test
    public void testCellInfoSetting() {
        
        
        for (int i = 0; i < queclinkDeviceGSMData.length; ++i) {
            String[] queclinkData = queclinkDeviceGSMData[i].split(QUECLINK.getFieldSeparator());
            
            String imei = queclinkDeviceCellInfo.parse(queclinkData);
            connectionDetails = new TestConnectionDetails();
            connectionDetails.setImei(imei);
            
            queclinkDeviceCellInfo.onParsed(new ReportContext(connectionDetails, queclinkData));
            
            // generate cell info list for testing
            CellInfo cellInfo = generateCellInfo(testCellInfoStr[i]);
            verify(device).setCellInfo(cellInfo);
        }
    }
    
    @Test
    public void testCellInfoWithEmptyField() {
        String[] queclinkData = queclinkDeviceGSMWithEmptyField.split(QUECLINK.getFieldSeparator());
        
        String imei = queclinkDeviceCellInfo.parse(queclinkData);
        connectionDetails = new TestConnectionDetails();
        connectionDetails.setImei(imei);
        
        queclinkDeviceCellInfo.onParsed(new ReportContext(connectionDetails, queclinkData));
        CellInfo cellInfo = generateCellInfo(testCellInfoStr[2]);
        
        //remove first element of the test cellInfo.
        List<CellTower> cellTowers = cellInfo.getCellTowers();
        cellTowers.remove(0);
        cellInfo.setCellTowers(cellTowers);
        verify(device).setCellInfo(cellInfo);
    }
    
    @Test
    public void testMobileSetting() {
        
        for (int i = 0; i < queclinkDeviceGSMData.length; ++i) {
            String[] queclinkData = queclinkDeviceGSMData[i].split(QUECLINK.getFieldSeparator());
            
            String imei = queclinkDeviceCellInfo.parse(queclinkData);
            connectionDetails = new TestConnectionDetails();
            connectionDetails.setImei(imei);
            
            queclinkDeviceCellInfo.onParsed(new ReportContext(connectionDetails, queclinkData));

            // generate mobile structure for testing
            Mobile mobile = generateMobileInfo(testMobileInfoStr[i]);
            verify(device).setMobile(mobile);
        }
    }
    

    @Test
    public void testSignalStrengthMeasurement() {
        
        int[] signalPercentage = {41, 42, 44};
        
        for (int i = 0; i < queclinkDeviceGSMData.length; ++i) {
            String[] queclinkData = queclinkDeviceGSMData[i].split(QUECLINK.getFieldSeparator());
            
            String imei = queclinkDeviceCellInfo.parse(queclinkData);
            connectionDetails = new TestConnectionDetails();
            connectionDetails.setImei(imei);
            
            queclinkDeviceCellInfo.onParsed(new ReportContext(connectionDetails, queclinkData));
            
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMddHHmmss");
            DateTime dateTime = formatter.parseDateTime(queclinkData[queclinkData.length - 2]);
            
            verify(measurementService).createGSMLevelMeasurement(new BigDecimal(signalPercentage[i]), device, dateTime);
            
        }
    }
    
    private CellInfo generateCellInfo(String cellInfoStr) {
        
        String[] cellInfoData = cellInfoStr.split(QUECLINK.getFieldSeparator());
        
        List<CellTower> cellTowers = new ArrayList<CellTower>();
        CellInfo cellInfo = new CellInfo();
        
        for (int i = 0; i < cellInfoData.length; i += 6) {
            
            CellTower cellTower = new CellTower();
            cellTower.setRadioType("gsm");
            cellTower.setMobileCountryCode(244);
            cellTower.setMobileNetworkCode(91);
            cellTower.setLocationAreaCode(Integer.parseInt("0d9f", 16));
            cellTower.setCellId(Integer.parseInt("abea", 16));
            // signal strength from report is shifted by 110 dBm (signal strength from report 0 - 63, actual signal strength -110 - -47 dBm)
            int actualSignalStrength = Integer.parseInt(cellInfoData[i + 4]) - 110;
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
