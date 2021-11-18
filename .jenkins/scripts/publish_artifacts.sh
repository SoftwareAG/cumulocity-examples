#!/bin/sh
set -e
# ./mvnw clean deploy -DskipTests -s $MVN_SETTINGS
#/var/lib/jenkins/bin/deploy2yum.sh -p $(find ./ -name *.rpm)

YUM_USR=hudson
YUM_SRV=yum.cumulocity.com
YUM_MS_DIR=/var/www/staging-resources/kubernetes-images
YUM_SNMP_DIR=/var/www/staging-resources/examples/snmp
YUM_EXAMPLES_DIR=/var/www/staging-resources/examples
PUBLISH_TYPE=$1

YUM_DEST_DIR=/var/staging-incoming-rpms/cumulocity/

deployAll(){
   dest=$1
   shift 1

   for i in "$@"
   do
      deploy $i $dest
  done
}


deploy() {
  echo "deploy $1 to $YUM_SRV:${YUM_DEST_DIR}"
  scp -o StrictHostKeyChecking=no  -Cr $1 ${YUM_USR}@${YUM_SRV}:$2 | true
}

setLatest(){
  echo "update latest $2 to $1"
  ssh ${YUM_USR}@${YUM_SRV} ln -sf $1 $2/$3-latest.$4 
}

if [ "!$1" = "!release" ]
then
    deployAll $YUM_DEST_DIR $(find ./ -name *.rpm| grep -v SNAPSHOT)
fi

if [ "!$1" = "!snapshot" ]
then
    deployAll $YUM_DEST_DIR $(find ./ -name *.rpm)
fi

deploy $(find ./ -regextype egrep -regex ".*snmp-mib-parser-[0-9]+\.[0-9]+\.[0-9]+(-SNAPSHOT)?\.zip") $YUM_MS_DIR

if [ "!$1" = "!release" ]
then
    # Copy snmp-agent-gateway RPM and snmp-mib-parser's zip files to examples/snmp folder
    # Copy only the released files not the SNAPSHOT files
    deploy $(find ./ -regextype egrep -regex ".*snmp-agent-gateway-[0-9]+\.[0-9]+\.[0-9]+-1\.noarch\.rpm") $YUM_SNMP_DIR
    deploy $(find ./ -regextype egrep -regex ".*snmp-mib-parser-[0-9]+\.[0-9]+\.[0-9]+\.zip") $YUM_SNMP_DIR

    target_package=$(find ./ -regextype egrep -regex ".*cumulocity-linux-agent-[0-9]+\.[0-9]+\.[0-9]+\.tar\.gz")
    target_package_name=$(basename $target_package)
    
    if [ -z "$target_package" ] || [ -z "$YUM_DEST_DIR" ]; then
        echo "cumulocity-linux-agent or destination  not find!"
        exit 1
    else
        deploy $target_package $YUM_EXAMPLES_DIR

        if [ "!$1" = "!develop" ]
        then
            setLatest $target_package_name $YUM_DEST_DIR  "cumulocity-linux-agent" "tar.gz"
        fi
    fi
fi
