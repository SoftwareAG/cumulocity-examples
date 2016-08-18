package c8y.trackeragent.protocol.mt90g;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import c8y.trackeragent.protocol.TrackingProtocol;
import c8y.trackeragent.protocol.mt90g.parser.MT90GFragment;
import c8y.trackeragent.tracker.BaseConnectedTracker;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ConnectedMT90GTracker extends BaseConnectedTracker<MT90GFragment> {

    @Override
    public TrackingProtocol getTrackingProtocol() {
        return TrackingProtocol.MT90G;
    }


}
