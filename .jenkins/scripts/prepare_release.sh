#!/usr/bin/env bash

release_version=r$1
echo "equivalent of hg flow release start $release_version"
hg branch release/$release_version
hg commit --message "flow: Created branch release/${release_version}"
