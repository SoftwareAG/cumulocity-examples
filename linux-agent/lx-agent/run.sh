#!/bin/sh
(
while true
do
  echo Running the Cumulocity Linux Agent...
  sudo java -cp '.:lib/*' c8y.lx.agent.Agent
  sleep 1
done
) 2>&1 | logger
