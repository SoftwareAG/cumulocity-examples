# Introduction
This example microservice demonstrates how to create a subscription and consume a stream of notifications using WebSocket protocol. The example microservice first creates a subscription and then proceeds to connect to the WebSocket server after obtaining the authorization token.

The example microservice creates the following subscription to the specified device:

```json
{
  "source": { "id": "<DEVICE_ID>" },
  "context": "mo",
  "subscription": "<SUBSCRIPTION_NAME>",
  "subscriptionFilter": {
    "apis": ["measurements"],
    "typeFilter": "c8y_Speed"
  },
  "fragmentsToCopy": ["c8y_SpeedMeasurement", "c8y_MaxSpeedMeasurement"]
}
```

The example microservice first creates the above subscription and then uses it to obtain a token. This token is used to access the subscription's WebSocket channel. Next a WebSocket client is connected; the client will listen for notifications of messages that meet the subscription criteria as they are sent to Cumulocity by the device.

In the above subscription example, we have expressed interest in receiving only measurements that bear the type of `c8y_speed`. The `fragmentsToCopy` property further transforms the filtered measurement to *only* include c8y_SpeedMeasurement and c8y_MaxSpeedMeasurement fragments.

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
  "source": {"id":"<DEVICE_ID>"},
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


## Prerequisite
- cumulocity tenant with microservice hosting feature enabled
    - for free trial account on cumulocity.com you will need to request this feature by contacting
- contents of this repository

### Instructions
1. Creating a test user
    - From the Application Switcher select Administration
    - In the Navigation pane expand Accounts and go to 'Users'
    - Click on 'Add user', fill in the form for the new user, untick "Send password reset link as email" and provide a password
    - Click Save
    - Enable notification support:
      - Using Navigation pane go to Accounts and then Roles, click on "Add global role"
      - Name the role "Notifications" and from the list of permissions tick "Notification 2" in Admin column
      - click Save
      - go to Accounts then Users
      - in the list find the username created a few steps before and under "Global roles" open the dropdown
      - find and tick "Notifications" and click "Apply"
2. Creating a test device
    - open the demo directory
    - edit the `measurements.py` script and provide the following credentials for your newly created user as well as the platform url:
      ```python
      _tenant_id = ''
      _username = ''
      _password = ''
      _platform_url = ''
        ```
    - create a device and make note of its id:
       ```console
       user@host:~$ python3 measurements.py device
       user@host:~$ Device id: <DEVICE_ID>
       ```
3. Build the microservice
    - edit the `application.properties` located in `src/main/resources`
        - supply the device id in `example.source.id`
    - build the microservice using `mvn clean install`
        - the microservice will build under `target/` and will be named `hello-notification-1011.62.0-SNAPSHOT.zip`
4. Deploy the microservice
   - by uploading application to the platform:
       - back in your browser, in Administration dashboard, from Navigation expand Applications and select Own applications
       - click on Add application, Upload Microservice and Upload file
       - select the `zip` microservice file built in step 3
       - when prompted to subscribe select `Dont' subscribe`
       - switch to the test user, under Applications, Own applications select `Hello-notification` and subscribe
   - by running the application locally:
     - edit the `application.properties`
     - uncomment the line containing `C8Y.baseURL` entry and provide the cumulocity platform url
     - start the application
5. Start sending measurements
   - run the `measurements.py` script again with the following parameters:
       ```console
       user@host:~$ python measurements.py send <DEVICE_ID> <DURATION_IN_SECONDS>
       user@host:~$ Measurement created [201]
       user@host:~$ Measurement created [201]
                    ...
       ```
   1. (Optional) Delete the device
      - run the script again with these parameters:
       ```console
       user@host:~$ python measurements.py delete <DEVICE_ID>
       user@host:~$ Device id: <DEVICE_ID> deleted
       ```

