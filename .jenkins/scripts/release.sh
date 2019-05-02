#!/bin/bash
set -e

while [ "$1" != "" ]; do
    case $1 in
        -r | --release )        shift
                                version=$1
                                ;;
        -d | --development )    shift
                                next_version=$1
                                ;;
        *)                      ;;
    esac
    shift
done

function tag-version {
    tag=$1
    hg commit -m "[maven-release-plugin] prepare release ${tag}" || echo ""
    hg tag -f -m "copy for tag ${tag}" "${tag}"
}

./mvnw clean -T 4

hg pull -u 
hg up -C


echo "Update version to ${version}"
./mvnw versions:set -DnewVersion=${version} 
./mvnw clean install -DskipTests -Dmaven.javadoc.skip=true -Dskip.microservice.package=false -Dskip.agent.package.container=false
./deploy.sh

echo "tagging cumulocity-examples"
tag-version "c8y-examples-${version}"

echo "Update version to ${next_version}"
./mvnw versions:set -DnewVersion=${next_version} -DgenerateBackupPoms=false
hg commit -m "[maven-release-plugin] prepare for next development iteration"

branch_name=$(hg branch)
hg push -r${branch_name} ssh://hg@bitbucket.org/m2m/cumulocity-examples

