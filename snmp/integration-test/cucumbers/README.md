# Prerequisite to run integration tests #
1. Copy `src/test/resources/cucumber.properties` file to `${user.home}/.snmp/` directory and adjust the properties if necessary
2. Put snmp-agent-gateway executable to the mentioned directory for `gateway.jar.location` in `cucumber.property` file or adjust accordingly

# Run integration tests #
1. To run all 
`$ mvn clean install -P integration-tests`

2. To run with tags
`$ mvn clean install -Dcucumber.options="--tags @..." -P integration-tests`
