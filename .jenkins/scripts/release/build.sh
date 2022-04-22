#!/bin/bash
set -e
./mvnw -B clean install -s $MVN_SETTINGS -U -Dskip.microservice.package=true -Dskip.agent.package.container=true -DskipTests -Dchangelist=

