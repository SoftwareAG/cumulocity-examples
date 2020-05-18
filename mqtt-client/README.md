##### The repository contains an example Java MQTT client with support for X509.certificates.  
MQTT client example is documented on the online documentation (refer to [Examples](https://cumulocity.com/guides/device-sdk/mqtt-examples/#hello-mqtt-java-with-certificates) in the Device SDK guide).

Before run, configuration is required. 

Place in resource folder keystore and truststore file. 

In C8yMqttClient.java provide:


* **KEYSTORE_NAME** -  key store file name (placed in resource folder)  
* **KEYSTORE_PASSWORD** - key store password
* **KEYSTORE_FORMAT** - key store format (eg. 'jks')  
* **TRUSTSTORE_NAME** - trust store file name (placed in resource folder)  
* **TRUSTSTORE_PASSWORD** - trust store password  
* **TRUSTSTORE_FORMAT** - trust store format (eg. 'jks')  
* **CLIENT_ID** - client Id which matches the certificate  
* **BROKER_URL** -URL for mqtt connection, note that Cumulocity IoT expects devices to connect using SSL on port 1884  


After start, client tries to establish mqtt connection using provided BROKER_URL.  
Successful connection is indicated by message:  
Connect complete  
