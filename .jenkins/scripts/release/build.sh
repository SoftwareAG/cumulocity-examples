#!/bin/bash
set -e
./mvnw clean install -s $MVN_SETTINGS -U -Dskip.microservice.package=true -Dskip.agent.package.container=true -DskipTests -Dchangelist=

