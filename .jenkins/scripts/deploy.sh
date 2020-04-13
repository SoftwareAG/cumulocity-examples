#!/bin/sh
set -e
# ./mvnw clean deploy -DskipTests -s $MVN_SETTINGS
#/var/lib/jenkins/bin/deploy2yum.sh -p $(find ./ -name *.rpm)
if [ "!$1" = "!release" ]
then
    /var/jenkins_home/bin/deploy2yum.sh -p $(find ./ -name *.rpm| grep -v SNAPSHOT)
fi
if [ "!$1" = "!snapshot" ]
then
    /var/jenkins_home/bin/deploy2yum.sh $(find ./ -name *.rpm)
fi

YUM_USR=hudson
YUM_USR_KEY=~/.ssh/id_rsa_hudson
YUM_SRV=yum.cumulocity.com
deploy() {
    echo "deploy $1 to $YUM_SRV:${YUM_DEST_DIR}"
    scp -Cr -i ${YUM_USR_KEY} $1 ${YUM_USR}@${YUM_SRV}:${YUM_DEST_DIR}
}

if [ "!$1" = "!release" ]
then
    # Copy snmp-agent-gateway RPM and snmp-mib-parser's zip files to examples/snmp folder
    # Copy only the released files not the SNAPSHOT files
    YUM_DEST_DIR=/var/www/resources/examples/snmp
    deploy $(find ./ -regextype egrep   -regex ".*snmp-agent-gateway-[0-9]+\.[0-9]+\.[0-9]+\.noarch.rpm")
    deploy $(find ./ -regextype egrep   -regex ".*snmp-mib-parser-[0-9]+\.[0-9]+\.[0-9]+\.zip")
fi