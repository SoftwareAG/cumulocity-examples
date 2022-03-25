#!/bin/sh
set -e
# ./mvnw -B clean deploy -DskipTests -s $MVN_SETTINGS
#/var/lib/jenkins/bin/deploy2yum.sh -p $(find ./ -name *.rpm)

YUM_USR=hudson
YUM_SRV=yum.cumulocity.com
deployAll(){
   for i in "$@"
   do
      deploy $i
  done
}

deploy() {
  echo "deploy $1 to $YUM_SRV:${YUM_DEST_DIR}"
  scp -o StrictHostKeyChecking=no  -Cr $1 ${YUM_USR}@${YUM_SRV}:${YUM_DEST_DIR} | true
}
setLatest(){
  echo "update latest $2 to $1"
  ssh ${YUM_USR}@${YUM_SRV} ln -sf $1 $2/$3-latest.$4 
}
if [ "!$1" = "!release" ]
then
    YUM_DEST_DIR=/var/incoming-rpms/cumulocity/
    deployAll $(find ./ -name *.rpm| grep -v SNAPSHOT)
fi
if [ "!$1" = "!snapshot" ]
then
    YUM_DEST_DIR=/var/incoming-rpms/cumulocity-testing/
    deployAll $(find ./ -name *.rpm)
fi

YUM_DEST_DIR=/var/www/resources/kubernetes-images
deploy $(find ./ -regextype egrep   -regex ".*snmp-mib-parser-[0-9]+\.[0-9]+\.[0-9]+(-SNAPSHOT)?\.zip")

if [ "!$1" = "!release" ]
then
    # Copy snmp-agent-gateway RPM and snmp-mib-parser's zip files to examples/snmp folder
    # Copy only the released files not the SNAPSHOT files
    YUM_DEST_DIR=/var/www/resources/examples/snmp
    deploy $(find ./ -regextype egrep   -regex ".*snmp-agent-gateway-[0-9]+\.[0-9]+\.[0-9]+-1\.noarch\.rpm")
    deploy $(find ./ -regextype egrep   -regex ".*snmp-mib-parser-[0-9]+\.[0-9]+\.[0-9]+\.zip")

    YUM_DEST_DIR=/var/www/resources/examples
    target_package=$(find ./ -regextype egrep -regex ".*cumulocity-linux-agent-[0-9]+\.[0-9]+\.[0-9]+\.tar\.gz")
    target_package_name=$(basename $target_package)
    

        if [ -z "$target_package" ] || [ -z "$YUM_DEST_DIR" ]; then
            echo "cumulocity-linux-agent or destination  not find!"
            exit 1
        else
            deploy $target_package

            if [ "!$1" = "!develop" ]
            then
                setLatest  $target_package_name $YUM_DEST_DIR  "cumulocity-linux-agent" "tar.gz"
            fi
        fi
   
fi
