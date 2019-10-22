### Build the application

Building requires a local Docker installation.

To build a ZIP package simply execute:
    
```shell
$ ./build.sh
```
  
This will use our [microservice script](https://www.cumulocity.com/guides/reference/microservice-package/) to create a Docker image, add the manifest file *cumulocity.json* and pack them together as a microservice application "sample-microservice.zip", which is ready to be uploaded to the Cumulocity platform. 

### Deploy the application

To deploy a packed microservice from employing the CLI, use the following command:

```shell
$ microservice deploy subscribe -n sample-microservice -d <URL> -u <USERNAME> -p <PASSWORD> -te <TENANT_ID>

where `<URL>` is your tenant domain and the rest are your Cumulocity credentials.
