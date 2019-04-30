#!/bin/bash
source ${BASH_SOURCE%/*}/common.sh
./mvnw clean -q -s $MVN_SETTINGS
./mvnw release:clean -q

