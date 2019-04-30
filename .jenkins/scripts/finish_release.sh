#!/usr/bin/env bash

release_version=$1

echo "equivalent of hg flow release finish r${release_version}"
hg commit --message "flow: Closed <release> ${release_version}" --close-branch
hg update develop
hg merge --tool internal:other release/r${release_version}
hg commit --message "flow: Merged <release> r${release_version} to <develop> (develop)."
hg push --new-branch -b release/r${release_version}
hg push -b develop
