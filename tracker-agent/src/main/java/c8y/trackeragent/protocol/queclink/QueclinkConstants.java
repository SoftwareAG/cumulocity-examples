/*
 * Copyright (C) 2013 Cumulocity GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of 
 * this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package c8y.trackeragent.protocol.queclink;

import java.util.HashMap;

public interface QueclinkConstants {
    static final String GL500_ID = "11";
    static final String GL505_ID = "40";
    static final String GV500_ID1 = "1F";
    static final String GV500_ID2 = "36";
    static final String GL300_ID = "30";
    static final String GL200_ID = "02";
    static final String GV75_ID = "3C";
    
    static final HashMap<String, String[]> queclinkProperties = new HashMap<String, String[]>() {/**
         * 
         */
        private static final long serialVersionUID = -8919978143488659000L;

    {
        put(GL500_ID, new String[]{"gl500","gl500"}); //protocol, default password
        put(GL505_ID, new String[]{"gl505","gl500"}); //protocol, default password
        put(GV500_ID1, new String[]{"gv500","gv500"}); //protocol, default password
        put(GV500_ID2, new String[]{"gv500","gv500"}); //protocol, default password
        put(GL300_ID, new String[]{"gl300","gl300"}); //protocol, default password
        put(GL200_ID, new String[]{"gl200","gl200"}); //protocol, default password
        put(GV75_ID, new String[]{"gv75","gv75"}); //protocol, default password
    }};
    
}
