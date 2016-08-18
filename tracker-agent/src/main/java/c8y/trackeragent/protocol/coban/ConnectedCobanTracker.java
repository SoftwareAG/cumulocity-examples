package c8y.trackeragent.protocol.coban;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import c8y.trackeragent.protocol.TrackingProtocol;
import c8y.trackeragent.protocol.coban.parser.CobanFragment;
import c8y.trackeragent.tracker.BaseConnectedTracker;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ConnectedCobanTracker extends BaseConnectedTracker<CobanFragment> {
    
    @Override
    public TrackingProtocol getTrackingProtocol() {
        return TrackingProtocol.COBAN;
    }
    
    

}
