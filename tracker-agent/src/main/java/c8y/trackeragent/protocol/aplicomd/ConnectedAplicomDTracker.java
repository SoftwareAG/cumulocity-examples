/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

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
