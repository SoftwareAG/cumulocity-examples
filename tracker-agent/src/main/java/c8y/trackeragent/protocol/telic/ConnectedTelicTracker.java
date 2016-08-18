package c8y.trackeragent.protocol.telic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import c8y.trackeragent.protocol.TrackingProtocol;
import c8y.trackeragent.protocol.telic.parser.TelicFragment;
import c8y.trackeragent.tracker.BaseConnectedTracker;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ConnectedTelicTracker extends BaseConnectedTracker<TelicFragment> {

    protected static Logger logger = LoggerFactory.getLogger(ConnectedTelicTracker.class);
    
    public ConnectedTelicTracker() {
        super(new TelicReportSplitter());
    }

    @Override
    public TrackingProtocol getTrackingProtocol() {
        return TrackingProtocol.TELIC;
    }

}
