#!/bin/bash
set -e
./mvnw clean install dependency:go-offline de.qaware.maven:go-offline-maven-plugin:resolve-dependencies -s $MVN_SETTINGS -U -DskipTests -T 4