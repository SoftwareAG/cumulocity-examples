package c8y.trackeragent.tracker;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import c8y.trackeragent.protocol.TrackingProtocol;

@Component
public class BaseTracker {
    
    ListableBeanFactory beanFactory;
    
    @Autowired
    public BaseTracker(ListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
    
    public ConnectedTracker getTrackerForTrackingProtocol (TrackingProtocol trackingProtocol) {
        ConnectedTracker tracker = beanFactory.getBean(trackingProtocol.getTrackerClass());
        return tracker;
    }

}
