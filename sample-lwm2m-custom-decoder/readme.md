# Writing your own LW2M Custom Action using Decoder Microservices
## Introduction
Cumulocity IoT is able to  fully integrate most Lightweight M2M (LWM2M) devices out of the box, **without the need to write code.** As long as devices solely use standard LWM2M data types (String, Integer, Floats...), the [existing mapping functionalities of Cumulocity LWM2M](https://www.cumulocity.com/guides/users-guide/optional-services#lwm2m) are completely sufficient

In certain cases, Lightweight M2M devices however expose proprietary data, for example binary arrays, often using the LWM2M Opaque data type. In these cases, it is impossible to defer the structure of the data from the DDF XML.  In order to enable Cumulocity IoT users to process such data,  so-called Custom Actions can be used to trigger so-called *decoder microservices.* 

## Decoder Microservices
Decoder Microservices are built on top of [Cumulocity Microservices](http://www.cumulocity.com/guides/microservice-sdk/java). 
In order to serve as a LWM2M decoder microservice, two requirements have be met

 1. The microservice needs to marked as decoder microservice in the microservice manifest 
 2. The microservice needs to provide a simple decoder REST endpoint (`/decode)

In the following, we illustrate how to write such a decoder microservice along the example in this repository - a binary series decoder. As you are going to see, our microservice simply takes a set of bytes and converts them into individual measurements.

### 1. Marking a microservice as decoder

In order to enable Cumulocity IoT to discover a Decoder Microservice, it needs to be marked as decoder microservice in the `cumulocity.json` file. This can be done by adding a simple additional fragment (`isDecoder`). For example, in this example, we use 


     "isDecoder": {  
        "name":"Binary Series Decoder"  
     }  
to mark the example as Binary Series Decoder.

### 2. Provide a decoder endpoint (`/decode`)

#### Input Format
Cumulocity IoT posts the raw data obtained from a Lightweight M2M device to this endpoint, along with certain meta data. For example, it would post three bytes (10,11,12) in the following way. 

   

    { 
           	"value" : "0A0B0C"
    }

In addition to the value, Cumulocity IoT passes also other meta data along, for example the ID of the source device (in a fragment called `sourceDevice)`. We kindly ask you to look at the DecoderInputData class in  this example for a full set of available meta data

### Response Format

Decoder Microservices do not need persist entities such as measurements themselves. Instead, they simply respond with a list of decoded entities. For example, the binary series decoder in this repository responds as follows:

    
    "alarms": null,
    "events": null,
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
    "success": true
}

Based on the Input Value, the microservice creates three measurements. It does not create events or alarms. Please also note that the microservice does not need to set the source of these entities. Cumulocity IoT executes decoder mappings always in the scope of a device and sets the source for events, alarms and measurements when it persists these entities to disk.

## Trying out the example

In order to build this microservice, please make sure you are able to build and to deploy Cumulocity Microservices, as described in the [Microservice Guide](http://www.cumulocity.com/guides/microservice-sdk/java/)

Clone this repository first. Next, build the microservice using `mvn clean install`. The build will create a zip file of the decoder microservice.

In the next step, deploy the microservice using Cumulocity UI. Once the decoder microservice has been deployed, wait a couple of minutes in order to allow Cumulocity IOT to discover the new decoder. Then, open Device Management UI. Inside Device Protocols, you should now see the decoder inside the selector of Custom Actions

