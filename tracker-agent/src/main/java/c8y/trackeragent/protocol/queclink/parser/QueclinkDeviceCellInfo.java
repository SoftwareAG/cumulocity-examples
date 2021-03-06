/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.protocol.queclink.parser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cumulocity.sdk.client.SDKException;

import c8y.CellInfo;
import c8y.CellTower;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.device.TrackerDevice;
import c8y.trackeragent.protocol.queclink.QueclinkConstants;
import c8y.trackeragent.service.MeasurementService;

/**
 * GL300 report for the information of the service cell and the neighbor cells.
 * +RESP:GTGSM,300400,860599001073709,FRI,0244,0091,0d9f,abea,42,,0244,0091,0d9f,86e4,34,,0244,0091,0d9f,ac07,28,,
 * 0244,0091,0d9f,882d,19,,0244,0091,0d9f,ac3d,18,,0244,0091,0d9f,87ab,15,,0244,0091,0d9f,abee,26,00,20160921072832,F50F$
 * 
 * report_type, protocol version, imei, fix type, mcc1, mnc1, lac1, cellid1, rxlevel1, reserved1, mcc2, mnc2, ..., reserved6, 
 * mcc, mnc, lac, cellid, rxlevel, ta, send time, count num
 * 
 *
 */
@Component
public class QueclinkDeviceCellInfo extends QueclinkParser {

    private Logger logger = LoggerFactory.getLogger(QueclinkDeviceCellInfo.class);
    public static final String GSM_REPORT = "+RESP:GTGSM"; 
    
    private final TrackerAgent trackerAgent;
    private final MeasurementService measurementService;
    
    private static final int totalCells = 7;
    private static final int cellInfoLength = 6;
    private CellInfo cellInfo;
    
    @Autowired
    public QueclinkDeviceCellInfo(TrackerAgent trackerAgent, MeasurementService measurementService) {
        this.trackerAgent = trackerAgent;
        this.measurementService = measurementService;
    }

    @Override
    public boolean onParsed(ReportContext reportCtx) throws SDKException {
        
        if (GSM_REPORT.equals(reportCtx.getEntry(0))) {
            
            String deviceType = reportCtx.getEntry(1).substring(0,2);
            int startIndex;

            if(QueclinkConstants.GV500_ID1.equals(deviceType) || QueclinkConstants.GV500_ID2.equals(deviceType)) { // gv500
                startIndex = 5;
            } else { // gl200, gl300, gl500, gl505 
                startIndex = 4;
            }
            
            int endIndex = startIndex + totalCells * cellInfoLength;
            return parseCellInfo(reportCtx.getReport(), reportCtx.getImei(), startIndex, endIndex);
        } else {
            return false;
        }
    }
    
    private boolean parseCellInfo(String[] report, String imei, int startIndex, int endIndex) {
        
        cellInfo = new CellInfo();
        List<CellTower> cellTowers = new ArrayList<CellTower>();
        
        // iterate over neighbor cells
        for (int i = startIndex; i < endIndex - totalCells; i += cellInfoLength) {
            
            if (report[i].isEmpty() ||
                    report[i + 1].isEmpty() ||
                    report[i + 2].isEmpty() ||
                    report[i + 4].isEmpty()) {
                continue;
            }
            
            CellTower cellTower = new CellTower();
            cellTower.setRadioType("gsm");
            cellTower.setMobileCountryCode(Integer.parseInt(report[i]));
            cellTower.setMobileNetworkCode(Integer.parseInt(report[i + 1]));
            cellTower.setLocationAreaCode(Integer.parseInt(report[i + 2], 16));
            cellTower.setCellId(Integer.parseInt(report[i + 3], 16));
            // signal strength from report is shifted by 110 dBm (signal strength from report 0 - 63, actual signal strength -110 - -47 dBm)
            int actualSignalStrength = Integer.parseInt(report[i + 4]) - 110;
            cellTower.setSignalStrength(actualSignalStrength);
            
            logger.info("mcc {}, mnc {}, lac {}, cellid {}, rxlevel {}", Integer.parseInt(report[i]), 
                    Integer.parseInt(report[i + 1]), 
                    Integer.parseInt(report[i + 2], 16),
                    Integer.parseInt(report[i + 3], 16),
                    Integer.parseInt(report[i + 4]) - 110);
            
            cellTowers.add(cellTower);
        }
   
        cellInfo.setCellTowers(cellTowers);
        TrackerDevice trackerDevice = trackerAgent.getOrCreateTrackerDevice(imei);
        trackerDevice.setCellInfo(cellInfo);
        
        int mainCellStartIndex = endIndex - cellInfoLength;
        logger.info("mcc {}, mnc {}, lac {}, cellid {}, rxlevel {}", report[mainCellStartIndex], 
                report[mainCellStartIndex+1], 
                report[mainCellStartIndex+2], 
                report[mainCellStartIndex+3], 
                report[mainCellStartIndex+4]);
        
        // record main cell info to mobile
        int lacDecimal = Integer.parseInt(report[mainCellStartIndex + 2], 16);
        int cellDecimal = Integer.parseInt(report[mainCellStartIndex + 3], 16);
        
        String mcc = report[mainCellStartIndex];
        String mnc = report[mainCellStartIndex + 1];
        String lac = String.valueOf(lacDecimal);
        String cellId = String.valueOf(cellDecimal);
        trackerDevice.setMobileInfo(mcc, mnc, lac, cellId);
        
        // record main cell's signal strength as measurement
        // convert signal strength to percentage
        BigDecimal signalStrengthPercentage = asPercentage(new BigDecimal(report[mainCellStartIndex + 4]), 0, 63);
        logger.info("Signal strength percentage {}", signalStrengthPercentage);
        
        // include date time that comes from the device for gsm measurement
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMddHHmmss");
        DateTime dateTime = formatter.parseDateTime(report[report.length - 2]);
        
        measurementService.createGSMLevelMeasurement(signalStrengthPercentage, trackerDevice, dateTime);
        
        return true;
    }
    
    private static BigDecimal asPercentage(BigDecimal val, int min, int max) {
        if (val == null) {
            return null;
        }
        int result = ((val.intValue() - min) * 100) / (max - min);
        return new BigDecimal(result);
    }
}
