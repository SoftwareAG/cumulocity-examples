package c8y.trackeragent.protocol.coban;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import c8y.trackeragent.ConnectedTracker;
import c8y.trackeragent.protocol.coban.parser.CobanFragment;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ConnectedCobanTracker extends ConnectedTracker<CobanFragment> {
    
    public ConnectedCobanTracker() {
        super(CobanConstants.REPORT_SEP, CobanConstants.FIELD_SEP);
    }

}
