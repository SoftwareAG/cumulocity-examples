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

### Building ###
Clone/download this repository and build using `mvn clean install`

### Running ###
* For MIB Parser microservice, refer to the README.md in mib-parser module
* For SNMP Agent Gateway, refer to the README.md in snmp-device-gateway module

