#!/bin/bash
set -e
./mvnw clean install -s $MVN_SETTINGS -U -DskipTests -Dskip.agent.package.container=true -Dskip.microservice.package=true -T 4
./mvnw de.qaware.maven:go-offline-maven-plugin:resolve-dependencies