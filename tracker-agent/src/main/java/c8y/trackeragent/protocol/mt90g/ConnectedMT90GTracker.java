package c8y.trackeragent.protocol.mt90g;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import c8y.trackeragent.protocol.mt90g.parser.MT90GFragment;
import c8y.trackeragent.tracker.BaseConnectedTracker;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ConnectedMT90GTracker extends BaseConnectedTracker<MT90GFragment> {

    public ConnectedMT90GTracker() {
        super(MT90GConstants.REPORT_SEP, MT90GConstants.FIELD_SEP);
    }

}
