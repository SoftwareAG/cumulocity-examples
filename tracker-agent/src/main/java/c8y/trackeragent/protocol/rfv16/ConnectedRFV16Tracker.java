package c8y.trackeragent.protocol.rfv16;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import c8y.trackeragent.ConnectedTracker;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.protocol.rfv16.parser.RFV16Fragment;
import c8y.trackeragent.service.AlarmService;

import com.cumulocity.agent.server.context.DeviceContextService;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ConnectedRFV16Tracker extends ConnectedTracker<RFV16Fragment> {

    @Autowired
    public ConnectedRFV16Tracker(TrackerAgent trackerAgent, DeviceContextService contextService, AlarmService alarmService, 
	    List<RFV16Fragment> fragments) {
	super(RFV16Constants.REPORT_SEP, RFV16Constants.FIELD_SEP, trackerAgent, contextService, fragments);
    }

}
