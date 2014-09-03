@echo off
%~d1
cd "%~p1"
:loop
	cls
	echo "Running the Cumulocity Windows Agent..."
	java -cp "cfg/*;lib/*" -Dlogback.configurationFile=cfg/logback.xml c8y.lx.agent.Agent
	echo "Press any key to restart Agent or Ctrl+C to close."
	pause >nul
goto loop