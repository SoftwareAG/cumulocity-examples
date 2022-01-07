## Example Notification 2.0 Microservice
This example microservice shows you how to create a subscription & consume a stream of notifications using WebSocket protocol. Before running this example microservice, please make sure to have created a device and specified its ID in `example.source.id` property located in applications.properties. As part of cleanup, you may wish to delete this device after you have finished running this example. The example microservice first creates a subscription and then proceeds to connect to the WebSocket server after obtaining the authorization token.

The example microservice creates the following subscription to the specified device:

```json
{
  "source": { "id": "<DEVICE ID>" }, 
  "context": "mo", 
  "subscription": "<SUBSCRIPTION NAME>", 
  "subscriptionFilter": {
    "apis": ["measurements"], 
    "typeFilter": "c8y_Speed"
  }, 
  "fragmentsToCopy": ["c8y_SpeedMeasurement", "c8y_MaxSpeedMeasurement"]
}
```
The example first creates the above subscription and uses it to create a token that permits access to that subscription. Then a WebSocket client using that token is connected; the client will now start receiving notifications of messages that meet the subscription criteria as they are sent to Cumulocity by the device.

In the above example, we have expressed interest in receiving only measurements that bear the type `c8y_speed`. The `fragmentsToCopy` property further transforms the filtered measurement to *only* include c8y_SpeedMeasurement and c8y_MaxSpeedMeasurement fragments.

As an example, if we post the following measurement from the specified device that meets our filter criteria above:

```json
{
  "c8y_SpeedMeasurement": {
    "T": {
      "value": 100,
      "unit": "km/h"
    }
  },
  "c8y_Speed2Measurement": {
    "T": {
      "value": 150,
      "unit": "km/h"
    }
  },
  "c8y_Speed3Measurement": {
    "T": {
      "value": 200,
      "unit": "km/h"
    }
  },
  "c8y_MaxSpeedMeasurement": {
    "T": {
      "value": 300,
      "unit": "km/h"
    }
  },            
  "time":"2021-06-11T17:03:14.000+02:00",
  "source": {"id":"<DEVICE ID>"},
  "type": "c8y_Speed"
}
```
we will receive the following message as we have specified to transform the filtered measurement to only include c8y_SpeedMeasurement and c8y_MaxSpeedMeasurement.

```json
{
  "c8y_SpeedMeasurement": {
    "T": {
      "value": 100, 
      "unit": "km/h"
    }
  }, 
  "c8y_MaxSpeedMeasurement": {
    "T": {
      "value": 300,
      "unit": "km/h"
    }
  },
  "time":"2021-06-11T17:03:14.000+02:00",
  "source": {"id":"<DEVICE ID>"},
  "type": "c8y_Speed"
}
```

## Device creation and measurement sending

The included [Python script](demo/measurements.py) has everything to get you started with the microservice and has example code for device creation, deletion and sending out measurements.
To use the script you will need to provide authentication details, edit the script and provide values to the following fields:

```python
_tenant_id = ''
_username = 'admin'
_password = ''
_platform_url = ''
```

To use, run the script with one of the options:

### To create a device
```console
user@host:~$ python measurements.py device
user@host:~$ Device id: <DEVICE_ID>
```

After creating the device you need to enter its ID in `example.source.id` property and deploy the microservice, before proceeding with sending out measurements.

### To start sending measurements
```console
user@host:~$ python measurements.py send <DEVICE_ID> <DURATION_IN_SECONDS>
user@host:~$ Measurement created [201]
user@host:~$ Measurement created [201]
...
```

### To delete a device
```console
user@host:~$ python measurements.py delete <DEVICE_ID>
user@host:~$ Device id: <DEVICE_ID> deleted
```



