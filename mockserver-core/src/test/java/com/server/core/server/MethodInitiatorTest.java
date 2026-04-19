package com.server.core.server;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MethodInitiatorTest {
    @Test
    void keyValueRecordExposesStoredValues() {
        KeyValue keyValue = new KeyValue("Content-Type", "application/json");

        assertEquals("Content-Type", keyValue.key());
        assertEquals("application/json", keyValue.value());
    }

    @Test
    void methodInitiatorStoresConfiguredValues() {
        MethodInitiator methodInitiator = new MethodInitiator();
        KeyValue header = new KeyValue("X-Test", "yes");

        methodInitiator.setDelay(50L);
        methodInitiator.setResponseCode(202);
        methodInitiator.setServerId("SER-1");
        methodInitiator.setServerName("Orders");
        methodInitiator.setResponseData("ok".getBytes());
        methodInitiator.addHeader(header);

        assertNotNull(methodInitiator.getHeaders());
        assertEquals(50L, methodInitiator.getDelay());
        assertEquals(202, methodInitiator.getResponseCode());
        assertEquals("SER-1", methodInitiator.getServerId());
        assertEquals("Orders", methodInitiator.getServerName());
        assertArrayEquals("ok".getBytes(), methodInitiator.getResponseData());
        assertEquals(1, methodInitiator.getHeaders().size());
        assertTrue(methodInitiator.getHeaders().contains(header));
    }

    @Test
    void methodInitiatorReplacesHeadersWhenExplicitlySet() {
        MethodInitiator methodInitiator = new MethodInitiator();
        List<KeyValue> headers = List.of(new KeyValue("Accept", "application/json"));

        methodInitiator.setHeaders(headers);

        assertSame(headers, methodInitiator.getHeaders());
    }
}
