## Cumulocity IoT SNMP Agent ##
Simple Network Management Protocol (SNMP) is an application layer protocol, used widely in network management for monitoring network devices.

Two components are required for SNMP enabled devices to connect to the Cumulocity IoT platform

* The MIB parser microservice, which helps in converting a Managed Information Base (MIB) file to a JSON representation which is then used to create a device protocol.
* The SNMP agent Gateway, which is a device-side agent that helps SNMP enabled devices to connect to the Cumulocity IoT platform.
  
  Agent translates messages from an SNMP-specific format to a Cumulocity IoT model before forwarding them to the Cumulocity IoT platform.

Please see README.md of each sub-module for a detailed description.

### Sub-modules ###
* mib-parser - Microservice to parse the MIB files and create device protocol objects in the Cumulocity Platform.
* snmp-device-gateway - SNMP Agent Gateway is a device-side agent, which connects/listens to SNMP devices. It is represented as a device in the Cumulocity Platform.
* integration-test - Integration tests

### Build Prerequisites ###
Ensure the following prerequisite packages are installed before you configure and build SNMP packages.

* Git 
```
  sudo yum install git -y
```
* Java
```
  sudo yum install java-1.8.0-openjdk-devel.x86_64
```
* [Maven][1]
```
  sudo yum install -y apache-maven
  mkdir $Home/.m2/
  copy settings.xml inside $Home/.m2
```
* RPM
```
  sudo yum install rpm-build -y
```
* [Docker][2] required for microservice build 
```
  sudo yum update -y
  sudo yum install -y docker
  sudo service docker start
  sudo usermod -a -G docker ec2-user
```

  **Note:** Running docker images command can result in permission error. Ensure that `/var/run/docker.sock` has required permissions for the logged in user.

### Building ###
* Clone the repository 
```
  git clone https://bitbucket.org/m2m/cumulocity-examples.git
  cd cumulocity-examples
  git fetch --all –tags
  git checkout tags/c8y-examples-1006.6.0
  cd snmp
```
* Build
```
  mvn clean install
```

### Running ###
* For MIB Parser microservice, refer to the README.md in mib-parser module
* For SNMP Agent Gateway, refer to the README.md in snmp-device-gateway module


[1]: https://docs.aws.amazon.com/neptune/latest/userguide/iam-auth-connect-prerq.html
[2]: https://www.lewuathe.com/how-to-install-docker-in-amazon-linux.html

