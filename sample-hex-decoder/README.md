# Sample Hex Decoder
### About

A sample decoder that can be used in conjunction with the Impact microservice. Such decoders are defined by an empty `c8y_ImpactDecoder` fragment in their manifest. They expose a single endpoint - `/decode`. 

### Inbound Data Structure

The decoder expects 28 character hexadecimal ASCII string as input. It should be interpreted as 14 bytes of binary data. The format is as follows:

| Byte 0 | Byte 1      |
|--------|-------------|
| Flags  | Temperature |         

| Byte 2    | Byte 3   | Byte 4   | Byte 5   |
|-----------|----------|----------|----------|
| Latitude  | Latitude | Latitude | Latitude |
 
| Byte 6    | Byte 7    | Byte 8    | Byte 9    |
|-----------|-----------|-----------|-----------|
| Longitude | Longitude | Longitude | Longitude |

| Byte 10         | Byte 11         | Byte 12 | Byte 13 |
|-----------------|-----------------|---------|---------|
| Battery Voltage | Battery Voltage | RSSI    | SNR     |

Where:

 - Flags - the Flags byte is a bitmask where:
    * bit 0 - unused
    * bit 1 - is triggered by an accelerometer
    * bit 2 - is triggered by a button
 - Temperature - signed 8-bit integer
 - Latitude and Longitude - IEEE 754 single-precision 32-bit floats
 - Battery Voltage - 16-bit signed integer
 - RSSI and SNR - 8-bit unsigned integers 

### Outbound Cumulocity Data

This data is mapped to the following cumulocity data:

 - **Accelerometer alarm** - created if the accelerometer flag is set:

```json
 {  
     "source":{  
         "id":"<Target device ID>"
     },
     "type":"c8y_AccelerometerAlarm",
     "text":"Transmission was triggered by accelerometer",
     "severity":"MAJOR",
     "time":"<Timestamp from the Report>"
 }
```

 - **Button event** - created if the button flag is set:

```json
{  
    "source":{  
        "id":"<Target device ID>"
    },
    "type":"c8y_ButtonEvent",
    "text":"Transmission was triggered by button press",
    "time":"<Timestamp from the Report>"
}
```

 - **Location event** - Created using Latitude and Longitude values:

```json
{  
    "source":{  
        "id":"<Target device ID>"
    },
    "type":"c8y_LocationUpdate",
    "text":"Location updated",
    "time":"<Timestamp from the Report>",
    "c8y_Position":{
        "lat":51.2277,
        "lon":6.7734
    }
}
```

 - **Measurement** - Created using the battery, RSSI, SNR and Temperature values:

```json
{  
    "source":{  
        "id":"<Target device ID>"
    },
    "type":"c8y_LoraDemonstratorTelemetry",
    "time":"<Timestamp from the Report>",
    "c8y_Temperature":{  
        "T": { "value": 23 }
    },
    "c8y_Battery":{  
        "voltage": { "value": 3347 }
    },
    "c8y_SignalStrength":{  
        "RSSI": { "value": -98 },
        "SNR": { "value": 15 }
    }
}
```
