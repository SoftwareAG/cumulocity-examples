#!/bin/sh
sudo java -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000 -cp '.:lib/*' c8y.lx.agent.Agent
