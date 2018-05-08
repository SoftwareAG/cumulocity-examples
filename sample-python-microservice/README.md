### Building application

Building requires installed and running docker.

To build .zip package simply run:
    
    ./build.sh
    
This will use our ["microservice"](https://www.cumulocity.com/guides/reference/microservice-package/) script to create docker image, add manifest file `cumulocity.json`, and pack them together as microservice application "hello-microservice.zip", which is ready to upload into cumulocity platform. 

### Deploying application

To deploy packed microservice from console on running platform, use following command for "microservice" script:

    microservice deploy subscribe -n sample-microservice -d {url} -u {username} -p {password} -te {tenant}

where `{url}` is base address of the platform, and `{username}` has form `{tenant}/{user}` (eg. `management/admin`). 

Deploy can be also done manually using installed cumulocity UI.

