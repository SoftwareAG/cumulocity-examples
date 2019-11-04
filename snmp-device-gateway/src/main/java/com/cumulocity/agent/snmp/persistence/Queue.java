package com.cumulocity.agent.snmp.persistence;

import java.util.Collection;

public interface Queue extends AutoCloseable {
    String getName();

    void enqueue(Message message);

    void backout(Message message);

    Message peek();

    Message dequeue();

    int drainTo(Collection<Message> collection, int maxElements);
}
