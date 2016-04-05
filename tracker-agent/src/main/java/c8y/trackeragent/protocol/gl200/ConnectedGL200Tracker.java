package c8y.trackeragent.protocol.gl200;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import c8y.trackeragent.ConnectedTracker;
import c8y.trackeragent.protocol.gl200.parser.GL200Fragment;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ConnectedGL200Tracker extends ConnectedTracker<GL200Fragment> {

	public ConnectedGL200Tracker() {
		super(GL200Constants.REPORT_SEP, GL200Constants.FIELD_SEP);
	}

}
