#!/bin/bash
set -e
./mvnw install -s $MVN_SETTINGS -U "$@"

