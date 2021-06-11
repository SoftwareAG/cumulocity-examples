package c8y.example.notification.helloworld;

import org.springframework.boot.SpringApplication;

/**
 * This example microservice shows you how to create a subscription & consume a stream of notifications using WebSocket protocol. Before running this example microservice, please make sure to have created a device and specified its ID in `example.source.id` property located in applications.properties. The example microservice first creates a subscription and then proceeds to connect to the WebSocket server after obtaining the authorization token.
 *
 * The example microservice creates the following subscription to the specified device:
 *
 * {
 *     "source": { "id": "<DEVICE ID>" },
 *     "context": "mo",
 *     "subscription": "<SUBSCRIPTION NAME>",
 *     "subscriptionFilter": {
 *         "apis": ["measurements"],
 *         "typeFilter": "c8y_Speed"
 *     },
 *     "fragmentsToCopy": ["c8y_SpeedMeasurement", "c8y_MaxSpeedMeasurement"]
 * }
 *
 * Once the above subscription is created and a WebSocket client is connected after obtaining the necessary authorization token, the WebSocket client will start receiving notifications as and when messages sent to the specified device in Cumulocity meets the subscription criteria.
 *
 * In the above example, we have expressed interest in receiving only measurements that bear the type `c8y_speed`. The `fragmentsToCopy` property further transforms the filtered measurement to *only* include c8y_SpeedMeasurement & c8y_MaxSpeedMeasurement fragments.
 *
 * As an example, if we post the following measurement to the specified device that meets our filter criteria above:
 *
 * {
 * 	"c8y_SpeedMeasurement": {
 *     	"T": {
 *         	"value": 100,
 *             "unit": "km/h" }
 *         },
 *     "c8y_Speed2Measurement": {
 *     	"T": {
 *         	"value": 150,
 *             "unit": "km/h" }
 *         },
 *     "c8y_Speed3Measurement": {
 *     	"T": {
 *         	"value": 200,
 *             "unit": "km/h" }
 *         },
 *     "c8y_MaxSpeedMeasurement": {
 *     	"T": {
 *         	"value": 300,
 *             "unit": "km/h" }
 *         },
 *     "time":"2021-06-11T17:03:14.000+02:00",
 *     "source": {
 *     	"id":"<DEVICE ID>" },
 *     "type": "c8y_Speed"
 * }
 *
 * we will receive:
 *
 * {
 * 	"c8y_SpeedMeasurement": {
 *     	"T": {
 *         	"value": 100,
 *             "unit": "km/h" }
 *         },
 *     "c8y_MaxSpeedMeasurement": {
 *     	"T": {
 *         	"value": 300,
 *             "unit": "km/h" }
 *         },
 *     "time":"2021-06-11T17:03:14.000+02:00",
 *     "source": {
 *     	"id":"<DEVICE ID>" },
 *     "type": "c8y_Speed"
 * }
 *
 * as we have specified to transform the filtered measurement to only include c8y_SpeedMeasurement and c8y_MaxSpeedMeasurement.
 */

public class HelloWorldApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelloWorldConfiguration.class, args);
    }

}
