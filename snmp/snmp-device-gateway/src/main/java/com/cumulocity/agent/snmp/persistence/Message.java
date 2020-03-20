package com.cumulocity.agent.snmp.persistence;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.openhft.chronicle.core.io.IORuntimeException;
import net.openhft.chronicle.wire.*;

@EqualsAndHashCode
public class Message implements ReadMarshallable, WriteMarshallable {

    @Getter
    private String payload;

    @Getter
    private short backoutCount;


    Message(WireIn wire) {
        readMarshallable(wire);
    }

    public Message(String payload) {
        this.payload = payload;
    }

    public Message(String payload, short backoutCount) {
        this(payload);
        this.backoutCount = backoutCount;
    }

    void incrementBackoutCount() {
        backoutCount++;
    }

    @Override
    public void readMarshallable(WireIn wire) throws IORuntimeException {
        ValueIn valueIn = wire.read();

        backoutCount = valueIn.int16();
        payload = valueIn.text();
    }

    @Override
    public void writeMarshallable(WireOut wire) {
        ValueOut valueOut = wire.getValueOut();

        valueOut.int16(backoutCount);
        valueOut.text(payload);
    }
}
