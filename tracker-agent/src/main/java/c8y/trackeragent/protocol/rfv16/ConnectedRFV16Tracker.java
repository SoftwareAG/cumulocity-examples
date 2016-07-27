package c8y.trackeragent.protocol.rfv16;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import c8y.trackeragent.protocol.rfv16.parser.RFV16Fragment;
import c8y.trackeragent.tracker.BaseConnectedTracker;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ConnectedRFV16Tracker extends BaseConnectedTracker<RFV16Fragment> {

	public ConnectedRFV16Tracker() {
		super(RFV16Constants.REPORT_SEP, RFV16Constants.FIELD_SEP);
	}

}
