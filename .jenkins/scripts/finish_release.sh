#!/usr/bin/env bash
set -e

release_version=$1

echo "equivalent of git flow release finish r${release_version}"

echo "git checkout develop; git pull..."
git checkout develop
git pull https://${REPOSITORY_CREDENTIALS}@${REPOSITORY_BASE_URL}/cumulocity-examples

echo "git merge"
git merge -s recursive -Xtheirs release/r${release_version}

echo "git commit merged information"
git add -A
git commit --allow-empty --message "flow: Merged <release> r${release_version} to <develop> (develop)."

echo "git push to release/r${release_version}"
git push https://${REPOSITORY_CREDENTIALS}@${REPOSITORY_BASE_URL}/cumulocity-examples release/r${release_version}
echo "git push to develop"
git push https://${REPOSITORY_CREDENTIALS}@${REPOSITORY_BASE_URL}/cumulocity-examples develop
