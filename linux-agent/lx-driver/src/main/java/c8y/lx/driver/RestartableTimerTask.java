package c8y.lx.driver;

import java.util.TimerTask;

/**
 * A simple class to work around the mechanism that a TimerTask cannot be
 * rescheduled. Pass a Runnable into this and create a new instance of this
 * class instead of creating a new instance of the Runnable.
 */
public class RestartableTimerTask extends TimerTask {

	private Runnable runnable;

	public RestartableTimerTask(Runnable runnable) {
		this.runnable = runnable;
	}

	@Override
	public void run() {
		runnable.run();
	}
}
