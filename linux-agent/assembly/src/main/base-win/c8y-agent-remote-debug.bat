@echo off
:loop
	cls
	echo "Running the Cumulocity Windows Agent in debug mode..."
	java -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000 -cp "cfg/*;lib/*" -Dlogback.configurationFile=cfg/logback.xml c8y.lx.agent.Agent
	echo "Press any key to restart the Agent or Ctrl+C to close."
	pause >nul
goto loop