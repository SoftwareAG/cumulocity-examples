/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

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
