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

import org.junit.Test;

import static org.junit.Assert.*;

public class MessageTest {

    private final static String MESSAGE_STR = "6017424CE92A40D8C0260D13620F";
    private final static String MESSAGE_STR_FLIPPED = "9FE8BDB316D5BF273FD9F2EC9DF0";
    private final static double COMPARISON_DELTA = 0.00000001;

    @Test
    public void toByteArray() {
        byte[] expectedResult = new byte[]{
                (byte)0x60,
                (byte)0x17,
                (byte)0x42, (byte)0x4C, (byte)0xE9, (byte)0x2A,
                (byte)0x40, (byte)0xD8, (byte)0xC0, (byte)0x26,
                (byte)0x0D, (byte)0x13,
                (byte)0x62,
                (byte)0x0F
        };
        byte[] result = Message.toByteArray(MESSAGE_STR);
        assertEquals(expectedResult.length, result.length);
        for(int i = 0; i < result.length; i++)
            assertEquals(expectedResult[i], result[i]);
    }

    @Test
    public void constructorTest() {
        Message msg = new Message(MESSAGE_STR);

        assertTrue(msg.isAccelerometerTriggered());
        assertTrue(msg.isButtonPressed());
        assertEquals(23, msg.getTemperature());
        assertEquals(51.2277f, msg.getLatitude(), COMPARISON_DELTA);
        assertEquals(6.7734556f, msg.getLongtitude(), COMPARISON_DELTA);
        assertEquals(3347, msg.getBatteryVoltage());
        assertEquals(-98, msg.getRSSI());
        assertEquals(15, msg.getSNR());
    }

    @Test
    public void constructorTestFlipped() {
        Message msg = new Message(MESSAGE_STR_FLIPPED);

        assertFalse(msg.isAccelerometerTriggered());
        assertFalse(msg.isButtonPressed());
        assertEquals(-24, msg.getTemperature());
        assertEquals(-0.08744589f, msg.getLatitude(), COMPARISON_DELTA);
        assertEquals(-0.653318f, msg.getLongtitude(), COMPARISON_DELTA);
        assertEquals(-3348, msg.getBatteryVoltage());
        assertEquals(-157, msg.getRSSI());
        assertEquals(240, msg.getSNR());
    }
}