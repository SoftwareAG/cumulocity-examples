#!/bin/bash
./mvnw -B clean -q -s $MVN_SETTINGS
./mvnw -B release:clean -q -s $MVN_SETTINGS

