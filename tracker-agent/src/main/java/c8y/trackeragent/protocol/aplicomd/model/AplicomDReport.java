/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.protocol.aplicomd.model;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class AplicomDReport {

    private int offset;
    private int selector;
    private byte[] snapshot;
    private HashMap<String, BigDecimal> data;

    public boolean isSelected(Field field){
        return (selector & field.getFieldSelector()) == field.getFieldSelector();
    }

    private int readIntField(Field field) {
        if (isSelected(field)) {
            ByteBuffer buffer = ByteBuffer.allocate(Integer.SIZE/8);
            for (int i = field.getLength(); i < Integer.SIZE / 8; i++)
                buffer.put((byte) 0x00);
            buffer.put(this.snapshot, this.offset, field.getLength());
            this.offset += field.getLength();
            return buffer.getInt(0);
        }
        return 0;
    }

    public AplicomDReport(int selector, byte[] snapshot) {
        this.selector = selector;
        this.snapshot = snapshot;
        this.offset = 0;
        this.data = new HashMap<>();
        for (Field field: Field.values())
            data.put(field.toString(), new BigDecimal(readIntField(field)));
    }

    public BigDecimal getField(Field field) {
        return data.get(field.toString());
    }
}
