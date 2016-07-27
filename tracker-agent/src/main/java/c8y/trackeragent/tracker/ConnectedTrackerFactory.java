package c8y.trackeragent.tracker;

import c8y.trackeragent.server.TrackerServerEvent;
import c8y.trackeragent.server.TrackerServerEvent.ReadDataEvent;

public interface ConnectedTrackerFactory {
    
    ConnectedTracker create(ReadDataEvent readData) throws Exception;
    
}
