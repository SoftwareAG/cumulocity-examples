package c8y.trackeragent_it.service;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cumulocity.sdk.client.PlatformImpl;

import c8y.trackeragent_it.TestSettings;
import c8y.trackeragent_it.TrackerITSupport;
import c8y.trackeragent_it.config.TestConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfiguration.class })
public class NewDeviceRequestServiceTest {
    
    @Autowired
    private TestSettings testSettings;
    
    private NewDeviceRequestService newDeviceRequestService;
    
    @Before
    public void init() {
        PlatformImpl platform = TrackerITSupport.platform(testSettings);
        newDeviceRequestService = new NewDeviceRequestService(platform, testSettings);
        newDeviceRequestService.deleteAll();
    }
    
    @Test
    public void shouldManageNewDeviceRequest() throws Exception {
        newDeviceRequestService.create("abc");
        
        assertThat(newDeviceRequestService.get("abc")).isNotNull();
        assertThat(newDeviceRequestService.get("cde")).isNull();
        
        assertThat(newDeviceRequestService.exists("abc")).isTrue();
        assertThat(newDeviceRequestService.exists("cde")).isFalse();
        
        newDeviceRequestService.delete("abc");
        
        assertThat(newDeviceRequestService.get("abc")).isNull();
        assertThat(newDeviceRequestService.exists("abc")).isFalse();
    }
    
    @Test
    public void shouldGetAllNewDeviceRequest() throws Exception {
        newDeviceRequestService.create("abc");
        newDeviceRequestService.create("cde");
        
        assertThat(newDeviceRequestService.getAll()).hasSize(2);
        
        newDeviceRequestService.delete("abc");
        newDeviceRequestService.delete("cde");
    }
    
    @Test
    public void shouldDeleteAllNewDeviceRequest() throws Exception {
        newDeviceRequestService.create("abc");
        newDeviceRequestService.create("cde");
        
        newDeviceRequestService.deleteAll();
        
        assertThat(newDeviceRequestService.getAll()).hasSize(0);
    }

}
