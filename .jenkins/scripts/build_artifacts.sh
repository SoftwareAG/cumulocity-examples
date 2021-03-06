#!/bin/bash
set -e
export MAVEN_OPTS="-Xmx2048m -XX:MetaspaceSize=1024m ${MAVEN_OPTS}"

./mvnw -B install \
  -s $MVN_SETTINGS -U \
  -DskipTests \
  -Dskip.agent.package.container=false \
  -Dskip.microservice.package=false "$@"
