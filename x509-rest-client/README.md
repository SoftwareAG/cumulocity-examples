##### The repository contains an example Java REST client with support for X509.certificates.  

Before run, configuration is required. 

Place in resource folder keystore and truststore file. 

In X509RestClient.java provide:


* **KEYSTORE_NAME** -  key store file name (placed in resource folder)  
* **KEYSTORE_PASSWORD** - key store password
* **KEYSTORE_FORMAT** - key store format (eg. 'jks')  
* **TRUSTSTORE_NAME** - trust store file name (placed in resource folder)  
* **TRUSTSTORE_PASSWORD** - trust store password  
* **TRUSTSTORE_FORMAT** - trust store format (eg. 'jks')  
* **CLIENT_ID** - client Id which matches the certificate subject common name  
* **PLATFORM_URL** - URL for mTLS connection
* **PLATFORM_MTLS_PORT** - Port for established mTLS connection using certificates
* **X_SSL_CERT_CHAIN** - constant for header key `x-ssl-cert-chain`
* **DEVICE_ACCESS_TOKEN_PATH** - API endpoint for making mTLS protocol
* **LOCAL_DEVICE_CHAIN** - value of header key `x-ssl-cert-chain` which contains full device chain not a mandatory header. If immediate issuer of device certificate is present in platform's truststore then we don't need this header.


After running, client tries to establish mTLS connection using provided PLATFORM_URL, endpoint and client full chain.  
Successful connection provide device access token: in response with 200 HTTP code.