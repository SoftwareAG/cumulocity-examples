#!/bin/bash
set -e
./mvnw clean install -s $MVN_SETTINGS -U -e -T 4

