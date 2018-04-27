# Overview

Microservice generates "c8y_RouteStart" and "c8y_RouteEnd" events basing on "GPS_Speed" measurements and "my_bile_movement_stop" events sent from device. 

## Running the agent.

### Prerequisites

Microservice needs to be built using maven and requires java 8.

### Compiling

In project directory just run:

    mvn clean install


### Uploading
To upload microservice to the Cumulocity platform, first configure credentials to your tenant in settings.xml file as follows: 

	<server>
	    <id>microservice</id>
	    <username>{{tenant}}/{{username}}</username>
	    <password>{{password}}</password>
	    <configuration>
            <url{{url}}</url>
	    </configuration>
	</server>
	
Now just run

    mvn microservice:upload

Your service should be uploaded to the tenant. You still need to subscribe the tenant to the application in order to use it.

# Implementation

Logic is implemented as follows for every device:
* every MeasurementCreated("GPS_Speed") generates SpeedMeasurement
* first SpeedMeasurement generates TriggerPause
* every pair (SpeedMeasurement, EventCreated("my_bike_movement_stop")) generates TriggerPause
* every pair (TriggerPause, SpeedMeasurement) generates TriggerTrip
* every pair (TriggerPause, TriggerTrip) generates CreateEvent("c8y_RouteStart")
* every pair (TriggerTrip, TriggerPause) generates CreateEvent("c8y_RouteEnd")

Where
* MeasurementCreated("GPS_Speed") is new measurement containing fragment "GPS_Speed" sent from the device to the Cumulocity platform.
* EventCreated("my_bike_movement_stop") is new event with type "my_bike_movement_stop" sent from the device to the Cumulocity platform.
* CreateEvent("c8y_RouteStart") is new event with type "c8y_RouteStart" generated as result of transformation implemented in microservice.
* CreateEvent("c8y_RouteEnd") is new event with type "c8y_RouteEnd" generated as result of transformation implemented in microservice.
* SpeedMeasurement, TriggerPause, TriggerTrip are only intermediate steps of calculation. 