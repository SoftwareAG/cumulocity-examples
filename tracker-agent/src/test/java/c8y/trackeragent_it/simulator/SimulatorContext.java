package c8y.trackeragent_it.simulator;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

public class SimulatorContext {

    private final Queue<SimulatorTask> tasks = new ConcurrentLinkedQueue<>();
    private final CountDownLatch latch;

    public SimulatorContext(CountDownLatch latch) {
        this.latch = latch;
    }

    public synchronized SimulatorTask poll() {
        SimulatorTask task = tasks.poll();
        if (task != null) {
            latch.countDown();
        }
        return task;
    }

    public void addTask(SimulatorTask task) {
        tasks.offer(task);
    }
}
