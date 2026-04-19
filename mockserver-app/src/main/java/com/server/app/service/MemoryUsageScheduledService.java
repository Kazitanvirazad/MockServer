package com.server.app.service;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

/**
 * @author Kazi Tanvir Azad
 * @since MockServer 1.1
 */
public class MemoryUsageScheduledService extends ScheduledService<Long> {
    /**
     * @param durationSeconds {@code double} Duration in seconds
     * @param periodSeconds   {@code double} Period in seconds
     */
    public MemoryUsageScheduledService(double durationSeconds, double periodSeconds) {
        setDelay(Duration.seconds(durationSeconds));
        setPeriod(Duration.seconds(periodSeconds));
    }

    /**
     * A FutureTask which returns JVM Heap memory usage
     *
     * @return {@link Task}<{@link Long}> JVM Heap memory usage in MB
     */
    @Override
    protected Task<Long> createTask() {
        return new Task<>() {
            @Override
            protected Long call() {
                MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
                MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
                long usedMemoryBytes = heapMemoryUsage.getUsed();
                return usedMemoryBytes / 1000000L;
            }
        };
    }
}
