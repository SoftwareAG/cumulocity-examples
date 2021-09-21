#!/usr/bin/env bash
set -e

release_version=$1
echo "equivalent of git flow release start $release_version"
git checkout -b release/r${release_version}
git commit --allow-empty -am "flow: Created branch release/r${release_version}"
git push https://${REPOSITORY_USER}:${REPOSITORY_PASSWORD}@${REPOSITORY_BASE_URL}/cumulocity-examples release/r${release_version}
