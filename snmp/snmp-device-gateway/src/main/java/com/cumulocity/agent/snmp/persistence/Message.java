/*
 * Copyright © 2012 - 2017 Cumulocity GmbH.
 * Copyright © 2017 - 2020 Software AG, Darmstadt, Germany and/or its licensors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
