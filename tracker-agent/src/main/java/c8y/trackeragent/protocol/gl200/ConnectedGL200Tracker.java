package c8y.trackeragent.protocol.gl200;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import c8y.trackeragent.ConnectedTracker;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.protocol.gl200.parser.GL200Fragment;

import com.cumulocity.agent.server.context.DeviceContextService;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ConnectedGL200Tracker extends ConnectedTracker<GL200Fragment> {

    @Autowired
    public ConnectedGL200Tracker(TrackerAgent trackerAgent, DeviceContextService contextService, 
	    List<GL200Fragment> fragments) {
        super(GL200Constants.REPORT_SEP, GL200Constants.FIELD_SEP, trackerAgent, contextService, fragments);
    }

}
