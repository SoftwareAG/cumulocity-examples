#!/usr/bin/env bash

release_version=$1
echo "equivalent of hg flow release start $release_version"
hg branch release/r$release_version
hg commit --message "flow: Created branch release/r${release_version}"

