#!/bin/sh
set -e
# ./mvnw clean deploy -DskipTests -s $MVN_SETTINGS
#/var/lib/jenkins/bin/deploy2yum.sh -p $(find ./ -name *.rpm)
if [ "!$1" = "!release" ]
then
/var/jenkins_home/bin/deploy2yum.sh -p $(find ./ -name *.rpm)
fi
if [ "!$1" = "!snapshot" ]
then
    /var/jenkins_home/bin/deploy2yum.sh $(find ./ -name *.rpm)
fi