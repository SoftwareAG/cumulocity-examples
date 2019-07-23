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

branch_name=$(hg branch)
if [ "!develop" == "!branch_name" ]; then
    branch_name="release/r${version}"
fi
echo "branch name: $branch_name"

hg up -C ${branch_name}


echo "Update version to ${version}"
./mvnw versions:set -DnewVersion=${version}
cd java-agent/assembly/
../../mvnw versions:set -DnewVersion=${version}
cd ../..
cd hello-world-microservice
../mvnw versions:set -DnewVersion=${version}
cd ..
#.jenkins/scripts/update_dependencies.sh ${version}

# tests are run to make sure all dependencies with ${project.version} work correctly
# same for usage of -s $MVN_SETTINGS.
./mvnw clean install -Dmaven.javadoc.skip=true -s $MVN_SETTINGS
chmod +x .jenkins/scripts/deploy.sh
.jenkins/scripts/deploy.sh release

echo "tagging cumulocity-examples"
tag-version "c8y-agents-${version}"

echo "Update version to ${next_version}"
#.jenkins/scripts/update_dependencies.sh ${next_version}
./mvnw versions:set -DnewVersion=${next_version} -DgenerateBackupPoms=false -s $MVN_SETTINGS
cd java-agent/assembly/
../../mvnw versions:set -DnewVersion=${next_version} -DgenerateBackupPoms=false -s $MVN_SETTINGS
cd ../..

cd hello-world-microservice
../mvnw versions:set -DnewVersion=${next_version} -DgenerateBackupPoms=false -s $MVN_SETTINGS
cd ..

hg commit -m "[maven-release-plugin] prepare for next development iteration"

branch_name=$(hg branch)
hg push -r${branch_name} https://${BITBUCKET_USER}:${BITBUCKET_PASSWORD}@bitbucket.org/m2m/cumulocity-examples --new-branch

