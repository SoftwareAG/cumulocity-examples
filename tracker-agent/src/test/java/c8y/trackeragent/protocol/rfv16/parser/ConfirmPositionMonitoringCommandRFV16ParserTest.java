package c8y.trackeragent.protocol.rfv16.parser;

import static c8y.trackeragent.protocol.rfv16.RFV16Constants.CONNECTION_PARAM_CONTROL_COMMANDS_SENT;
import static org.fest.assertions.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.protocol.rfv16.RFV16ParserTestSupport;
import c8y.trackeragent.utils.message.TrackerMessage;

public class ConfirmPositionMonitoringCommandRFV16ParserTest extends RFV16ParserTestSupport {

    private ConfirmPositionMonitoringCommandRFV16Parser parser;

    @Before
    public void init() {
        parser = new ConfirmPositionMonitoringCommandRFV16Parser(trackerAgent, serverMessages, alarmService);
    }

    @Test
    public void shouldParseImei() throws Exception {
        TrackerMessage msg = deviceMessages.confirmPositionMonitoringCommand("DB", IMEI);

        String actualImei = parser.parse(msg.asArray());

        assertThat(actualImei).isEqualTo(IMEI);
    }

    @Test
    public void shouldSetupConnectionParam() throws Exception {
        TrackerMessage msg = deviceMessages.confirmPositionMonitoringCommand("DB", IMEI);
        ReportContext reportContext = new ReportContext(connectionDetails, msg.asArray());

        boolean result = parser.onParsed(reportContext);

        assertThat(result).isTrue();
        assertThat(reportContext.isConnectionFlagOn(CONNECTION_PARAM_CONTROL_COMMANDS_SENT)).isTrue();
    }

}
