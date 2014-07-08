package com.cumulocity.tixi.server.services;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.stereotype.Component;

@Component
public class TixiOperationsQueue <T> {

    private final BlockingQueue<T> queue = new LinkedBlockingQueue<T>();
    
    public void put(T element) {
        try {
            queue.put(element);
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }
    }
    
    public T take() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }
    }
    
}
