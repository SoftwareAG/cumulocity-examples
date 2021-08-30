#!/usr/bin/env bash
set -e

source ${BASH_SOURCE%/*}/update_dependencies.sh

hotfix_version=$1
development_version=$2

git checkout githubdev
find . -name 'pom.xml' | xargs sed -i "s/<version>${hotfix_version}<\\/version>/<version>${development_version}<\\/version>/g"
update-dependencies ${development_version}

git commit -am 'Update dependencies to next SNAPSHOT version'
echo "git push origin githubdev"
git push ${REPOSITORY_BASE_URL}/cumulocity-examples githubdev
