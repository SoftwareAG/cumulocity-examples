package com.cumulocity.tixi.server.services;



public interface MessageChannel<T> {

    void send(MessageChannelContext context, T message);
    
}
    
