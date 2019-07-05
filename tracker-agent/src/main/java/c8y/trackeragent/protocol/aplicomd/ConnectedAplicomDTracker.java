package c8y.trackeragent.protocol.aplicomd;

import c8y.trackeragent.protocol.TrackingProtocol;
import c8y.trackeragent.protocol.aplicomd.parser.AplicomDFragment;
import c8y.trackeragent.tracker.BaseConnectedTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ConnectedAplicomDTracker extends BaseConnectedTracker<AplicomDFragment> {

    protected static Logger logger = LoggerFactory.getLogger(ConnectedAplicomDTracker.class);

    public ConnectedAplicomDTracker(){
        super(new AplicomDReportSplitter());
    }

    @Override
    public TrackingProtocol getTrackingProtocol() {
        return TrackingProtocol.APLICOM_D;
    }
}
