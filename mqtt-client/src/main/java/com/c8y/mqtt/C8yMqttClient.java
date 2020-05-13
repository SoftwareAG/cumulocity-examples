package com.c8y.mqtt;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.internal.security.SSLSocketFactoryFactory;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Stream;

import static com.c8y.mqtt.HttpClientHelper.deleteRootCertificate;
import static com.c8y.mqtt.HttpClientHelper.postRootCertificate;
import static org.apache.commons.lang.StringUtils.isNotBlank;

public class C8yMqttClient {
    // Configuration
    private static final String PASSWORD = "";
    private static final String USER = "";
    private static final String TENANT = "";

    private static final String KEYSTORE = "";
    private static final String TRUSTSTORE = "";
    private static final String CLIENT_ID = "";
    static final String BROKER_URL = "";

    // smartrest uplink access token
    private static final String DEVICE_TOKEN_PUBLISH = "s/uat";
    // smartrest downlink access token
    private static final String DEVICE_TOKEN_SUBSCRIBE = "s/dat";
    // persistent smartrest uplink static
    private static final String STATIC_TOPIC = "s/us";

    private static class MyMqttClient implements MqttCallbackExtended {

        private final Set<String> receivedOperations = new CopyOnWriteArraySet<>();

        final MqttClient mqttClient;

        MyMqttClient() throws MqttException {
            mqttClient = connectSsl();
            mqttClient.setCallback(this);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private MqttClient connectSsl() throws MqttException {
            MqttClient mqttClient = new MqttClient(BROKER_URL, "d:" + CLIENT_ID, new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            Properties sslProperties = new Properties();
            sslProperties.put(SSLSocketFactoryFactory.KEYSTORE, getClass().getClassLoader().getResource(KEYSTORE).getPath());
            sslProperties.put(SSLSocketFactoryFactory.KEYSTOREPWD, "password");
            sslProperties.put(SSLSocketFactoryFactory.KEYSTORETYPE, "jks");
            sslProperties.put(SSLSocketFactoryFactory.TRUSTSTORE, getClass().getClassLoader().getResource(TRUSTSTORE).getPath());
            sslProperties.put(SSLSocketFactoryFactory.TRUSTSTOREPWD, "password");
            sslProperties.put(SSLSocketFactoryFactory.TRUSTSTORETYPE, "jks");
            sslProperties.put(SSLSocketFactoryFactory.CLIENTAUTH, true);
            options.setSSLProperties(sslProperties);
            mqttClient.setCallback(this);
            System.out.println("Connecting to broker " + BROKER_URL);
            mqttClient.connect(options);
            return mqttClient;
        }

        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
            System.out.println("Connect complete");
            if (reconnect) {
                subscribeOperations();
            }
        }

        @Override
        public void connectionLost(Throwable cause) {
            System.out.println("connectionLost");
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) {
            String msg = new String(message.getPayload());
            Stream.of(msg.split("\\n")).forEach(payload -> {
                System.out.println("\nmessageArrived " + topic + " = " + payload);
                if (payload.startsWith("511")) {
                    publishStringMessage(mqttClient, "501,c8y_Command", 0);
                    String operation = payload.split(",")[2];
                    boolean added = receivedOperations.add(operation);
                    if (added) {
                        publishStringMessage(mqttClient, "503,c8y_Command", 0);
                    } else {
                        System.out.println("Operation " + operation + " already exists");
                        publishStringMessage(mqttClient, "502,c8y_Command", 0);
                    }
                }
            });

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            try {
                String message = token.getMessage() != null ? token.getMessage().toString() : "Empty message";
                System.out.println("Delivery complete on topic : " + Arrays.toString(token.getTopics()) + " " + message);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        void subscribeOperations() {
            try {
                mqttClient.subscribe("s/ds", 2);
                System.out.println("Subscribed to s/ds");
                mqttClient.subscribe("s/e", 2);
                System.out.println("Subscribed to s/e");
                mqttClient.subscribe(DEVICE_TOKEN_SUBSCRIBE, 2);
                System.out.println("Subscribed to " + DEVICE_TOKEN_SUBSCRIBE);
            } catch (MqttException e) {
                System.out.println("Error subscribing to operations");
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        if (isNotBlank(TENANT) && isNotBlank(USER) && isNotBlank(PASSWORD)) {
            postRootCertificate(TENANT, USER, PASSWORD);
        }
        try {
            MyMqttClient myMqttClient = new MyMqttClient();
            Scanner command = new Scanner(System.in);
            boolean running = true;
            while (running) {
                System.out.print("payload> ");
                String payload = command.nextLine();
                switch (payload) {
                    case "quit":
                    case "exit":
                        if (isNotBlank(TENANT) && isNotBlank(USER) && isNotBlank(PASSWORD)) {
                            deleteRootCertificate(TENANT, USER, PASSWORD);
                        }
                        running = false;
                        break;
                    case "publish-token":
                        myMqttClient.mqttClient.publish(DEVICE_TOKEN_PUBLISH, new MqttMessage());
                        break;
                    case "upload-root":
                        postRootCertificate(TENANT, USER, PASSWORD);
                        break;
                    case "delete-root":
                        deleteRootCertificate(TENANT, USER, PASSWORD);
                        break;
                    case "subscribe":
                        myMqttClient.subscribeOperations();
                        break;
                    case "initialize":
                        publishStringMessage(myMqttClient.mqttClient, "100", 0);
                        break;
                    default:
                        if (!payload.isEmpty()) {
                            publishStringMessage(myMqttClient.mqttClient, payload, 0);
                        }
                        break;
                }
            }
            command.close();
            myMqttClient.mqttClient.disconnect();
            myMqttClient.mqttClient.close();
            System.out.println("Disconnected");
            System.exit(0);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        System.exit(1);
    }

    private static MqttMessage createMessage(String content, int qos) {
        MqttMessage message = new MqttMessage(content.getBytes());
        message.setQos(qos);
        return message;
    }

    private static void publishStringMessage(MqttClient client, String body, int qos) {
        MqttMessage message = createMessage(body, qos);
        try {
            client.publish(STATIC_TOPIC, message);
        } catch (MqttException e) {
            System.out.println("Error publishing " + message);
            e.printStackTrace();
        }
    }
}
