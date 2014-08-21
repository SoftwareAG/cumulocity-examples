package com.cumulocity.tekelec.server.main;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.*;

import com.cumulocity.model.measurement.MeasurementValue;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.tekelec.PayloadData;
import com.cumulocity.tekelec.TEK586MO;
import com.cumulocity.tekelec.TEK586Measurement;
import com.cumulocity.tekelec.TekelecRequest;
import com.cumulocity.tekelec.server.main.DeviceService.NotInitizializedException;
import com.google.common.base.Charsets;

public class MessageHandler extends ChannelInboundHandlerAdapter {
    
    private static final byte[] ackResponse = "R1=80".getBytes(Charsets.US_ASCII);
    
    private static final Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    private Map<String, DeviceService> devicesService = new HashMap<String, DeviceService>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        TekelecRequest tekelecRequest = (TekelecRequest) msg;
        try {
                registerDeviceService(tekelecRequest.getImei());
                ManagedObjectRepresentation device = createDevice(tekelecRequest);
                
                int dataIndex = 0;
                for (PayloadData data : tekelecRequest.getPayloadData()) {
                    createMeasurement(tekelecRequest, data, dataIndex, device);
                    dataIndex++;
                }
                sendAckResponseIfRequired(tekelecRequest.getContactReason(), ctx);
        } catch (NotInitizializedException e) {
            logger.info(e.getMessage());
        } finally {
            ReferenceCountUtil.release(msg);
            ctx.close();
        }
    }

    private void createMeasurement(TekelecRequest tekelecRequest, PayloadData data, int dataIndex, ManagedObjectRepresentation device) {
        DeviceService deviceService = devicesService.get(tekelecRequest.getImei());
        MeasurementRepresentation measurement = new MeasurementRepresentation();
        measurement.setTime(new DateTime().minusMinutes(dataIndex * tekelecRequest.getLoggerSpeed()).toDate());
        measurement.setSource(device);
        measurement.setType("c8y_TekelecMeasurement");
        measurement.set(distanceMeasurement(data.getDistance()));
        measurement.set(temperatureMeasurement(data.getTempInCelsius()));
        measurement.set(batteryMeasurement(tekelecRequest.getBattery()));
        measurement.set(signalStrengthMeasurement(tekelecRequest.getGsmRssi()));
        measurement.setProperty("c8y_TEK586", new TEK586Measurement(data.getSonicRssi(), data.getSonicResultCode()));
        deviceService.createMeasurement(measurement);
    }

    private SignalStrength signalStrengthMeasurement(int gsmRssi) {
        SignalStrength signalStrength = new SignalStrength();
        signalStrength.putRawRssi(gsmRssi);
        return signalStrength;
    }

    private ManagedObjectRepresentation createDevice(TekelecRequest tekelecRequest) throws NotInitizializedException {
        String imei = tekelecRequest.getImei();
        DeviceService deviceService = devicesService.get(imei);
        ManagedObjectRepresentation device = deviceService.register(imei);
        ManagedObjectRepresentation update = new ManagedObjectRepresentation();
        update.setId(device.getId());
        update.set(new Hardware("TEK586", imei, String.valueOf(tekelecRequest.getHardwareRevision())));
        update.set(new Firmware("TEK586", String.valueOf(tekelecRequest.getFirmwareRevision()), null));
        update.setProperty("c8y_TEK586", new TEK586MO(tekelecRequest.getContactReason(), tekelecRequest.getAlarmAndStatus()));
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
        MeasurementValue measurement = new MeasurementValue("cm");
        measurement.setValue(BigDecimal.valueOf(value));
        distanceMeasurement.setDistance(measurement);
        return distanceMeasurement;
    }

    private void sendAckResponseIfRequired(List<String> contactReason, ChannelHandlerContext ctx) {
        if (contactReason.contains("Scheduled") || contactReason.contains("Alarm")) {
            ctx.write(ackResponse);
            ctx.flush();
        }
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}
