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

package com.cumulocity.agents.mps.emulator.model;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MeterReadOut {

    private static BigDecimal reactivePosCap = new BigDecimal(Math.random());

    private static BigDecimal reactivePosInd = new BigDecimal(Math.random());

    private static BigDecimal reactiveNegCap = new BigDecimal(Math.random());

    private static BigDecimal reactiveNegInd = new BigDecimal(Math.random());

    private static BigDecimal activePos1 = new BigDecimal(Math.random());

    private static BigDecimal activePos2 = new BigDecimal(Math.random());

    private static final BigDecimal INCREASE_RATE = new BigDecimal("0.003");

    public static Map<String, List<Map<String, Object>>> createReading() {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("status", Boolean.TRUE);
        properties.put("fault", "A00");
        properties.put("meterDateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        properties.put("devDate", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        properties.put("devTime", new SimpleDateFormat("HH:mm:ss").format(new Date()));
        properties.put("serialNum", "8306992");
        properties.put("paramVersion", "1A72");
        properties.put("id", "");
        properties.put("worktime", workTime());
        properties.put("active", active(activeNegative(), activePositive()));

        properties.put(
                "reactive",
                append(capIndWithTotal(reactiveMeas(reactiveNegCap, "-Rc"), reactiveMeas(reactiveNegInd, "-Ri")),
                        capIndWithTotal(reactiveMeas(reactivePosCap, "+Rc"), reactiveMeas(reactivePosInd, "+Ri"))));

        properties.put("maxdemand", maxDemand());

        reactivePosCap = reactivePosCap.add(INCREASE_RATE);
        reactivePosInd = reactivePosInd.add(INCREASE_RATE);
        reactiveNegCap = reactivePosCap.add(INCREASE_RATE);
        reactiveNegInd = reactivePosInd.add(INCREASE_RATE);

        activePos1 = activePos1.add(INCREASE_RATE);
        activePos2 = activePos2.add(INCREASE_RATE);

        return singletonMap("result", singletonList(properties));
    }

    private static Map<String, Object> maxDemand() {
        Map<String, Object> maxDemand = new HashMap<String, Object>();
        maxDemand.put("active", maxDemandActive());
        maxDemand.put("reactive", maxDemandReactive());
        return maxDemand;
    }

    private static Map<String, Object> maxDemandReactive() {
        Map<String, Object> reactive = new HashMap<String, Object>();
        reactive.put("positive", emptyReactive());
        reactive.put("negative", emptyReactive());
        return reactive;
    }

    private static HashMap<String, Object> maxDemandActive() {
        HashMap<String, Object> active = new HashMap<String, Object>();
        active.put("positive", maxDemandPositive());
        active.put("negative", singletonMap("cumulation", Collections.emptyMap()));
        return active;
    }

    private static Map<String, Object> emptyReactive() {
        Map<String, Object> emptyReactive = new HashMap<String, Object>();
        emptyReactive.put("capacitive", singletonMap("cumulation", Collections.emptyMap()));
        emptyReactive.put("total", emptyMap());
        emptyReactive.put("inductive", singletonMap("cumulation", Collections.emptyMap()));
        return emptyReactive;
    }

    private static Map<String, Object> maxDemandPositive() {
        Map<String, Object> maxDemandPositive = new HashMap<String, Object>();
        maxDemandPositive.put("max", new String[] { "0.024", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) });
        maxDemandPositive.put("cumulation", new HashMap<String, Object>());
        maxDemandPositive.put("last", "0.008");
        return maxDemandPositive;
    }

    private static Map<String, Object> append(Map<String, Object> negative, Map<String, Object> positive) {
        Map<String, Object> reactive = new HashMap<String, Object>();
        reactive.put("negative", negative);
        reactive.put("positive", positive);
        return reactive;
    }

    private static Map<String, Object> capIndWithTotal(Map<String, Object> capacitive, Map<String, Object> inductive) {
        Map<String, Object> positive = new HashMap<String, Object>();
        BigDecimal a = (BigDecimal) capacitive.get("total");
        BigDecimal b = (BigDecimal) inductive.get("total");
        positive.put("total", a.add(b).setScale(3, BigDecimal.ROUND_DOWN));
        positive.put("capacitive", capacitive);
        positive.put("inductive", inductive);
        return positive;
    }

    private static Map<String, Object> reactiveMeas(BigDecimal total, String type) {
        Map<String, Object> reac = new HashMap<String, Object>();
        reac.put("total", total.setScale(3, BigDecimal.ROUND_DOWN));
        reac.put("type", type);
        reac.put("cumulation", new HashMap<String, Object>());
        return reac;
    }

    private static Map<String, Object> activePositive() {
        Map<String, Object> activePositive = new HashMap<String, Object>();
        activePositive.put("type", "+A");
        activePositive.put("1", activePos1.setScale(3, BigDecimal.ROUND_DOWN));
        activePositive.put("2", activePos2.setScale(3, BigDecimal.ROUND_DOWN));
        activePositive.put("total", activePos1.add(activePos2).setScale(3, BigDecimal.ROUND_DOWN));
        return activePositive;
    }

    private static Map<String, Object> activeNegative() {
        Map<String, Object> activeNegative = new HashMap<String, Object>();
        activeNegative.put("cumulation", new HashMap<String, Object>());
        activeNegative.put("total", "0.0");
        activeNegative.put("type", "-A");
        return activeNegative;
    }

    private static Map<String, Object> active(Map<String, Object> negative, Map<String, Object> positive) {
        Map<String, Object> active = new HashMap<String, Object>();
        active.put("negative", negative);
        active.put("positive", positive);
        return active;
    }

    private static Map<String, Object> workTime() {
        Map<String, Object> workTime = new HashMap<String, Object>();
        workTime.put("1", "480");
        workTime.put("total", "4192");
        workTime.put("2", "3712");
        return workTime;
    }
}
