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
        -b )                    shift
                                current_branch=$1
                                ;;
        *)                      ;;
    esac
    shift
done

function tag-version {
    tag=$1
    git commit --allow-empty -am "[maven-release-plugin] prepare release ${tag}" || echo ""
    git tag -f -m "copy for tag ${tag}" -a "${tag}"
}

./mvnw -B -s $MVN_SETTINGS clean -T 4

echo "obtain current branch name"

if [ "develop" == "${current_branch}" ]; then
  branch_name="release/r${version}"
else
  branch_name=$current_branch
fi

echo "branch name: $branch_name"
git checkout ${branch_name}

echo "pull latest changes from the branch ${branch_name}"
git pull https://${REPOSITORY_CREDENTIALS}@${REPOSITORY_BASE_URL}/cumulocity-examples ${branch_name}

echo "Update version to ${version}"
./mvnw -B -s $MVN_SETTINGS versions:set -DnewVersion=${version}
cd java-agent/assembly/
../../mvnw -B -s $MVN_SETTINGS versions:set -DnewVersion=${version}
cd ../..
cd hello-world-microservice
../mvnw -B -s $MVN_SETTINGS versions:set -DnewVersion=${version}
cd ..
.jenkins/scripts/update_dependencies.sh ${version}

# tests are run to make sure all dependencies with ${project.version} work correctly
# same for usage of -s $MVN_SETTINGS.
./mvnw -B clean install -Dmaven.javadoc.skip=true -s $MVN_SETTINGS
chmod +x .jenkins/scripts/deploy.sh
.jenkins/scripts/deploy.sh release

echo "tagging cumulocity-examples"
tag-version "c8y-examples-${version}"

echo "Update version to ${next_version}"
.jenkins/scripts/update_dependencies.sh ${next_version}
./mvnw -B versions:set -DnewVersion=${next_version} -DgenerateBackupPoms=false -s $MVN_SETTINGS
cd java-agent/assembly/
../../mvnw -B versions:set -DnewVersion=${next_version} -DgenerateBackupPoms=false -s $MVN_SETTINGS
cd ../..

cd hello-world-microservice
../mvnw -B versions:set -DnewVersion=${next_version} -DgenerateBackupPoms=false -s $MVN_SETTINGS
cd ..

git commit --allow-empty -am "[maven-release-plugin] prepare for next development iteration"

git push https://${REPOSITORY_CREDENTIALS}@${REPOSITORY_BASE_URL}/cumulocity-examples ${branch_name}
git push https://${REPOSITORY_CREDENTIALS}@${REPOSITORY_BASE_URL}/cumulocity-examples ${tag}

