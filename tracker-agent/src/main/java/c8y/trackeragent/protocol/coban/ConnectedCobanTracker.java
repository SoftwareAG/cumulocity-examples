package c8y.trackeragent.protocol.coban;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.cumulocity.agent.server.context.DeviceContextService;

import c8y.trackeragent.ConnectedTracker;
import c8y.trackeragent.devicebootstrap.DeviceBootstrapProcessor;
import c8y.trackeragent.devicebootstrap.DeviceCredentialsRepository;
import c8y.trackeragent.protocol.coban.parser.CobanFragment;
import c8y.trackeragent.service.AlarmService;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ConnectedCobanTracker extends ConnectedTracker<CobanFragment> {
    
    @Autowired
    public ConnectedCobanTracker(DeviceContextService contextService, 
    		DeviceBootstrapProcessor bootstrapProcessor, AlarmService alarmService, 
	    List<CobanFragment> fragments, DeviceCredentialsRepository credentialsRepository) {
        super(CobanConstants.REPORT_SEP, CobanConstants.FIELD_SEP, contextService, bootstrapProcessor, credentialsRepository, fragments);
    }

}
