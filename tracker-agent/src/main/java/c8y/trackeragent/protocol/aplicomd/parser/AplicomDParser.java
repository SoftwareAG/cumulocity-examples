package c8y.trackeragent.protocol.aplicomd.parser;

import c8y.Position;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.device.TrackerDevice;
import c8y.trackeragent.protocol.TrackingProtocol;
import c8y.trackeragent.protocol.aplicomd.model.AplicomDReport;
import c8y.trackeragent.protocol.aplicomd.model.Field;
import c8y.trackeragent.service.MeasurementService;
import c8y.trackeragent.tracker.Parser;
import c8y.trackeragent.utils.ByteHelper;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.sdk.client.SDKException;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;

@Component
public class AplicomDParser implements Parser, AplicomDFragment {

    private static final int IMEI_START_BYTE = 2;
    private static final int IMEI_LENGTH = 7;
    private static final int SELECTOR_START = 11;
    private static final int SELECTOR_LENGTH = 3;

    private MeasurementService measurementService;
    private TrackerAgent trackerAgent;

    @Autowired
    public AplicomDParser(TrackerAgent trackerAgent, MeasurementService measurementService) {
        this.measurementService = measurementService;
        this.trackerAgent = trackerAgent;
    }

    @Override
    public String parse(String[] report) throws SDKException {
        String reportStr = StringUtils.join(report, TrackingProtocol.APLICOM_D.getFieldSeparator());
        byte[] reportBytes = ByteHelper.getHexBytes(reportStr);
        long imei = ByteBuffer.allocate(8)
                .put((byte)0x00)
                .put(reportBytes, IMEI_START_BYTE, IMEI_LENGTH)
                .getLong(0);
        return Long.toString(imei);
    }

    @Override
    public boolean onParsed(ReportContext reportCtx) throws SDKException {
        TrackerDevice device = trackerAgent.getOrCreateTrackerDevice(reportCtx.getImei());
        byte[] report = ByteHelper.getHexBytes(reportCtx.getReportMessage());
        int selector = ByteBuffer
                .allocate(4)
                .put((byte)0x00)
                .put(report, SELECTOR_START, SELECTOR_LENGTH)
                .getInt(0);
        byte[] snapshot = Arrays.copyOfRange(report,14,report.length);
        AplicomDReport processedReport = new AplicomDReport(selector, snapshot);

        if (processedReport.isSelected(Field.GPS_TIME)) {
            Position pos = new Position();
            pos.setLat(processedReport.getField(Field.LATITUDE).divide(new BigDecimal(1000000)));
            pos.setLng(processedReport.getField(Field.LONGTITUDE).divide(new BigDecimal(1000000)));
            device.setPosition(pos, new DateTime(processedReport.getField(Field.GPS_TIME).multiply(new BigDecimal(1000)).longValue()));
        }
        DateTime reportTime;
        if (processedReport.isSelected(Field.TIME))
            reportTime = new DateTime(processedReport.getField(Field.TIME).multiply(new BigDecimal(1000)).longValue());
        else reportTime = new DateTime();

        if (processedReport.isSelected(Field.SPEED))
            measurementService.createSpeedMeasurement(processedReport.getField(Field.SPEED), device, reportTime);

        if (processedReport.isSelected(Field.EXT_BATTERY))
            measurementService.createBatteryLevelMeasurement(processedReport.getField(Field.EXT_BATTERY), device,
                    reportTime, "mV");

        if (processedReport.isSelected(Field.MAIN_POWER)) {
            createMeasurement(device,"c8y_MainPower", new String[]{"MainPower"},
                    new BigDecimal[]{processedReport.getField(Field.MAIN_POWER)}, new String[]{"mV"}, reportTime );
        }

        if (processedReport.isSelected(Field.AD1)) {
            createMeasurement(device,"c8y_Voltage", new String[]{"Voltage1", "Voltage2", "Voltage3", "Voltage4"},
                    new BigDecimal[]{processedReport.getField(Field.AD1), processedReport.getField(Field.AD2),
                            processedReport.getField(Field.AD3), processedReport.getField(Field.AD4)},
                    new String[] {"mV", "mV", "mV", "mV"}, reportTime);
        }

        return true;
    }

    private void createMeasurement(TrackerDevice device, String type, String[] series, BigDecimal[] values, String[] units, DateTime time) {
        MeasurementRepresentation m = new MeasurementRepresentation();
        ManagedObjectRepresentation source = new ManagedObjectRepresentation();
        source.setId(device.getGId());
        m.setSource(source);
        m.setType(type);
        HashMap<String, Object> fragment = new HashMap<>();
        for(int i = 0; i<series.length && i< values.length && i<units.length; i++) {
            HashMap<String, Object> ser = new HashMap<>();
            ser.put("value", values[i]);
            ser.put("unit", units[i]);
            fragment.put(series[i], ser);
        }
        m.set(fragment, type);
        m.setDateTime(time);
        device.createMeasurement(m);
    }
}
