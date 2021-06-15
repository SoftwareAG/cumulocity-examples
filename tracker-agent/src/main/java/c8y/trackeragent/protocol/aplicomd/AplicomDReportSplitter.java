/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.protocol.aplicomd;

import c8y.trackeragent.protocol.TrackingProtocol;
import c8y.trackeragent.tracker.BaseReportSplitter;
import c8y.trackeragent.utils.ByteHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AplicomDReportSplitter extends BaseReportSplitter {

    private static final int HEADER_SIZE = 14;
    private static final int SNAPSHOT_MSB = 9;
    private static final int SNAPSHOT_LENGHT = 2;

    private static Logger logger = LoggerFactory.getLogger(AplicomDReportSplitter.class);

    AplicomDReportSplitter() {
        super(TrackingProtocol.APLICOM_D.getReportSeparator());
    }

    @Override
    public List<String> split(byte[] reports) {
        ArrayList<String> result = new ArrayList<>();
        int i=0;
        String hexReport=getHexReport(reports, i);
        while (!"".equals(hexReport)) {
            result.add(hexReport);
            i+=hexReport.length()/2;
            hexReport=getHexReport(reports, i);
        }
        return result;
    }

    private String getHexReport(byte[] reports, int startingByte) {
        if (startingByte + SNAPSHOT_MSB + SNAPSHOT_LENGHT > reports.length)
            return "";
        int snapshotLenght = ByteBuffer
                .allocate(4)
                .put((byte)0x00)
                .put((byte)0x00)
                .put(reports, startingByte+SNAPSHOT_MSB, SNAPSHOT_LENGHT)
                .getInt(0);
        int endingByte = startingByte + HEADER_SIZE + snapshotLenght;
        if(endingByte <=  reports.length)
            return ByteHelper.toHexString(Arrays.copyOfRange(reports, startingByte, endingByte));
        else if (endingByte >  reports.length + 1)
            logger.warn("Error decoding report from message 0x{}, startingByte:{}, endingByte:{}",
                    ByteHelper.toHexString(reports), startingByte, endingByte);
        return "";
    }
}
