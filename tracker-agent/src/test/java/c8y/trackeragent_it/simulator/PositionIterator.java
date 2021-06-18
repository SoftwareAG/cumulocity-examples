/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, 
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent_it.simulator;

import static c8y.trackeragent.utils.Positions.aPosition;

import java.math.BigDecimal;

import c8y.Position;

public class PositionIterator {
    
    private Double lat;
    private final Double lng;
    private final Double latStep;
    
    public PositionIterator(double lat, double lng, double latStep) {
        this.lat = lat;
        this.lng = lng;
        this.latStep = latStep;
    }
    
    public Position next() {
        lat += latStep;
        return current();
    }
    
    public Position current() {
        return aPosition(BigDecimal.valueOf(lat), BigDecimal.valueOf(lng), BigDecimal.valueOf(0));
    }
}
