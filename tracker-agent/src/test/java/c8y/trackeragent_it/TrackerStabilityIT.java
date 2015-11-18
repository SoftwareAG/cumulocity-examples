package c8y.trackeragent_it;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import c8y.trackeragent.utils.Devices;
import c8y.trackeragent.utils.Positions;
import c8y.trackeragent.utils.Reports;

public class TrackerStabilityIT extends TrackerITSupport {

    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    private final int parallelIndex = 2;
    private final static Random random = new Random();
    
    @Test
    public void shouldWork() throws Exception {
        for (int i = 0; i < parallelIndex; i++) {
            executorService.submit(new TrackerTask());
        }
        while(true) {
            
        }
    }

    private String executeFirstStep() throws Exception {
        writeInNewConnection(new byte[]{});
        String imei = Devices.randomImei();
        createNewDeviceRequest(imei);
        byte[] report = Reports.getTelicReportBytes(imei, Positions.ZERO, Positions.SAMPLE_1, Positions.SAMPLE_2, Positions.SAMPLE_3);

        // trigger bootstrap
        writeInNewConnection(report);
        Thread.sleep(8000);
        acceptNewDeviceRequest(imei);
        return imei;
    }

    private void executeStep(String imei) throws Exception {
        byte[] report = null;
        report = Reports.getTelicReportBytes(imei, Positions.random(), Positions.random(), Positions.random(), Positions.random());
        
        if(random.nextInt(2) == 0) {
            System.out.println("send empty report");
            report = new byte[]{};
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
