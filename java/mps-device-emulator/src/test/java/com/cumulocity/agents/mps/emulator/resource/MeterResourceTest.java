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


