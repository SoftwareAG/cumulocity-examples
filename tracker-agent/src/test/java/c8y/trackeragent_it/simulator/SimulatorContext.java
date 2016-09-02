package c8y.trackeragent_it.simulator;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

public class SimulatorContext {

    private final Queue<SimulatorTask> tasks = new ConcurrentLinkedQueue<>();
    private CountDownLatch latch = new CountDownLatch(0);

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

    public void setLatch(int size) {
        latch = new CountDownLatch(size);
    }

    public CountDownLatch getLatch() {
        return latch;
    }

}
