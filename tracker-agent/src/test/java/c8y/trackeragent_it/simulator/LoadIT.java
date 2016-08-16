package c8y.trackeragent_it.simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cumulocity.sdk.client.PlatformImpl;

import c8y.trackeragent.protocol.coban.CobanDeviceMessages;
import c8y.trackeragent.utils.Positions;
import c8y.trackeragent.utils.message.TrackerMessage;
import c8y.trackeragent_it.TestSettings;
import c8y.trackeragent_it.TrackerITSupport;
import c8y.trackeragent_it.config.TestConfiguration;
import c8y.trackeragent_it.service.Bootstraper;
import c8y.trackeragent_it.service.NewDeviceRequestService;
import c8y.trackeragent_it.service.SocketWritter;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfiguration.class })
public class LoadIT {

    @Autowired
    private TestSettings testSettings;

    private CobanDeviceMessages deviceMessages = new CobanDeviceMessages();

    private Bootstraper bootstraper;

    private NewDeviceRequestService newDeviceRequestService;

    private ExecutorService executorService = Executors.newFixedThreadPool(TOTAL_THREADS);

    private SocketWritter socketWriter;

    private List<String> imeis = new ArrayList<>();

    private Map<String, SocketWritter> socketWriters = new HashMap<>();

//    private static final int IMEI_START     = 200005;
//    private static final int IMEI_STOP      = 200007;
    private static final int IMEI_START     = 100000;
    private static final int IMEI_STOP      = 100200;
    private static final int TOTAL_TASKS_PER_DEVICE = 200;
    private static final int TOTAL_THREADS = 1;
    private static final int REMOTE_PORT = 9091;

    @Before
    public void before() {
        PlatformImpl platform = TrackerITSupport.platform(testSettings);
        newDeviceRequestService = new NewDeviceRequestService(platform, testSettings);
        socketWriter = newSocketWritter();
        bootstraper = new Bootstraper(testSettings, socketWriter, newDeviceRequestService);
        for (int imeiNo = IMEI_START; imeiNo <= IMEI_STOP; imeiNo++) {
            String imei = String.valueOf(imeiNo);
            imeis.add(imei);
            socketWriters.put(imei, newSocketWritter());
        }
    }

    @Test
    public void shouldBootstrapDevices() throws Exception {
        for (int imei = IMEI_START; imei <= IMEI_STOP; imei++) {
            bootstrapDevice(bootstraper, "" + imei);
        }
    }

    @Test
    public void shouldSimulateMultiplyDevices() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch((IMEI_STOP - IMEI_START + 1) * TOTAL_TASKS_PER_DEVICE);
        SimulatorContext simulatorContext = new SimulatorContext(countDownLatch);

        for (int poolNo = 0; poolNo < TOTAL_TASKS_PER_DEVICE; poolNo++) {
            for (String imei : imeis) {
                TrackerMessage message = getMessage(poolNo, imei);
                SimulatorTask task = asTask(imei, message);
                simulatorContext.addTask(task);
            }
        }
        for (int index = 0; index < TOTAL_THREADS; index++) {
            executorService.execute(new SimulatorWorker(simulatorContext));
        }
        countDownLatch.await(600, TimeUnit.SECONDS);
        Thread.sleep(TimeUnit.SECONDS.toMillis(20));
    }

    private TrackerMessage getMessage(int poolNo, String imei) {
        if (poolNo == 0) {
            return deviceMessages.logon(imei);
        } else {
            return deviceMessages.positionUpdate(imei, Positions.ZERO);
        }
    }

    private SimulatorTask asTask(String imei, TrackerMessage message) {
        SocketWritter socketWriter = socketWriters.get(imei);
        return new SimulatorTask(socketWriter, message);
    }

    private void bootstrapDevice(Bootstraper bootstraper, String imei) throws Exception {
        TrackerMessage message = deviceMessages.logon(imei);
        bootstraper.bootstrapDeviceNotAgentAware(imei, message);
    }
    
    private SocketWritter newSocketWritter() {
        return new SocketWritter(testSettings, REMOTE_PORT);
    }


}
