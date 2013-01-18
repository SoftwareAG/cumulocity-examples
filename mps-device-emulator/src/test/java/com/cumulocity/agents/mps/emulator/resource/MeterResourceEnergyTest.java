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

package com.cumulocity.agents.mps.emulator.resource;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasKey;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/spring/test-mocks.xml" })
public class MeterResourceEnergyTest extends MeterResourceTestBase {

    private static final double DELTA = 0.002;

    private Map<String, Object> response;

    @Before
    public void before() throws Exception {
        // when
        response = getEnergyReadout();
    }

    @Test
    public void shouldHaveSerialNumInReadout() throws Exception {
        // then
        assertThat(response, hasKey("serialNum"));
    }

    @Test
    public void shouldHaveWorktimeInReadout() throws Exception {
        // then
        Map<String, String> worktime = new HashMap<String, String>();
        worktime.put("1", "480");
        worktime.put("total", "4192");
        worktime.put("2", "3712");
        assertThat(response, Matchers.<String, Object> hasEntry("worktime", worktime));
    }

    @Test
    public void shouldHaveActivePositiveEnergyInReadout() throws Exception {
        // then
        Map<String, Object> positive = getMeasurements("active", "positive");
        BigDecimal one = getEnergy("1", "active", "positive");
        BigDecimal two = getEnergy("2", "active", "positive");
        BigDecimal total = getEnergy("total", "active", "positive");

        assertBigDecimalEquals(one.add(two), total);
        assertThat(positive, Matchers.<String, Object> hasEntry("type", "+A"));
    }

    @Test
    public void shouldHaveActiveNegativeEnergyInReadout() throws Exception {
        // then
        Map<String, Object> negative = getMeasurements("active", "negative");
        assertThat(negative, hasKey("cumulation"));
        assertThat(negative, hasEntry("type", "-A"));
        assertThat(negative, hasEntry("total", "0.0"));
    }

    @Test
    public void shouldHaveReactiveNegativeEnergiesInReadout() throws Exception {
        // then
        shouldHaveReactiveEnergies("negative", "-");
    }

    @Test
    public void shouldHaveReactivePositiveEnergiesInReadout() throws Exception {
        // then
        shouldHaveReactiveEnergies("positive", "+");
    }

    @Test
    public void shouldIncrementEnergiesInSubsequentReadout() throws Exception {
        // given
        BigDecimal active1 = getEnergy("1", "active", "positive");
        BigDecimal active2 = getEnergy("2", "active", "positive");
        BigDecimal reacPosCap = getEnergy("total", "reactive", "positive", "capacitive");
        BigDecimal reacPosInd = getEnergy("total", "reactive", "positive", "inductive");
        BigDecimal reacNegCap = getEnergy("total", "reactive", "negative", "capacitive");
        BigDecimal reacNegInd = getEnergy("total", "reactive", "negative", "inductive");

        // when
        response = getEnergyReadout();

        // then
        assertThat(getEnergy("1", "active", "positive"), greaterThan(active1));
        assertThat(getEnergy("2", "active", "positive"), greaterThan(active2));

        assertThat(getEnergy("total", "reactive", "positive", "capacitive"), greaterThan(reacPosCap));
        assertThat(getEnergy("total", "reactive", "positive", "inductive"), greaterThan(reacPosInd));
        assertThat(getEnergy("total", "reactive", "negative", "capacitive"), greaterThan(reacNegCap));
        assertThat(getEnergy("total", "reactive", "negative", "inductive"), greaterThan(reacNegInd));
    }

    @Test
    public void shouldHaveMaxDemandActivePositive() throws Exception {
        // then
        Map<String, Object> actPos = getMeasurements("maxdemand", "active", "positive");
        assertThat(actPos, hasKey(is("max")));
        assertThat(actPos, Matchers.<String, Object> hasEntry("cumulation", emptyMap()));
        assertThat(actPos, Matchers.<String, Object> hasEntry("last", "0.008"));

    }

    @Test
    public void shouldHaveMaxDemandActiveNegative() throws Exception {
        // then
        Map<String, Object> negCumulation = getMeasurements("maxdemand", "active", "negative", "cumulation");
        assertThat(negCumulation, is(Collections.<String, Object> emptyMap()));
    }

    @Test
    public void shouldHaveEmptyMaxDemandReactive() throws Exception {
        Map<String, Object> expected = new HashMap<String, Object>();
        expected.put("capacitive", singletonMap("cumulation", emptyMap()));
        expected.put("total", emptyMap());
        expected.put("inductive", singletonMap("cumulation", emptyMap()));

        assertThat(getMeasurements("maxdemand", "reactive", "positive"), is(expected));
        assertThat(getMeasurements("maxdemand", "reactive", "negative"), is(expected));

    }

    private void shouldHaveReactiveEnergies(String type, String sign) {
        Map<String, Object> capacitive = getMeasurements("reactive", type, "capacitive");
        Map<String, Object> inductive = getMeasurements("reactive", type, "inductive");
        assertThat(capacitive, hasKey("cumulation"));
        assertThat(capacitive, hasEntry("type", sign + "Rc"));
        assertThat(inductive, hasKey("cumulation"));
        assertThat(inductive, hasEntry("type", sign + "Ri"));

        BigDecimal totalCap = (BigDecimal) capacitive.get("total");
        assertThat(totalCap, greaterThan(BigDecimal.ZERO));
        BigDecimal totalInd = (BigDecimal) inductive.get("total");
        assertThat(totalInd, greaterThan(BigDecimal.ZERO));

        BigDecimal total = getEnergy("total", "reactive", type);
        assertBigDecimalEquals(totalInd.add(totalCap), total);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Map<String, Object> getEnergyReadout() throws Exception {
        Map<String, Object> r = issueGet("meter/readout/1/en");
        return (Map<String, Object>) ((List) r.get("result")).get(0);
    }

    private BigDecimal getEnergy(String key, String... inners) {
        Map<String, Object> measurements = getMeasurements(inners);
        return (BigDecimal) measurements.get(key);

    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getMeasurements(String... inners) {
        Map<String, Object> act = response;
        for (String node : inners) {
            act = (Map<String, Object>) act.get(node);
        }
        return act;
    }

    private static void assertBigDecimalEquals(BigDecimal add, BigDecimal total) {
        assertEquals(add.doubleValue(), total.doubleValue(), DELTA);
    }

    private static Matcher<Map<String, Object>> hasEntry(String key, String value) {
        return Matchers.<String, Object> hasEntry(key, value);
    }
}
