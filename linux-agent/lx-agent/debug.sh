#!/bin/sh
cd lib
sudo java -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000 -cp '..:*' c8y.lx.agent.Agent
