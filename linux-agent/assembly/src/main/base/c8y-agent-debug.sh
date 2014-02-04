#!/bin/sh
java -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000 -cp 'cfg/*:lib/*' c8y.lx.agent.Agent
