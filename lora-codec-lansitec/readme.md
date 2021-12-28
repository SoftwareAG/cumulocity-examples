# Writing your own LPWAN Custom Codec using lpwan-custom-codec

## Introduction

Cumulocity IoT has the ability to integrate LPWAN devices via LPWAN agents. Supported list of LPWAN agents. (https://cumulocity.com/guides/protocol-integration/overview/)   

In the following, we describe how to implement a custom codec microservice for implementing LPWAN device specific codec for decoding and encoding the device payload. 

## Codec Workflow

We have to deploy the codec microservice into Cumulocity where the codec microservice creates the device types with the list of supported device models.

#### The REST endpoint: /decode

LPWAN codec microservice automatically exposes one REST endpoint using the path */decode*

#### Request JSON body format

When posting data to the decoder microservice, the LPWAN agents posts the data using the following JSON format:

```
{
    "sourceDeviceId":"<<device Id>>",
   	"value":"<<The value to be decoded (hex string)",
   	"args": {
   		"deviceModel": "<<device model>>",
   		"deviceManufacturer": "<<device manufacturer>>",
   		"sourceDeviceEui": "<<device external Id>>"
   	},
    
}
```

The LPWAN agent passes in the following fragments to the codec microservice.

*args* - Meta information that is required by codec microservice to know the model and manufacturer of the device, along with the EUI of the device.

*sourceDeviceId* - The ID of the source device in the Cumulocity IoT inventory

*value* - The actual value to be decoded. The value is a series of bytes encoded as a hexadecimal string

**Example**:

```
{
    "sourceDeviceId": "1025"
    "value": "202355251812984589",
    "args": {
        "deviceModel": "Asset Tracker",
        "deviceManufacturer": "LANSITEC",
        "sourceDeviceEui": "AA02030405060708"
    },
}
```

#### Response JSON body format

If the decoder microservice is able to handle the request, it responds with the following JSON format:

```
{
  "alarms": [<<Array of alarms to be created>>],
  "alarmTypesToUpdate": [<<Array of alarm types to be updated>>],
  "events": [<<Array of events to be created >>],
  "dataFragments" : [Map <<fragment-path>,<Value>>],
  "success": true || false,
  "measurements": [<<Array of measurements to be created>>]
}
```

The fragments above are used as follows:

*alarms* - A list of alarms to be created by the LPWAN agent. The alarms have to be given in the ordinary [Cumulocity IoT alarm JSON format](https://cumulocity.com/guides/reference/alarms/). 

*events* - A list of events to be created by the LPWAN agent. The events have to be given in the ordinary [Cumulocity IoT event JSON format](https://cumulocity.com/guides/reference/events/).

*alarmTypesToUpdate* - A list of alaram types to be updated by LPWAN agent.

*dataFragments* - The data fragments can be used by a decoder to hand over a set of fragment updates.

*success* - An informative boolean flag (true or false) that indicates if decoding by the microservice was successful.

*measurements* - A list of measurements to be created by the LPWAN agent. The syntax here follows an own DTO format, like this example shows:


```json
  {
      "alarms": null,
      "alarmTypesToUpdate": null,
      "events": [
          {
              "type": "Tracker status",
              "time": "2021-12-27T10:13:24.251+00:00",
              "creationTime": null,
              "text": "GPSSTATE: LOCATING\nVIBSTATE: 5\nCHGSTATE: UNKNOWN(24)",
              "source": {
                  "id": {
                      "attrs": {},
                      "type": "com_cumulocity_model_idtype_GId",
                      "value": "1025",
                      "name": null,
                      "long": 1025
                  }
              },
              "dateTime": "2021-12-27T10:13:24.251Z"
          }
      ],
      "measurements": [
          {
              "type": "c8y_Battery",
              "series": "c8y_Battery",
              "time": "2021-12-27T10:13:24.251Z",
              "values": [
                  {
                      "seriesName": "level",
                      "unit": "%",
                      "value": 35
                  }
              ]
          },
          {
              "type": "Tracker Signal Strength",
              "series": "Tracker Signal Strength",
              "time": "2021-12-27T10:13:24.251Z",
              "values": [
                  {
                      "seriesName": "rssi",
                      "unit": "dBm",
                      "value": -85
                  }
              ]
          },
          {
              "type": "Tracker Signal Strength",
              "series": "Tracker Signal Strength",
              "time": "2021-12-27T10:13:24.251Z",
              "values": [
                  {
                      "seriesName": "snr",
                      "unit": "dBm",
                      "value": 0
                  }
              ]
          }
      ],
      "dataFragments": null,
      "success": true
  }
```

**Full decoder response sample**

```
{
    "self": null,
    "alarms": [{
        "source": {
            "id": null
          },
        "type": "c8yDemoDecoderalarm",
        "text": "I am an decoder alarm",
        "severity": "MINOR",
        "status": "ACTIVE",
        "time": "2020-03-03T12:03:23.845Z",
        "myFragment": "my data"
    }],
    "alarmTypesToUpdate": null,
    "events": [
        {
            "self": null,
            "attrs": {},
            "id": null,
            "type": "c8y_LocationUpdate",
            "time": "1997-10-26T13:27:16.000+00:00",
            "creationTime": null,
            "text": "Location updated",
            "externalSource": null,
            "source": {
                "self": null,
                "attrs": {},
                "id": {
                    "attrs": {},
                    "type": "com_cumulocity_model_idtype_GId",
                    "value": "1025",
                    "name": null,
                    "long": 1025
                },
                "type": null,
                "name": null,
                "lastUpdated": null,
                "creationTime": null,
                "owner": null,
                "childDevices": null,
                "childAssets": null,
                "childAdditions": null,
                "deviceParents": null,
                "assetParents": null,
                "additionParents": null,
                "lastUpdatedDateTime": null,
                "creationDateTime": null,
                "selfDecoded": null
            },
            "dateTime": "1997-10-26T13:27:16.000Z",
            "lastUpdatedDateTime": null,
            "creationDateTime": null,
            "selfDecoded": null
        }
    ],
    "measurements": null,
    "dataFragments": [
        {
            "key": "c8y_Position/lat",
            "value": null,
            "valueAsObject": 9.609690346957522E-28
        },
        {
            "key": "c8y_Position/lng",
            "value": null,
            "valueAsObject": 1.1554608044067426E-17
        }
    ],
    "message": null,
    "success": true,
    "selfDecoded": null
}

```

## Steps to implement LPWAN codec microservice:

Decoder microservices can be easily built on top of [Cumulocity IoT Microservices](http://www.cumulocity.com/guides/microservice-sdk/java).
In order to serve as a LPWAN decoder microservice, two requirements have to be met

1. The codec microservice Main class needs to be annotated as `@CodecMicroserviceApplication`.
2. The microservice needs to provide implementation for the following interfaces.
 ```java
 /**
 * The <b>Codec</b> interface exposes methods to provide the uniquely supported devices. The class which implements this interface should be annotated with "@Component".
 */
public interface Codec {

    /**
     * This method returns a set of uniquely supported devices w.r.t the device manufacturer and the device model.
     *
     * @return Set
     */
    @NotNull @NotEmpty Set<DeviceInfo> supportsDevices();
}
```

```java
public interface DecoderService {

    /**
     * Decodes byte array data into DecoderResult object.
     *
     * @param inputData Hex encoded input byte array
     * @param deviceId device from which this data comes from
     * @param args additional arguments that may be required by decoder
     * @return DecoderResult object
     * @throws DecoderServiceException when decode failed
     */
    DecoderResult decode(String inputData, GId deviceId, Map<String, String> args) throws DecoderServiceException;
}

```

## Sample decoder microservice implementation

In this repository, you'll find a very straightforward decoder example, the lansitec decoder (`lora-codec-lansitec`). It is implemented in Spring Boot. 

Below are the steps to be followed while implementing the microservice. 

1) Annotate the Main class with `@CodecMicroserviceApplication`

```java
@CodecMicroserviceApplication
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
```

2) Implement the `Codec` interface and supply the list of supported devices.

```java
@Component
public class LansitecCodec implements Codec {

    /**
     * This method should populate a set of unique devices identified by their manufacturer and model.
     * @return Set: A set of unique devices identified by their manufacturer and model.
     */
    public Set<DeviceInfo> supportsDevices() {

        // The manufacturer "LANSITEC" has 2 different devices with model "Outdoor Asset Tracker" and "Temperature Sensor"
        DeviceInfo deviceInfo_Lansitec_Asset_Tracker = new DeviceInfo("LANSITEC", "Asset Tracker");
        DeviceInfo deviceInfo_Lansitec_Temperature_Sensor = new DeviceInfo("LANSITEC", "Temperature Sensor");

        return Stream.of(deviceInfo_Lansitec_Asset_Tracker, deviceInfo_Lansitec_Temperature_Sensor).collect(Collectors.toCollection(HashSet::new));
    }
}
```

3) Implement `DecoderService` interface

```java
@Component
public class LansitecDecoder implements DecoderService {
    @Override
    public DecoderResult decode(String inputData, GId deviceId, Map<String, String> args) throws DecoderServiceException {

        // Create an LpwanDecodeInputData object to get selected device information like manufacturer and model
        LpwanDecoderInputData decoderInput = new LpwanDecoderInputData(inputData, deviceId, args);

        // Sample decoding logic
        try {
            // DecoderResult will contain the list of measurements, events, alarms and/or alarmTypes to Update. 
            DecoderResult decoderResult =  process(decoderInputData);
        } catch(Exception e) {
            // Create an alarm on the device, so the decoder issue is shown as an alarm
            DecoderResult decoderResult = new DecoderResult();
            AlarmRepresentation alarm = new AlarmRepresentation();
            alarm.setSource(ManagedObjects.asManagedObject(deviceId));
            alarm.setType("DecoderError");
            alarm.setSeverity(CumulocitySeverities.CRITICAL.name());
            alarm.setText(e.getMessage());
            alarm.setDateTime(DateTime.now());
            decoderResult.addAlarm(alarm, true);

            throw new DecoderServiceException(e, e.getMessage(), decoderResult);
        }
        return decoderResult;

    }
}
```

A flexible option named `success` is provided in the DecoderResult which represents whether the `decode` operation is successful or not. 

## Deploying the example codec microservice

In order to build and deploy the sample codec microservice, follow [Microservice Guide](http://www.cumulocity.com/guides/microservice-sdk/java/)

Clone this repository first. Next, build the microservice using `mvn clean install`. The build will create a zip file of the decoder microservice.

In the next step, deploy the microservice using Cumulocity IoT UI. Once the decoder microservice has been deployed, wait a couple of minutes in order to allow Cumulocity IoT to discover the new decoder. Then, open Device Management UI. Inside Device Protocols, you should now see the device types with type as 'lpwan' created by codec microservice. Map one of the device types to the LPWAN device and send uplink message to see the respective measurements 
created in Cumulocity. 
