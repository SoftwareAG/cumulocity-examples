#!/usr/bin/env bash

release_version=$1
echo "equivalent of git flow release start $release_version"
git checkout -b release/r${release_version}
git commit --allow-empty -am "flow: Created branch release/r${release_version}"
git push https://${BITBUCKET_USER}:${BITBUCKET_PASSWORD}@bitbucket.org/m2m/cumulocity-examples release/r${release_version}
