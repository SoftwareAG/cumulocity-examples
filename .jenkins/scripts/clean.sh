#!/bin/bash
./mvnw clean -q -s $MVN_SETTINGS
./mvnw release:clean -q -s $MVN_SETTINGS

