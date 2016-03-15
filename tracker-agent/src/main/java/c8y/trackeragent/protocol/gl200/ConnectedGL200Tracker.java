package c8y.trackeragent.protocol.gl200;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.cumulocity.agent.server.context.DeviceContextService;

import c8y.trackeragent.ConnectedTracker;
import c8y.trackeragent.devicebootstrap.DeviceBootstrapProcessor;
import c8y.trackeragent.devicebootstrap.DeviceCredentialsRepository;
import c8y.trackeragent.protocol.gl200.parser.GL200Fragment;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ConnectedGL200Tracker extends ConnectedTracker<GL200Fragment> {

	@Autowired
	public ConnectedGL200Tracker(DeviceContextService contextService,
			DeviceBootstrapProcessor bootstrapProcessor, List<GL200Fragment> fragments,
			DeviceCredentialsRepository credentialsRepository) {
		super(GL200Constants.REPORT_SEP, GL200Constants.FIELD_SEP, contextService, bootstrapProcessor,
				credentialsRepository, fragments);
	}

}
