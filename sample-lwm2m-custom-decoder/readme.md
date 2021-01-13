# Writing your own LW2M Custom Action using Decoder Microservices

## Introduction

Cumulocity IoT is able to fully integrate most Lightweight M2M (LWM2M) devices out of the box, **without the need to write code.** As long as devices solely use standard LWM2M data types (String, Integer, Floats...), the [existing mapping functionalities of Cumulocity IoT LWM2M](https://www.cumulocity.com/guides/users-guide/optional-services#lwm2m) are completely sufficient.

In certain cases, Lightweight M2M devices however expose proprietary data, for example binary arrays, often using the LWM2M Opaque data type. In these cases, it is impossible to defer the structure of the data from the DDF XML.  In order to enable Cumulocity IoT users to process such data,  so-called Custom Actions can be used to trigger so-called *decoder microservices.*

In the following, we explain the general workflow for decoding such data using decoder microservices. In addition, we discuss the interface between the LWM2M Agent and the decoder microservice in detail.

In a second step, we describe how to implement a microservice along the example in this repository - a binary series decoder, which turns a series of bytes into measurements.

## Decoder Workflow

Let us for now assume that you have a decoder microservice already available. You may here think of a microservice decoding CBOR data, or the example described in this document.

#### Configuration

The first step for using the decoder microservice is obviously to deploy the microservice in Cumulocity IoT. Once that has been done, map the LWM2M resource that carries the data to be decoded to pass the data to your decoder microservice. [Our LWM2M guide]((https://cumulocity.com/guides/protocol-integration/#lwm2m)) explains how to set up such mappings.

#### Delivery of data and the decoder microservice invocation

In the following, we describe the end-2-end workflow between a LWM2M device sending opaque data and the data being decoded by a decoder microservice.

1. The LWM2M device sends its payload to the LWM2M agent, originating at a LWM2M resource that is mapped in Cumulocity IoT to a decoder microservice. Typically, this data is in the OPAQUE format, and in most cases the data is delivered in a notification message.

2. The LWM2M agent receives this data. It first temporarily persists the data internally  as Cumulocity IoT events. It then acknowledges the receipt of the data to the device. If the device sends data from a multiple instance resource, data from all resources is persisted in separate events.

3. In parallel, a decoupled and continuously-running process works through the persisted internal events and pushes them to the decoder microservices via a REST call. In case of a failure with this REST call, for example due to the decoder being down, the LWM2M agent retries multiple times before giving up. A REST call to the decoder is considered to have failed if it returns with an abnormal HTTP response code or if an internal exception occurs, for example a connection reset.

4. Once the decoder microservice has received the payload via REST, it decodes the data using its internal logic. The decoder microservice responds with a set of events, measurements, alarms and inventory updates to be created. It does not have to deal with persisting these entities itself.

5. The LWM2M agent takes the set of measurements, events, alarms and inventory updates to be created and persists them in the platform.

It is important to understand how the Cumulocity IoT LWM2M agent interacts with these decoder microservices.

## The LWM2M decoder interface

As explained above, the LWM2M agent sends the data to the microservice for decoding using a REST call, more specifically using JSON. Hence, the interface is defined by three parts.

1. REST endpoint

2. Request JSON body format

3. Response JSON body format

#### The REST endoint: /decode

Each LWM2M decoder microservice has to expose one REST endpoint using the path */decode*

#### Request JSON body format

When posting data to the decoder microservice, the LWM2M agents posts the data using the following JSON format:

```
{
   "args":{
      "resourcePath": "<<LWM2M Object Resource Path on the device>>"
   },
   "sourceDeviceId":"<<device Id>>",
   "serviceKey":"<<The service key of the decoder microservice>>",
   "value":"<<The value to be decoded (hex string)",
   "status":"IN_PROGRESS"
}
```

The LWM2M agent passes in the following fragments:

*args* - Additional arguments and meta information for processing the request. As of now, only the resource path is sent to the decoder microservice

*sourceDeviceId* - The ID of the source device in the Cumulocity IoT inventory

*serviceKey* - The service key of the microservice

*value* - The actual value to be decoded. The value is a series of bytes encoded as a hexadecimal string

*status* - The status of the event (typically "IN_PROGRESS")

**Example**:

```
{
   "args":{
      "resourcePath": "/33456/0/123"
   },
   "sourceDeviceId":"12345",
   "serviceKey":"my-decoder-microservice",
   "value":"83186a02831a5ae089e10000",
   "status":"IN_PROGRESS"
}
```

#### Response JSON body format

If the decoder microservice is able to handle the request, it has to respond with the following JSON format:

```
{
  "alarms": [<<Array of alarms to be created>>],
  "events": [<<Array of events to be created >>],
  "measurements": [<<Array of measurements to be created>>],
  "dataFragments" : [Map <<LWM2MPath>,<Value>>],
  "success": true || false
}
```

The fragments above are used as follows:

*alarms* - A list of alarms to be created by the LWM2M agent. The alarms have to be given in the ordinary [Cumulocity IoT alarm JSON format](https://cumulocity.com/guides/reference/alarms/). This can be an arbitrary alarm representation, for example carrying custom fields.

*events* - A list of events to be created by the LWM2M agent. The events have to be given in the ordinary [Cumulocity IoT event JSON format](https://cumulocity.com/guides/reference/events/), for example carrying custom fields.  This can be an arbitrary event representation, for example carrying custom fields.

*success* - An informative boolean flag (true or false) that indicates if decoding by the microservice was successful. Note that this field is independent from the retry mechanism above: Consider for example a device that is sending malformed data which cannot be processed by the microservice. In this case, the decoder invocation can be executed as expected. However, the microservice could detect the malformed payload and then it could use this field to signal that the payload could not be decoded.

*dataFragments* - The LWM2M agent persists all last-seen values for all LWM2M objects and their resources in the device managed object. The dataFragments field is a map that stores LWM2M resource values using LWM2M object-resource-paths as a key. The data fragments can be used by a decoder to hand over a set of fragment updates. Our example below shows how this could be used to insert values for two object resource paths into the device managed object.

Also note that the microservice does not need to set the source of measurements, events and alarms. The LWM2M agent always executes decoder mappings in the scope of a device and sets the source for events, alarms and measurements when it persists these entities to disk.

*measurements* - A list of measurements to be created by the LWM2M agent. The syntax here follows an own DTO format, like this example shows:

```json
  {
     "type":"c8y_example_lwm2m_decoder_binaryValues_byteIndex_1",
     "series":"binaryValueSeries",
     "time":"2019-02-07T11:05:55.272Z",
     "fragmentsToCopyFromSourceDevice":[
        "IMEI",
        "IMSI"
     ],
     "deviceFragmentPrefix":"device!Name",
     "includeDeviceName":false,
     "deviceNameFragment":null,
     "additionalProperties":{
        "foo":"bar"
     },
     "values":[
        {
           "seriesName":"byte 1",
           "unit":"unknown",
           "value":11
        }
     ]
  }
```

  *type*: Type of the measurement to be created

  *series*: Series of the measurement to be created

  *time*: Measurement timestamp

  *fragmentsToCopyFromSourceDevice*: The LWM2M agent can copy fragments from the LWM2M device managed object into measurements being created. This field allows the external decoder to control this behavior. If used, this field needs to contain a list of device fragment names. In the example above, the agent copies over the fragments "IMSI" and "IMEI" if present in the device managed object.

  *deviceFragmentPrefix*: Can be used in conjunction with fragmentsToCopyFromSourceDevice. If given, the agent prefixes the copied fragment names with the deviceFragmentPrefix. The agent then only copies data from the source device if there is a fragment in the device, for which the key is a concatenation of deviceFragmentPrefix and the fragment name. The property is null in the example above, but if the prefix was "MY_", the agent would look for "MY_IMEI" or "MY_IMSI".

  *includeDeviceName*: Boolean flag that controls if the device name is copied to the measurement

**Full decoder response sample**

```
{
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
"events": [
{
    "source": {
        "id":"12345" },
    "type": "TestEvent 1",
    "text": "Data was decoded",
    "time": "2020-03-03T12:03:12.845Z",
    "myFragment": "my data"
},
{
    "source": {
        "id":"12345" },
    "type": "TestEvent 2",
    "text": "More data was decoded",
    "time": "2020-03-04T12:05:27.845Z"
}
],
"measurements": [
    {
        "type": "c8y_example_lwm2m_decoder_binaryValues_byteIndex_0",
        "series": "binaryValueSeries",
        "time": "2019-02-07T11:05:55.271Z",
        "fragmentsToCopyFromSourceDevice": null,
        "deviceFragmentPrefix": null,
        "deviceNameFragment": null,
        "includeDeviceName": false,
        "additionalProperties": {},
        "values": [
            {
                "seriesName": "byte 0",
                "unit": "unknown",
                "value": 10
            }
        ]
    },
    {
        "type": "c8y_example_lwm2m_decoder_binaryValues_byteIndex_1",
        "series": "binaryValueSeries",
        "time": "2019-02-07T11:05:55.272Z",
        "fragmentsToCopyFromSourceDevice": null,
        "deviceFragmentPrefix": null,
        "deviceNameFragment": null,
        "includeDeviceName": false,
        "additionalProperties": {},
        "values": [
            {
                "seriesName": "byte 1",
                "unit": "unknown",
                "value": 11
            }
        ]
    },
    {
        "type": "c8y_example_lwm2m_decoder_binaryValues_byteIndex_2",
        "series": "binaryValueSeries",
        "time": "2019-02-07T11:05:55.272Z",
        "fragmentsToCopyFromSourceDevice": null,
        "deviceFragmentPrefix": null,
        "deviceNameFragment": null,
        "includeDeviceName": false,
        "additionalProperties": {},
        "values": [
            {
                "seriesName": "byte 2",
                "unit": "unknown",
                "value": 12
            }
        ]
    }
],
"dataFragments":[
    {
       "value":"12345",
       "key":"/999/433/3"
    },
    {
       "value":"Hello World",
       "key":"/45678/0/1234"
    }
 ],
"success": true
}
```

## Implementing decoder microservices:

Decoder microservices can be easily built on top of [Cumulocity IoT Microservices](http://www.cumulocity.com/guides/microservice-sdk/java).
In order to serve as a LWM2M decoder microservice, two requirements have to be met

1. The microservice needs to be marked as decoder microservice in the microservice manifest
2. The microservice needs to provide a simple decoder REST endpoint (/decode), as described above

### Marking a microservice as decoder

In order to enable Cumulocity IoT to discover a decoder microservice, it needs to be marked as decoder microservice in the `cumulocity.json` file. This can be done by adding a simple additional fragment (`isDecoder`). In this example, we use

     "isDecoder": {  
        "name":"Binary Series Decoder"  
     }  

to mark the example as Binary series decoder.

## The binary series decoder example

In this repository, you'll find a very straightforward decoder example, the binary series decoder. It is implemented in Spring Boot.

The following code block brings up the REST endpoint:

```java
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public DecoderResult decodeWithJSONInput(@RequestBody DecoderInputData inputData) throws DecoderServiceException, IOException {
        return byteSequenceDecoderService.decode(inputData.getValue(), GId.asGId(inputData.getSourceDeviceId()), inputData.getArgs());
    }
```

As you can see, our code already provides input types for the decoder input data and the decoder result. Have a look at the `DecoderResult` and `DecoderInputData` classes  for more context.

The actual decoding is done in a dedicated service method:

```java
    @Override
    public DecoderResult decode(String payloadToDecode, GId sourceDeviceId, Map<String, String> inputArguments) throws DecoderServiceException {

        log.debug("Decoding payload {}. Converting hex string into values", payloadToDecode);

        byte[] decodedBytes = DecoderUtils.hexStringToByteArray(payloadToDecode);

        log.debug("Byte values: {}",decodedBytes);

        DecoderResult decoderResult = new DecoderResult();

        int byteIndex=0;

        for (byte valueByte: decodedBytes) {
            List<MeasurementValueDto> measurementValueDtos = new ArrayList<>();

            log.debug("Creating Measurement for byte {}, value {}", byteIndex,valueByte);
            MeasurementValueDto valueDto = new MeasurementValueDto();
            valueDto.setValue(new BigDecimal(valueByte));
            valueDto.setSeriesName("byte "+byteIndex);
            valueDto.setUnit("unknown");
            measurementValueDtos.add(valueDto);

            MeasurementDto measurementDto = new MeasurementDto();
            measurementDto.setType("c8y_example_lwm2m_decoder_binaryValues_byteIndex_"+byteIndex);
            measurementDto.setTime(new DateTime());
            measurementDto.setValues(measurementValueDtos);
            measurementDto.setSeries("binaryValueSeries");
            decoderResult.addMeasurement(measurementDto);

            byteIndex++;
        }



        log.debug("Finished decoding byte values");
        return decoderResult;

    }
```

As you can see, the code simply iterates over the given bytes in the hex string and turns them into measurements.

## Trying out the example

In order to build this microservice, please make sure you are able to build and to deploy Cumulocity IoT Microservices, as described in the [Microservice Guide](http://www.cumulocity.com/guides/microservice-sdk/java/)

Clone this repository first. Next, build the microservice using `mvn clean install`. The build will create a zip file of the decoder microservice.

In the next step, deploy the microservice using Cumulocity IoT UI. Once the decoder microservice has been deployed, wait a couple of minutes in order to allow Cumulocity IoT to discover the new decoder. Then, open Device Management UI. Inside Device Protocols, you should now see the decoder inside the selector of Custom Actions. After you have configured it against an OPAQUE resource, it should decode binary data into measurements.
