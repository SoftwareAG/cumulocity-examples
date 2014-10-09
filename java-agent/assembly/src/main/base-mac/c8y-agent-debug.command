#!/bin/sh
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $DIR
echo "Running the Cumulocity Linux Agent..."
java -cp 'cfg/*:lib/*' -Dlogback.configurationFile=cfg/logback-debug.xml c8y.lx.agent.Agent