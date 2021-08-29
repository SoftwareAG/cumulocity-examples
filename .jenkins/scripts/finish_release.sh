#!/usr/bin/env bash
set -e

release_version=$1

echo "equivalent of git flow release finish r${release_version}"

echo "git checkout githubdev; git pull..."
git checkout githubdev
git pull ${REPOSITORY_BASE_URL}/cumulocity-examples

echo "git merge"
git merge -s recursive -Xtheirs release/r${release_version}

echo "git commit merged information"
git add -A
git commit --allow-empty --message "flow: Merged <release> r${release_version} to <githubdev> (githubdev)."

echo "git push to release/r${release_version}"
git push ${REPOSITORY_BASE_URL}/cumulocity-examples release/r${release_version}
echo "git push to githubdev"
git push ${REPOSITORY_BASE_URL}/cumulocity-examples githubdev
