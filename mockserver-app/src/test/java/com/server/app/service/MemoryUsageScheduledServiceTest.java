package com.server.app.service;

import javafx.concurrent.Task;
import javafx.util.Duration;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class MemoryUsageScheduledServiceTest {
    @Test
    void constructorSetsDelayAndPeriod() {
        TestMemoryUsageScheduledService service = new TestMemoryUsageScheduledService(2.0d, 4.5d);

        assertEquals(Duration.seconds(2.0d), service.getDelay());
        assertEquals(Duration.seconds(4.5d), service.getPeriod());
    }

    @Test
    void createTaskReturnsHeapUsageInMegabytes() throws Exception {
        MemoryMXBean memoryMXBean = mock(MemoryMXBean.class);
        when(memoryMXBean.getHeapMemoryUsage()).thenReturn(new MemoryUsage(0L, 25_000_000L, 50_000_000L, 100_000_000L));

        try (MockedStatic<ManagementFactory> managementFactoryMock = mockStatic(ManagementFactory.class)) {
            managementFactoryMock.when(ManagementFactory::getMemoryMXBean).thenReturn(memoryMXBean);

            Task<Long> task = new TestMemoryUsageScheduledService(0d, 1d).exposedCreateTask();

            assertEquals(25L, invokeCall(task));
        }
    }

    private static final class TestMemoryUsageScheduledService extends MemoryUsageScheduledService {
        private TestMemoryUsageScheduledService(double durationSeconds, double periodSeconds) {
            super(durationSeconds, periodSeconds);
        }

        private Task<Long> exposedCreateTask() {
            return super.createTask();
        }
    }

    private static Long invokeCall(Task<Long> task) throws Exception {
        java.lang.reflect.Method call = task.getClass().getDeclaredMethod("call");
        call.setAccessible(true);
        return (Long) call.invoke(task);
    }
}
