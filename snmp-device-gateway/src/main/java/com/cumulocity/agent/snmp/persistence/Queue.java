package com.cumulocity.agent.snmp.persistence;

import java.util.Collection;

public interface Queue extends AutoCloseable {
    String getName();

    void enqueue(String message);

    String peek();

    String dequeue();

    int drainTo(Collection<String> collection, int maxElements);
}
