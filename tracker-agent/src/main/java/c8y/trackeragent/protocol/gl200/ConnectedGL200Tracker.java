package c8y.trackeragent.protocol.gl200;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import c8y.trackeragent.protocol.TrackingProtocol;
import c8y.trackeragent.protocol.gl200.parser.GL200Fragment;
import c8y.trackeragent.tracker.BaseConnectedTracker;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ConnectedGL200Tracker extends BaseConnectedTracker<GL200Fragment> {

    @Override
    public TrackingProtocol getTrackingProtocol() {
        return TrackingProtocol.GL200;
    }


}
