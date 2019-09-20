/*
 * Copyright © 2019 Software AG, Darmstadt, Germany and/or its licensors
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package c8y.example.decoders.hex.util;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

@Getter
@Setter
@Slf4j
public class Message {

    public static final int MESSAGE_LENGTH = 28;

    private boolean accelerometerTriggered;
    private boolean buttonPressed;
    private byte temperature;
    private float latitude;
    private float longtitude;
    private short batteryVoltage;
    private int RSSI;
    private short SNR;

    public Message(String hexMessage) {
        if (hexMessage.length() < MESSAGE_LENGTH) {
            log.error("Message too short: " + hexMessage);
            throw new IllegalArgumentException("Message too short: " + hexMessage);
        }
        else if (hexMessage.length() > MESSAGE_LENGTH)
            log.warn("Message too long: " + hexMessage);

        byte[] msgBytes = toByteArray(hexMessage);
        ByteBuffer byteBuffer = ByteBuffer.wrap(msgBytes, 2, 10);

        accelerometerTriggered = (msgBytes[0] & 0x40) == 0x40;
        buttonPressed = (msgBytes[0] & 0x20) == 0x20;
        temperature = msgBytes[1];
        latitude = byteBuffer.getFloat();
        longtitude = byteBuffer.getFloat();
        batteryVoltage = byteBuffer.getShort();
        RSSI = ByteBuffer.wrap(new byte[]{0, msgBytes[12]}).getShort()*-1;
        SNR = ByteBuffer.wrap(new byte[]{0, msgBytes[13]}).getShort();
    }

     static byte[] toByteArray(String hexString){
        byte[] result = new byte[hexString.length() >> 1];
        for (int i = 0; i<hexString.length(); i+=2)
            result[i >> 1] = (byte)(
                    (Character.digit(hexString.charAt(i), 16) << 4) + Character.digit(hexString.charAt(i + 1),16)
            );
        return result;
    }
}
