#!/bin/sh
MVN_GOAL="com.nsn.cumulocity.dependencies:3rd-license-maven-plugin:3rd-tpp-fetcher-scan"
if [ -z "$TPP_FETCHER_URL" ]; then TPP_FETCHER_URL="http://172.30.0.129:8083"; fi
if [ -n "$1" ]
  then
    git checkout "c8y-examples-$1"
fi
TPP_URL_PARAM="-Dtpp.fetcher.url=$TPP_FETCHER_URL"
ENABLE_SCAN="-Dtpp.fetcher.scan.enabled=true"
MVN_SCAN_TPP="$MVN_GOAL $TPP_URL_PARAM $ENABLE_SCAN"

./mvnw -B -f ./tracker-agent/pom.xml $MVN_SCAN_TPP -Dtpp.fetcher.project.name=tracker-agent
./mvnw -B -f ./sample-hex-decoder/pom.xml $MVN_SCAN_TPP -Dtpp.fetcher.project.name=impact-custom-processor
./mvnw -B -f ./sample-json-decoder/pom.xml $MVN_SCAN_TPP -Dtpp.fetcher.project.name=impact-custom-processor
./mvnw -B -f ./snmp/snmp-device-gateway/pom.xml $MVN_SCAN_TPP -Dtpp.fetcher.project.name=snmp-agent-gateway
./mvnw -B -f ./snmp/mib-parser/pom.xml $MVN_SCAN_TPP -Dtpp.fetcher.project.name=c8y-mib-parser
