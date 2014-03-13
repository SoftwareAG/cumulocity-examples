#!/bin/sh
java -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000 -cp 'cfg/*:lib/*' -Dlogback.configurationFile=cfg/logback.xml c8y.lx.agent.Agent
