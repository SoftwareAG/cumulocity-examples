#!/bin/bash -e

MAVEN_ARGS="${MAVEN_ARGS:-} $@"
VERBOSE=true

while [ $# -gt 0 ]; do
  case "$1" in
    -q | --quiet ) VERBOSE=false; shift ;;
    * ) shift ;;
  esac
done

if [ -n "${VERSION}" ]; then
  MAVEN_ARGS="--define revision=${VERSION} ${MAVEN_ARGS}"
  if [ -n "${CHANGE_VERSION}" ]; then
    MAVEN_ARGS="--define changelist=${CHANGE_VERSION} ${MAVEN_ARGS}"
  else
    MAVEN_ARGS="--define changelist= ${MAVEN_ARGS}"
  fi
else
  if [ -n "${CHANGE_VERSION}" ]; then
    MAVEN_ARGS="--define changelist=${CHANGE_VERSION} ${MAVEN_ARGS}"
  fi
fi

MAVEN_PROFILES="${MAVEN_PROFILES:-}"
if [ -n "$MAVEN_PROFILES" ]; then
  MAVEN_ARGS="--activate-profiles $MAVEN_PROFILES ${MAVEN_ARGS}"
fi

if [ -n "$WORKSPACE" ]; then
  MAVEN_ARGS="--define maven.repo.local=${WORKSPACE}/.m2/repository ${MAVEN_ARGS}"
fi

MVN_SETTINGS="${MVN_SETTINGS:-$HOME/.m2/settings.xml}"
MAVEN_ARGS="--settings $MVN_SETTINGS ${MAVEN_ARGS}"

if [ $VERBOSE = true ]; then
  MAVEN_ARGS="--show-version --errors ${MAVEN_ARGS}"
  set -x
fi

export MAVEN_OPTS="-Xms256m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=192m -XX:+TieredCompilation -XX:TieredStopAtLevel=1 ${MAVEN_OPTS}"

./mvnw --batch-mode --threads 4 $MAVEN_ARGS
