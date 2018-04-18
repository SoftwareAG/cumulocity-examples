## Building application

Building requires installed and running docker.

To build .zip package simply run:
    
    ./build_app.sh
    
This will create docker image of application, add manifest file `cumulocity.json`, and pack them together as microservice application "hello-microservice.zip", which is ready to upload into cumulocity platform.

