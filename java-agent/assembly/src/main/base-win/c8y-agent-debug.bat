@echo off
%~d1
cd "%~p1"
:loop
	cls
	echo "Running the Cumulocity Windows Agent in Debug Mode..."
	java -cp "cfg/*;lib/*" -Dlogback.configurationFile=cfg/logback-debug.xml c8y.lx.agent.Agent
	echo "Press any key to restart Agent or Ctrl+C to close."
	pause >nul
goto loop