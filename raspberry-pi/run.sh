#!/bin/sh
(
while true
do
  echo Running the Cumulocity Pi Agent...
  cd /home/pi/piagent/lib
  sudo java -cp '*' c8y.pi.PiAgent
  sleep 1
done
) 2>&1 | logger
