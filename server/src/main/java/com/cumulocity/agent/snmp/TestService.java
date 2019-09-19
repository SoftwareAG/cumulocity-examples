package com.cumulocity.agent.snmp;

import com.cumulocity.agent.snmp.configuration.SnmpAgentGatewayProperties;
import com.cumulocity.agent.snmp.platform.pubsub.publisher.AlarmPublisher;
import com.cumulocity.agent.snmp.platform.pubsub.publisher.EventPublisher;
import com.cumulocity.agent.snmp.platform.pubsub.publisher.MeasurementPublisher;
import com.cumulocity.agent.snmp.platform.service.SnmpAgentGatewayService;
import com.cumulocity.model.JSONBase;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.*;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class TestService {

    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    @Autowired
    SnmpAgentGatewayProperties snmpAgentGatewayProperties;

    @Autowired
    SnmpAgentGatewayService snmpAgentGatewayService;

    @Autowired
    private MeasurementPublisher measurementPublisher;

    @Autowired
    private AlarmPublisher alarmPublisher;

    @Autowired
    private EventPublisher eventPublisher;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @PostConstruct
    private void postConstruct() {
        executorService.execute(() -> {
                    File messagesFile = Paths.get(
                            System.getProperty("user.home"),
                            ".snmp",
                            snmpAgentGatewayProperties.getGatewayIdentifier().toLowerCase(),
                            "messages.csv").toFile();
                    if(!messagesFile.exists()) {
                        try {
                            messagesFile.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    File successfulMessagesFile = Paths.get(
                            System.getProperty("user.home"),
                            ".snmp",
                            snmpAgentGatewayProperties.getGatewayIdentifier().toLowerCase(),
                            "successfulMessages.csv").toFile();
                    if(!successfulMessagesFile.exists()) {
                        try {
                            successfulMessagesFile.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    File failedMessagesFile = Paths.get(
                            System.getProperty("user.home"),
                            ".snmp",
                            snmpAgentGatewayProperties.getGatewayIdentifier().toLowerCase(),
                            "failedMessages.csv").toFile();
                    if(!failedMessagesFile.exists()) {
                        try {
                            failedMessagesFile.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    while(true) {
                        try (BufferedReader reader = new BufferedReader(new FileReader(messagesFile));
                             BufferedWriter successWriter = new BufferedWriter(new FileWriter(successfulMessagesFile));
                             BufferedWriter failureWriter = new BufferedWriter(new FileWriter(failedMessagesFile));
                        ) {

                            String oneLine = reader.readLine();
                            while(oneLine != null) {

                                try {
                                    oneLine = oneLine.trim();
                                    String[] splitStrings = oneLine.trim().split(";");

                                    splitStrings[1] = splitStrings[1].replace("{current_time}", SIMPLE_DATE_FORMAT.format(new Date()));

                                    String messageType = splitStrings[0].trim().toUpperCase();
                                    switch (messageType) {
                                        case "M": {
                                            measurementPublisher.publish(JSONBase.fromJSON(splitStrings[1], MeasurementRepresentation.class));

                                            break;
                                        }
                                        case "A": {
                                            alarmPublisher.publish(JSONBase.fromJSON(splitStrings[1], AlarmRepresentation.class));

                                            break;
                                        }
                                        case "E": {
                                            eventPublisher.publish(JSONBase.fromJSON(splitStrings[1], EventRepresentation.class));

                                            break;
                                        }
                                        default: {
                                            throw new InvalidObjectException("First character of the line should be M for MeasurementPubSub, A for AlarmPubSub, E for EventPubSub.");
                                        }
                                    }

                                    try {
                                        successWriter.write(oneLine);
                                        successWriter.newLine();
                                    } catch (IOException ioe) {
                                        ioe.printStackTrace();
                                    }
                                } catch(Throwable t) {
                                    try {
                                        failureWriter.write(oneLine + ", ERROR: " + t.getMessage());
                                        failureWriter.newLine();

                                        t.printStackTrace();
                                    } catch (IOException ioe) {
                                        ioe.printStackTrace();
                                    }
                                }

                                oneLine = reader.readLine();
                            }
                        } catch (IOException ioe) {
//                            ioe.printStackTrace();
//                            break;
                        }


                        if(messagesFile.exists() && !messagesFile.delete()) {
                            log.error("UNABLE TO DELETE THE " + messagesFile.getAbsolutePath() + ", SO EXITING THE PROCESS!!!");
                            break;
                        }

//                        System.out.println("MEASUREMENTS PUB:" + measurementPubSub.publishedCount);
//                        System.out.println("MEASUREMENTS SUB:" + measurementPubSub.subscribeCount);
//                        System.out.println("ALARMS PUB:" + alarmPubSub.publishedCount);
//                        System.out.println("ALARMS SUB:" + alarmPubSub.subscribeCount);
//                        System.out.println("EVENTS PUB:" + eventPubSub.publishedCount);
//                        System.out.println("EVENTS SUB:" + eventPubSub.subscribeCount);

                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
//                            e.printStackTrace();
                            break;
                        }
//
//                        if(!snmpAgentGatewayService.isPlatformAvailable()) {
//                            snmpAgentGatewayService.markPlatfromAsAvailable();
//                        }
                    }
                }
        );
    }

    @PreDestroy
    private void stopSubscriber() {
        try {
            executorService.shutdownNow(); // Shutdown the Drainer
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
