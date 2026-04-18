package com.server.app.service;

import com.sun.management.OperatingSystemMXBean;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;

import java.lang.management.ManagementFactory;

/**
 * @author Kazi Tanvir Azad
 * @since MockServer 1.1
 */
public class CpuUsageScheduledService extends ScheduledService<Double> {
    /**
     * @param durationSeconds {@code double} Duration in seconds
     * @param periodSeconds   {@code double} Period in seconds
     */
    public CpuUsageScheduledService(double durationSeconds, double periodSeconds) {
        setDelay(Duration.seconds(durationSeconds));
        setPeriod(Duration.seconds(periodSeconds));
    }

    /**
     * A FutureTask which returns JVM recent CPU usage
     *
     * @return {@link Task}<{@link Double}> Recent CPU usage percentage for the JVM process
     */
    @Override
    protected Task<Double> createTask() {
        return new Task<>() {
            @Override
            protected Double call() {
                OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
                double processCpuLoad = operatingSystemMXBean.getProcessCpuLoad();
                return processCpuLoad >= 0.00d ? (processCpuLoad * 100) : 0d;
            }
        };
    }
}
