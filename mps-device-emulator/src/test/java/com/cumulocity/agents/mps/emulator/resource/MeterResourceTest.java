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

import static org.hamcrest.Matchers.hasKey;
import static org.junit.Assert.assertThat;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/spring/test-mocks.xml" })
public class MeterResourceTest extends MeterResourceTestBase {

    @Test
    public void shouldReturnMeterProfile() throws Exception {
        // when
        Map<String, Object> response = issueGet("meter/profile/1/2011-11-17T00:00/2011-11-18T00:00");
        
        // then
        assertThat(response, hasKey("status"));
        assertThat(response, hasKey("result"));
    }

    @Test
    public void shouldReturnMeterList() throws Exception {
        // when
        Map<String, Object> response = issueGet("meter/list/");
        
        // then
        assertThat(response, hasKey("id"));
        assertThat(response, hasKey("name"));
        assertThat(response, hasKey("children"));
    }

    @Test
    public void shouldReturnMeterProperties() throws Exception {
        // when
        Map<String, Object> response = issueGet("meter/properties/1");
        
        // then
        assertThat(response, hasKey("status"));
        assertThat(response, hasKey("result"));
    }

    @Test
    public void shouldReturnMeterState() throws Exception {
        // when
        Map<String, Object> response = issuePost("meter/relay/1/GET");
        
        // then
        assertThat(response, hasKey("status"));
        assertThat(response, hasKey("result"));
    }

    @Test
    public void shouldReturnOKWhenChangingMeterState() throws Exception {
        // when
        Map<String, Object> response = issuePost("meter/relay/1/OFF");
        
        //then
        assertThat(response, hasKey("status"));
        assertThat(response, hasKey("result"));
    }
}


