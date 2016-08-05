package c8y.trackeragent_it.simulator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cumulocity.sdk.client.PlatformImpl;

import c8y.trackeragent.protocol.telic.TelicDeviceMessages;
import c8y.trackeragent.utils.Devices;
import c8y.trackeragent.utils.Positions;
import c8y.trackeragent.utils.message.TrackerMessage;
import c8y.trackeragent_it.TestSettings;
import c8y.trackeragent_it.TrackerITSupport;
import c8y.trackeragent_it.config.TestConfiguration;
import c8y.trackeragent_it.service.Bootstraper;
import c8y.trackeragent_it.service.NewDeviceRequestService;
import c8y.trackeragent_it.service.SocketWriter;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfiguration.class })
public class LoadIT {
    
    @Autowired
    private TestSettings testSettings;
    
    private TelicDeviceMessages deviceMessages = new TelicDeviceMessages();

    private Bootstraper bootstraper;
    
    private NewDeviceRequestService newDeviceRequestService;

    private SocketWriter socketWriter;
    
    @Before
    public void before() {
        PlatformImpl platform = TrackerITSupport.platform(testSettings);
        newDeviceRequestService = new NewDeviceRequestService(platform, testSettings);
        socketWriter = new SocketWriter(testSettings, 9090);
        bootstraper = new Bootstraper(testSettings, socketWriter, newDeviceRequestService);
        //newDeviceRequestService.deleteAll();
    }
    
    @Test
    public void assureAgentBootstraped() throws Exception {
        String imei = Devices.randomImei();
        bootstrapDevice(bootstraper, imei);
        Thread.sleep(5000);
        socketWriter.writeInNewConnection(deviceMessages.positionUpdate(imei, Positions.ZERO));
        Thread.sleep(5000);
        socketWriter.writeInNewConnection(deviceMessages.positionUpdate(imei, Positions.ZERO));
        Thread.sleep(5000);
    }
    
    @Test
    public void shouldBootstrapMultiplyTelicDevices() throws Exception {
        int imeiStart = 100000;
        int imeiStop =  100001;
        for(int imei = imeiStart; imei <= imeiStop; imei++) {
            bootstrapDevice(bootstraper, "" + imei);
        }
    }

    private void bootstrapDevice(Bootstraper bootstraper, String imei) throws Exception {
        TrackerMessage message = deviceMessages.positionUpdate(imei, Positions.ZERO);
        bootstraper.bootstrapDevice(imei, message);
    }
    
    

}
