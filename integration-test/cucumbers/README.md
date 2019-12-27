- Prerequisite to run integration tests -
1. Put cucumber.properties file to ${user.home}/.snmp/ directory and adjust the properties if necessary
2. Put snmp-gateway executable to the mentioned directory in cucumber.property file or adjust accordingly

- Run integration tests -
To run all:
$ mvn clean install -P integration-tests
To run with tags:
$ mvn clean install -Dcucumber.options="--tags @..." -P integration-tests
