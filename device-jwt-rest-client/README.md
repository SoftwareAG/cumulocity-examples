##### The repository contains an example Java REST client with support for X509.certificates.  
Java REST client example is documented on the online documentation (refer to [Examples](https://cumulocity.com/guides/device-sdk/device-rest-client/#rest-java-with-certificates) in the Device SDK guide).

Before run, configuration is required. 

Place in resource folder keystore and truststore file. 

In DeviceAccessTokenRestClient.java provide:


* **KEYSTORE_NAME** -  key store file name (placed in resource folder)  
* **KEYSTORE_PASSWORD** - key store password
* **KEYSTORE_FORMAT** - key store format (eg. 'jks')  
* **TRUSTSTORE_NAME** - trust store file name (placed in resource folder)  
* **TRUSTSTORE_PASSWORD** - trust store password  
* **TRUSTSTORE_FORMAT** - trust store format (eg. 'jks')  
* **CLIENT_ID** - client Id which matches the certificate subject common name  
* **PLATFORM_URL** - URL for mTLS connection
* **X_SSL_CERT_CHAIN** - constant for header key `x-ssl-cert-chain`
* **DEVICE_ACCESS_TOKEN_PATH** - API endpoint for making mTLS protocol
* **LOCAL_DEVICE_CHAIN** - value of header key `x-ssl-cert-chain` which contains full device chain


After running, client tries to establish mtls connection using provided PLATFORM_URL, endpoint and client full chain.  
Successful connection provide device access token: in response with 200 HTTP code.