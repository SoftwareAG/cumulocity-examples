##### The repository contains an example Java MQTT client with support for X509.certificates.

Before run, configuration is required. 

Place in resource folder keystore and truststore file. 

In C8yMqttClient.java provide:

* **KEYSTORE** - key store file name
* **TRUSTSTORE** - trust store file name
* **PASSWORD** - (optional) password for user only for HTTP requests 
* **USER** - (optional) user only for HTTP requests
* **TENANT** - (optional) used only for HTTP requests
* **CLIENT_ID** - client Id which matches the certificate  
* **BROKER_URL** - URL for mqtt connection, note that Cumulocity IoT expects devices to connect using SSL on port 1884

In HttpClientHelper.java provide:

* **FINGERPRINT** - (optional) fingerprint for root certificate, used only for HTTP requests
* **CERT_IN_PEM_FORMAT** - (optional) root certificate in pem format, used only for HTTP requests

In the first step if data required for HTTP requests are provided client try to upload root certificate.  
After that it tries to establish mqtt connection using provided BROKER_URL.  
Successful connection is indicated by message:  
Connect complete  
payload>  

Some predefined commands available:
* **initialize** - if device connecting for the first time, the first message have to create device.
* **subscribe** - will subscribe MQTT client on _s/ds_, _s/e_, _s/dat_
* **upload-root** -  will upload root certificate
* **delete-root** - will delete root certificate
* **publish-token** - will publish empty message on topic _s/uat_, then if client is subscribed on _s/dat_ topic, after a while will receive JWT token
* **exit**/ **quit** - if data required for HTTP requests are provided client try to delete root certificat and close connection

Besides, all of templates can be used eg. for create measurement type:     
payload> 211,1  
payload> Delivery complete on topic : [s/us] 211,1