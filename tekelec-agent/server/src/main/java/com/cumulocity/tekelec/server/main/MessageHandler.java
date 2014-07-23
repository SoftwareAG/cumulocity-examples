package com.cumulocity.tekelec.server.main;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.*;

import com.cumulocity.model.measurement.MeasurementValue;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.tekelec.TEK586MO;
import com.cumulocity.tekelec.TEK586Measurement;

public class MessageHandler extends ChannelInboundHandlerAdapter {
    
    private static final Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    private String[] contactReasons = { "DynLim2 Status", "DymLin1 Status", "TSP Requested", "Re-boot", "Manual", "Server Requested",
            "Alarm", "Scheduled" };

    private String[] alarmAndStatuses = { "GO Active", "BOR Reset", "WDT Reset", "Limp Along RTC", "Bund Status Closed", "Limits 3 Status",
            "Limits 2 Status", "Limits 1 Status" };
    
    private Map<String, DeviceService> devicesService = new HashMap<String, DeviceService>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf) msg;
        try {
                printRequest(in);
                
                int productType = readInt(in);
                int hardwareRevision = readInt(in);
                int firmwareRevision = readInt(in);
                byte contactReasonByte = in.readByte();
                List<String> contactReason = getMatchingResult(contactReasonByte, contactReasons);
                byte alarmAndStatusByte = in.readByte();
                List<String> alarmAndStatus = getMatchingResult(alarmAndStatusByte, alarmAndStatuses);
                byte gsmRssi = in.readByte();
                BigDecimal battery = BigDecimal.valueOf(extractRightBits(in.readByte(), 5) + 30).divide(BigDecimal.valueOf(10), 2, RoundingMode.CEILING);
                String imei = "";
                for (int i = 0 ; i < 8 ; i++) {
                    imei += String.format("%02X", readInt(in));
                }
                in.skipBytes(11);
                int auxRssi = readInt(in);
                int tempInCelsius = (readInt(in) >> 1) - 30;
                byte byte1 = in.readByte();
                byte shifted = (byte) (byte1 >> 2);
                int sonitResultCode = extractRightBits(shifted, 4);
                int distance = extractRightBits(byte1, 1)*256 + readInt(in);
                
                logger.info("productType " + productType);
                logger.info("hardwareRevision " + hardwareRevision);
                logger.info("firmwareRevision " + firmwareRevision);
                logger.info("contactReason " + contactReason);
                logger.info("alarmAndStatus " + alarmAndStatus);
                logger.info("gsmRssi " + gsmRssi);
                logger.info("battery " + battery);
                logger.info("imei " + imei);
                logger.info("auxRssi " + auxRssi);
                logger.info("tempInCelsius " + tempInCelsius);
                logger.info("sonitResultCode " + sonitResultCode);
                logger.info("distance " + distance);
                
                registerDeviceService(imei);
                ManagedObjectRepresentation device = createDevice(hardwareRevision, firmwareRevision, contactReason, alarmAndStatus, imei);
                createMeasurement(imei, battery, auxRssi, tempInCelsius, distance, device);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    private void printRequest(ByteBuf in) {
        ByteBuf copy = in.copy();
        while(copy.isReadable()) {
            logger.info("" + readInt(copy));
        }
    }

    private void createMeasurement(String imei, BigDecimal battery, int auxRssi, int tempInCelsius, int distance, ManagedObjectRepresentation device) {
        DeviceService deviceService = devicesService.get(imei);
        MeasurementRepresentation measurement = new MeasurementRepresentation();
        measurement.setTime(new Date());
        measurement.setSource(device);
        measurement.setType("c8y_TekelecMeasurement");
        measurement.set(distanceMeasurement(distance));
        measurement.set(temperatureMeasurement(tempInCelsius));
        measurement.set(batteryMeasurement(battery));
        measurement.setProperty("c8y_TEK586", new TEK586Measurement(auxRssi));
        deviceService.createMeasurement(measurement);
    }

    private ManagedObjectRepresentation createDevice(int hardwareRevision, int firmwareRevision, List<String> contactReason,
            List<String> alarmAndStatus, String imei) {
        DeviceService deviceService = devicesService.get(imei);
        ManagedObjectRepresentation device = deviceService.register(imei);
        ManagedObjectRepresentation update = new ManagedObjectRepresentation();
        update.setId(device.getId());
        update.set(new Hardware("TEK586", imei, String.valueOf(hardwareRevision)));
        update.set(new Firmware("TEK586", String.valueOf(firmwareRevision), null));
        update.setProperty("c8y_TEK586", new TEK586MO(contactReason, alarmAndStatus));
        deviceService.update(update);
        return device;
    }

    private void registerDeviceService(String imei) {
        if (!devicesService.containsKey(imei)) {
            devicesService.put(imei, new DeviceService(imei));
        }
    }

    private Battery batteryMeasurement(BigDecimal batteryValue) {
        Battery battery = new Battery();
        MeasurementValue level = new MeasurementValue();
        level.setValue(batteryValue);
        level.setUnit("V");
        battery.setLevel(level);
        return battery;
    }

    private TemperatureMeasurement temperatureMeasurement(int value) {
        TemperatureMeasurement temperatureMeasurement = new TemperatureMeasurement();
        temperatureMeasurement.setTemperature(BigDecimal.valueOf(value));
        return temperatureMeasurement;
    }

    private DistanceMeasurement distanceMeasurement(int value) {
        DistanceMeasurement distanceMeasurement = new DistanceMeasurement();
        MeasurementValue measurement = new MeasurementValue("m");
        measurement.setValue(BigDecimal.valueOf(value));
        distanceMeasurement.setDistance(measurement);
        return distanceMeasurement;
    }
    
    private int readInt(ByteBuf in) {
        return (int) in.readByte() & 0xff;
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

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}
