## Cumulocity IoT SNMP Agent Gateway ##

The SNMP agent is a stand-alone Java program that communicates with the SNMP enabled device(s) and the Cumulocity IoT platform. It receives SNMP data from the devices, converts the data to Cumulocity IoT-based objects based on the device protocol mapping, persists the data locally, and forwards the data to Cumulocity IoT. The agent has to be registered in Cumulocity IoT before serving the device request.

### Build Prerequisites ###
Refer to the "**Build Prerequisites**" section of the [Readme][3] under the snmp folder.

### Building ###
Refer to the "**Building**" section of the [Readme][3] under the snmp folder.

Build generates `snmp-agent-gateway-<version>-1.noarch.rpm` RPM package under the `target/rpm/c8y-mib-parser/RPMS/noarch/` folder.

For installation and usage details of the SNMP Agent Gateway, please refer to [SNMP][1] section of the [Cumulocity IoT user guide][2].

[1]: https://cumulocity.com/guides/users-guide/optional-services/#snmp
[2]: https://cumulocity.com/guides/users-guide/getting-started/
[3]: ../README.md

