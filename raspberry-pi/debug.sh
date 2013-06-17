#!/bin/sh
cd /home/pi/piagent/lib
sudo java -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000 -cp '*' c8y.pi.PiAgent
