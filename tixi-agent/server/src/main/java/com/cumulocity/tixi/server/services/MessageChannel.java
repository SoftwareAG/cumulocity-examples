package com.cumulocity.tixi.server.services;

import c8y.MeasurementRequestOperation;


public interface MessageChannel<T> {

    void send(MessageChannelContext context, MeasurementRequestOperation measurementRequest);
    
}
    
