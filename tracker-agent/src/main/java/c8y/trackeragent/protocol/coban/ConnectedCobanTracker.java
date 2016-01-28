package c8y.trackeragent.protocol.coban;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import c8y.trackeragent.ConnectedTracker;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.protocol.coban.parser.CobanFragment;
import c8y.trackeragent.service.AlarmService;

import com.cumulocity.agent.server.context.DeviceContextService;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ConnectedCobanTracker extends ConnectedTracker<CobanFragment> {
    
    @Autowired
    public ConnectedCobanTracker(TrackerAgent trackerAgent, DeviceContextService contextService, AlarmService alarmService, 
	    List<CobanFragment> fragments) {
        super(CobanConstants.REPORT_SEP, CobanConstants.FIELD_SEP, trackerAgent, contextService, fragments);
    }

}
