package c8y.trackeragent.protocol.queclink;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import c8y.trackeragent.protocol.TrackingProtocol;
import c8y.trackeragent.protocol.queclink.parser.QueclinkFragment;
import c8y.trackeragent.tracker.BaseConnectedTracker;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ConnectedQueclinkTracker extends BaseConnectedTracker<QueclinkFragment> {

    @Override
    public TrackingProtocol getTrackingProtocol() {
        return TrackingProtocol.QUECLINK;
    }


}
