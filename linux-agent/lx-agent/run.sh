#!/bin/sh
(
while true
do
  echo Running the Cumulocity Linux Agent...
  cd lib
  sudo java -cp '..:*' c8y.lx.agent.Agent
  sleep 1
done
) 2>&1 | logger
