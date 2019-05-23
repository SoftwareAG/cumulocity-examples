#!/usr/bin/env bash
set -e
source ${BASH_SOURCE%/*}/update_dependencies.sh

hotfix_version=$1
development_version=$2

hg update develop
find . -name 'pom.xml' | xargs sed -i "s/<version>${hotfix_version}<\\/version>/<version>${development_version}<\\/version>/g"
update-dependencies ${development_version}

hg commit -m 'Update dependencies to next SNAPSHOT version'
hg push -b develop https://${BITBUCKET_USER}:${BITBUCKET_PASSWORD}@bitbucket.org/m2m/cumulocity-examples
