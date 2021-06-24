/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, 
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent_it.simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.cumulocity.sdk.client.devicecontrol.DeviceCredentialsApi;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cumulocity.sdk.client.PlatformImpl;

import c8y.Position;
import c8y.trackeragent.protocol.coban.CobanDeviceMessages;
import c8y.trackeragent.utils.message.TrackerMessage;
import c8y.trackeragent_it.TestSettings;
import c8y.trackeragent_it.TrackerITSupport;
import c8y.trackeragent_it.config.TestConfiguration;
import c8y.trackeragent_it.service.Bootstraper;
import c8y.trackeragent_it.service.NewDeviceRequestService;
import c8y.trackeragent_it.service.SocketWritter;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfiguration.class })
@Ignore
public class LoadIT {
    
    private static Logger logger = LoggerFactory.getLogger(LoadIT.class);

    @Autowired
    private TestSettings testSettings;

    @Autowired
    private DeviceCredentialsApi deviceCredentialsApi;

    private CobanDeviceMessages deviceMessages = new CobanDeviceMessages();

    private Bootstraper bootstraper;

    private NewDeviceRequestService newDeviceRequestService;

    private ExecutorService executorService = Executors.newFixedThreadPool(TOTAL_THREADS);

    private SocketWritter socketWriter;

    private List<String> imeis = new ArrayList<>();

    private Map<String, SocketWritter> socketWriters = new HashMap<>();
    
    private static final int IMEI_START     = 100000;
    private static final int IMEI_STOP      = 101010;
    private static final int TOTAL_TASKS_PER_DEVICE = 20;
    private static final int TOTAL_THREADS = 10;
    private static final int REMOTE_PORT = 9091;

    @Before
    public void before() {
        PlatformImpl platform = TrackerITSupport.platform(testSettings);
        newDeviceRequestService = new NewDeviceRequestService(platform, testSettings, deviceCredentialsApi);
        socketWriter = newSocketWritter();
        bootstraper = new Bootstraper(testSettings, socketWriter, newDeviceRequestService);
        for (int imeiNo = IMEI_START; imeiNo <= IMEI_STOP; imeiNo++) {
            String imei = String.valueOf(imeiNo);
            imeis.add(imei);
            socketWriters.put(imei, newSocketWritter());
        }
        newDeviceRequestService.deleteAll();
    }

    @Test
    public void shouldBootstrapDevices() throws Exception {
        for (String imei : imeis) {
            TrackerMessage message = deviceMessages.logon(imei);
            bootstraper.bootstrapDeviceNotAgentAware(imei, message);
        }
    }

    @Test
    public void shouldSimulateMultiplyDevices() throws Exception {
        SimulatorContext simulatorContext = new SimulatorContext();
        
        // connect all devices
        simulatorContext.setLatch(imeis.size());
        for (String imei : imeis) {
            TrackerMessage message = deviceMessages.logon(imei);
            sendMessage(simulatorContext, imei, message);
        }
        
        // wait for all device connected
        simulatorContext.getLatch().await(600, TimeUnit.SECONDS);
        Thread.sleep(TimeUnit.SECONDS.toMillis(20));

        // report all devices
        PositionIterator positionIter = new PositionIterator(5114.3471, 00643.2373, 0.0001);
        simulatorContext.setLatch((IMEI_STOP - IMEI_START + 1) * TOTAL_TASKS_PER_DEVICE);
        for (int loopIndex = 0; loopIndex < TOTAL_TASKS_PER_DEVICE; loopIndex++) {
            Position position = positionIter.next();
            Thread.sleep(TimeUnit.SECONDS.toMillis(5));
            for (String imei : imeis) {
                TrackerMessage message = deviceMessages.positionUpdate(imei, position);
                sendMessage(simulatorContext, imei, message);
            }
        }
        
        // wait for all report sending
        simulatorContext.getLatch().await(600, TimeUnit.SECONDS);
        
        // wait for all report finishing
        Thread.sleep(TimeUnit.SECONDS.toMillis(20));
        
        logger.info("Final position is: {}", positionIter.current());
    }
    
    private void sendMessage(SimulatorContext simulatorContext, String imei, TrackerMessage message) {
        SimulatorTask task = asTask(imei, message);
        simulatorContext.addTask(task);
        executorService.execute(new SimulatorWorker(simulatorContext));
    }

    private SimulatorTask asTask(String imei, TrackerMessage message) {
        SocketWritter socketWriter = socketWriters.get(imei);
        return new SimulatorTask(socketWriter, message);
    }

    private SocketWritter newSocketWritter() {
        return new SocketWritter(testSettings, REMOTE_PORT);
    }


}
