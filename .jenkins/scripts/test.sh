#!/bin/bash
set -e
export MAVEN_OPTS="-Xmx2048m -XX:MetaspaceSize=1024m ${MAVEN_OPTS}"

./mvnw -B test -s $MVN_SETTINGS -U "$@"

