#!/bin/sh
(
while true
do
  echo Running the Cumulocity Linux Agent...
  sudo java -cp '.:lib/*' c8y.lx.LxAgent
  sleep 1
done
) 2>&1 | logger
