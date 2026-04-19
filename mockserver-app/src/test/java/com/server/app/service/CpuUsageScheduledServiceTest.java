package com.server.app.service;

import com.sun.management.OperatingSystemMXBean;
import javafx.concurrent.Task;
import javafx.util.Duration;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.lang.management.ManagementFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class CpuUsageScheduledServiceTest {
    @Test
    void constructorSetsDelayAndPeriod() {
        TestCpuUsageScheduledService service = new TestCpuUsageScheduledService(1.5d, 3.0d);

        assertEquals(Duration.seconds(1.5d), service.getDelay());
        assertEquals(Duration.seconds(3.0d), service.getPeriod());
    }

    @Test
    void createTaskReturnsPositiveCpuPercentage() throws Exception {
        OperatingSystemMXBean operatingSystemMXBean = mock(OperatingSystemMXBean.class);
        when(operatingSystemMXBean.getProcessCpuLoad()).thenReturn(0.25d);

        try (MockedStatic<ManagementFactory> managementFactoryMock = mockStatic(ManagementFactory.class)) {
            managementFactoryMock.when(ManagementFactory::getOperatingSystemMXBean).thenReturn(operatingSystemMXBean);

            Task<Double> task = new TestCpuUsageScheduledService(0d, 1d).exposedCreateTask();

            assertEquals(25.0d, invokeCall(task));
        }
    }

    @Test
    void createTaskReturnsZeroWhenCpuLoadIsNegative() throws Exception {
        OperatingSystemMXBean operatingSystemMXBean = mock(OperatingSystemMXBean.class);
        when(operatingSystemMXBean.getProcessCpuLoad()).thenReturn(-1.0d);

        try (MockedStatic<ManagementFactory> managementFactoryMock = mockStatic(ManagementFactory.class)) {
            managementFactoryMock.when(ManagementFactory::getOperatingSystemMXBean).thenReturn(operatingSystemMXBean);

            Task<Double> task = new TestCpuUsageScheduledService(0d, 1d).exposedCreateTask();

            assertEquals(0.0d, invokeCall(task));
        }
    }

    private static final class TestCpuUsageScheduledService extends CpuUsageScheduledService {
        private TestCpuUsageScheduledService(double durationSeconds, double periodSeconds) {
            super(durationSeconds, periodSeconds);
        }

        private Task<Double> exposedCreateTask() {
            return super.createTask();
        }
    }

    private static Double invokeCall(Task<Double> task) throws Exception {
        java.lang.reflect.Method call = task.getClass().getDeclaredMethod("call");
        call.setAccessible(true);
        return (Double) call.invoke(task);
    }
}
