package c8y.example.LongPolling;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;

@Component
@Slf4j
@AllArgsConstructor
public class JavaMemoryScheduledLogger {

    private final LongPollingService longPollingService;

    @Scheduled(cron = "0 */1 * * * *")
    public void reportMemoryUses() {

        MemoryUsage metaSpaceMemoryUsage = null;
        MemoryUsage heapMemoryUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        MemoryUsage nonHeapMemoryUsage = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
        for (MemoryPoolMXBean memoryMXBean : ManagementFactory.getMemoryPoolMXBeans()) {
            if ("Metaspace".equals(memoryMXBean.getName())) {
                metaSpaceMemoryUsage = memoryMXBean.getUsage();
                break;
            }
        }
        int threadCount = ManagementFactory.getThreadMXBean().getThreadCount();
        log.info("Number of Devices: {}, JVM Stats:: Heap: {}, NonHeap: {}, MetaSpace: {}, ThreadCount: {}", longPollingService.getDeviceCounter().get(), heapMemoryUsage, nonHeapMemoryUsage, metaSpaceMemoryUsage, threadCount);
    }

}