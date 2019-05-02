#!/bin/sh
set -e
./mvnw clean deploy -DskipTests -Dnexus.host=http://nexus:8081 -s $MVN_SETTINGS
/var/jenkins_home/bin/deploy2yum.sh -p $(find ./ -name *.rpm)