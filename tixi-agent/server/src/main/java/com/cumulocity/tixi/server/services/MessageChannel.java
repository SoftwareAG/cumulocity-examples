package com.cumulocity.tixi.server.services;



public interface MessageChannel<T> {
    
    public interface MessageChannelListener<T> {
        void failed(T message);
    }
    
    void addListener(MessageChannelListener<T> listener);
    
    void send(T message);
    
    boolean isClosed();

    void close();
    
}
    
