#!/usr/bin/env bash

source ${BASH_SOURCE%/*}/update_dependencies.sh

hotfix_version=$1
development_version=$2

git checkout develop
find . -name 'pom.xml' | xargs sed -i "s/<version>${hotfix_version}<\\/version>/<version>${development_version}<\\/version>/g"
update-dependencies ${development_version}

git commit -am 'Update dependencies to next SNAPSHOT version'
git push https://${BITBUCKET_USER}:${BITBUCKET_PASSWORD}@bitbucket.org/m2m/cumulocity-examples develop
