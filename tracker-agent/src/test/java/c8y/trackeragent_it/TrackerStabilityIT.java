package c8y.trackeragent_it;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Ignore;
import org.junit.Test;

import c8y.trackeragent.protocol.TrackingProtocol;
import c8y.trackeragent.protocol.coban.CobanDeviceMessages;
import c8y.trackeragent.utils.Devices;
import c8y.trackeragent.utils.Positions;
import c8y.trackeragent.utils.message.TrackerMessage;

@Ignore
public class TrackerStabilityIT extends TrackerITSupport {

    private ExecutorService executorService = Executors.newFixedThreadPool(10);
    private CobanDeviceMessages deviceMessages = new CobanDeviceMessages();

    private final int parallelIndex = 2;
    private final static Random random = new Random();
    
    @Override
    protected TrackingProtocol getTrackerProtocol() {
        return TrackingProtocol.TELIC;
    }
    
    @Test
    public void shouldWork() throws Exception {
        for (int i = 0; i < parallelIndex; i++) {
            executorService.submit(new TrackerTask());
        }
        while(true) {
            
        }
    }

    private String executeFirstStep() throws Exception {
        String imei = Devices.randomImei();
        bootstrapDevice(imei, deviceMessages.logon(imei));
        return imei;
    }

    private void executeStep(String imei) throws Exception {
        TrackerMessage report;
        if (random.nextBoolean()) {
            report = deviceMessages.msg();
        } else {
            report = deviceMessages.positionUpdate(imei, Positions.random());
        }
        writeInNewConnection(report);
    }

    class TrackerTask implements Runnable {

        int step = 0;
        String deviceImei;

        @Override
        public void run() {
            try {
                while (true) {
                    doRun();
                    Thread.sleep(random.nextInt(1000));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void doRun() throws Exception {
            if (step == 0) {
                deviceImei = executeFirstStep();
            } else {
                executeStep(deviceImei);
            }
            step++;
        }
    }
}
