#!/bin/bash
set -e
./mvnw clean install -s $MVN_SETTINGS -U -DskipTests -Dskip.agent.package.container=true -Dskip.microservice.package=true
