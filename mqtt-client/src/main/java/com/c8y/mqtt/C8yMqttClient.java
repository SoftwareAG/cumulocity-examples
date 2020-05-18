package com.c8y.mqtt;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.internal.security.SSLSocketFactoryFactory;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Arrays;
import java.util.Properties;

public class C8yMqttClient {
    //Configuration
    private static final String KEYSTORE_NAME = "";
    private static final String KEYSTORE_PASSWORD = "";
    private static final String KEYSTORE_FORMAT = "";
    private static final String TRUSTSTORE_NAME = "";
    private static final String TRUSTSTORE_PASSWORD = "";
    private static final String TRUSTSTORE_FORMAT = "";
    private static final String CLIENT_ID = "";
    private static final String BROKER_URL = "";

    private static class MyMqttClient implements MqttCallbackExtended {

        final MqttClient mqttClient;

        MyMqttClient() throws MqttException {
            mqttClient = connect();
            mqttClient.setCallback(this);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private MqttClient connect() throws MqttException {
            MqttClient mqttClient = new MqttClient(BROKER_URL, "d:" + CLIENT_ID, new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            Properties sslProperties = new Properties();
            sslProperties.put(SSLSocketFactoryFactory.KEYSTORE, getClass().getClassLoader().getResource(KEYSTORE_NAME).getPath());
            sslProperties.put(SSLSocketFactoryFactory.KEYSTOREPWD, KEYSTORE_PASSWORD);
            sslProperties.put(SSLSocketFactoryFactory.KEYSTORETYPE, KEYSTORE_FORMAT);
            sslProperties.put(SSLSocketFactoryFactory.TRUSTSTORE, getClass().getClassLoader().getResource(TRUSTSTORE_NAME).getPath());
            sslProperties.put(SSLSocketFactoryFactory.TRUSTSTOREPWD, TRUSTSTORE_PASSWORD);
            sslProperties.put(SSLSocketFactoryFactory.TRUSTSTORETYPE, TRUSTSTORE_FORMAT);
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
        }

        @Override
        public void connectionLost(Throwable cause) {
            System.out.println("connectionLost");
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) {
            System.out.println("\nmessageArrived " + topic + " = " + Arrays.toString(message.getPayload()));
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            try {
                System.out.println("Delivery complete on topic : " + Arrays.toString(token.getTopics()) + " " + token.getMessage());
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        public static void main(String[] args) {
            try {
                MyMqttClient myMqttClient = new MyMqttClient();
                myMqttClient.mqttClient.disconnect();
                myMqttClient.mqttClient.close();
                System.out.println("Disconnected");
                System.exit(0);
            } catch (Throwable t) {
                t.printStackTrace();
            }
            System.exit(1);
        }

    }
}
