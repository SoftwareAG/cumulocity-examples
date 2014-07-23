package com.cumulocity.tixi.server.services;



public interface MessageChannel<T> {
    public interface MessageChannelListener<T> {
        void failed(T message);
        void close();
    }
    
    void send(MessageChannelListener<T> context, T message);
    
}
    
