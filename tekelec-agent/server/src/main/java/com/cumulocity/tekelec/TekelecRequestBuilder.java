package com.cumulocity.tekelec;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class TekelecRequestBuilder {
    
    private static final String[] contactReasons = { "DynLim2 Status", "DymLin1 Status", "TSP Requested", "Re-boot", "Manual", "Server Requested",
            "Alarm", "Scheduled" };

    private static final String[] alarmAndStatuses = { "GO Active", "BOR Reset", "WDT Reset", "Limp Along RTC", "Bund Status Closed", "Limits 3 Status",
            "Limits 2 Status", "Limits 1 Status" };

    public TekelecRequest build(byte[] dst) {
        TekelecRequest request = new TekelecRequest();
        int i = 0;
        
        request.setProductType(readInt(dst[i++]));
        request.setHardwareRevision(readInt(dst[i++]));
        request.setFirmwareRevision(readInt(dst[i++]));
        byte contactReasonByte = dst[i++];
        request.setContactReason(getMatchingResult(contactReasonByte, contactReasons));
        byte alarmAndStatusByte = dst[i++];
        request.setAlarmAndStatus(getMatchingResult(alarmAndStatusByte, alarmAndStatuses));
        request.setGsmRssi(readInt(dst[i++]));
        request.setBattery(BigDecimal.valueOf(extractRightBits(dst[i++], 5) + 30).divide(BigDecimal.valueOf(10), 1, RoundingMode.CEILING));
        String imei = "";
        for (int bit = 0 ; bit < 8 ; bit++) {
            imei += String.format("%02X", readInt(dst[i++]));
        }
        request.setImei(imei);
        
        request.setMessageType(dst[i++]);
        byte payloadLength = dst[i++];
        
        i += 6;
        request.setLoggerSpeed(calculateLoggerSpeed(dst[i++])); 
        i += 2;
        
        payloadLength -= 9; // minus skipped/not_data bytes
        payloadLength -= 2; // minus last two crc bytes
        
        while (payloadLength >= 4) {
            PayloadData data = new PayloadData();
            data.setSonicRssi(readInt(dst[i++]));
            data.setTempInCelsius((readInt(dst[i++]) >> 1) - 30);
            byte byte1 = dst[i++];
            byte shifted = (byte) (byte1 >> 2);
            data.setSonicResultCode(extractRightBits(shifted, 4));
            data.setDistance(extractRightBits(byte1, 1)*256 + readInt(dst[i++]));
            
            request.addPayloadData(data);
            
            payloadLength -= 4; //
        }
        
        return request;
    }
    
    private int readInt(byte b) {
        return (int) b & 0xff;
    }
    
    private List<String> getMatchingResult(byte crByte, String[] values) {
        String bin = String.format("%8s", Integer.toBinaryString(crByte & 0xFF)).replace(' ', '0');
        List<String> result = new ArrayList<String>();
        char arr[] = bin.toCharArray();
        for (int i = 0; i < arr.length; ++i) {
            if (arr[i] == '1') {
                result.add(values[i]);
            }
        }
        return result;
    }
    
    private int extractRightBits(byte x, int numBits) {
        if (numBits < 1) {
            return 0;
        }
        if (numBits > 32) {
            return x;
        }
        int mask = (1 << numBits) - 1;
        return x & mask;
    }
    
    private int calculateLoggerSpeed(byte b) {
        if (b == 0) {
            return 1;
        }
        int loggerSpeed = extractRightBits(b, 7);
        return loggerSpeed * 15;
    }
}
