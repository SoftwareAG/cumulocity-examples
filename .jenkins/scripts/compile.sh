#!/bin/bash
set -e
export MAVEN_OPTS="-Xmx2048m -XX:MetaspaceSize=1024m -XX:+TieredCompilation -XX:TieredStopAtLevel=1 ${MAVEN_OPTS}"

./mvnw -B install -s $MVN_SETTINGS -U -DskipTests -Dskip.agent.package.container=true -Dskip.microservice.package=true "$@"

# -Dchangelist=
