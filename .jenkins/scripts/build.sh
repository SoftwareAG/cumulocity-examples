#!/bin/bash
set -e
./mvnw -B install -s $MVN_SETTINGS -U "$@"

