package com.server.core.server;

import com.server.core.constants.Method;
import com.server.core.model.data.Cookie;
import com.server.core.model.data.Header;
import com.server.core.model.data.Server;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.server.core.constants.CommonConstants.COOKIE_HEADER_KEY;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EndpointInitiatorTest {
    @Test
    void addMethodUsesTextResponseHeadersAndCookies() {
        EndpointInitiator endpointInitiator = new EndpointInitiator();
        Server server = server("SER-1", Method.GET, "/orders", "hello");
        server.setHeaders(List.of(new Header("Content-Type", "application/json")));
        server.setCookies(List.of(new Cookie("example.com", null, false, null,
                "session", false, "/", Cookie.SameSite.LAX, true, "abc")));

        endpointInitiator.addMethod(server);

        MethodInitiator methodInitiator = endpointInitiator.getMethods().get(Method.GET);
        assertEquals(1, endpointInitiator.getMethods().size());
        assertEquals(125L, methodInitiator.getDelay());
        assertEquals(201, methodInitiator.getResponseCode());
        assertEquals("SER-1", methodInitiator.getServerId());
        assertEquals("Server-SER-1", methodInitiator.getServerName());
        assertArrayEquals("hello".getBytes(), methodInitiator.getResponseData());
        assertEquals(2, methodInitiator.getHeaders().size());
        assertEquals("Content-Type", methodInitiator.getHeaders().getFirst().key());
        assertEquals(COOKIE_HEADER_KEY, methodInitiator.getHeaders().get(1).key());
        assertTrue(methodInitiator.getHeaders().get(1).value().contains("session=abc"));
    }

    @Test
    void addMethodReadsBinaryResponseFromFileAndSupportsRemoval() throws Exception {
        EndpointInitiator endpointInitiator = new EndpointInitiator();
        Path responseFile = Files.createTempFile("mockserver-response", ".bin");
        Files.write(responseFile, new byte[]{1, 2, 3});

        Server server = server("SER-2", Method.POST, "/binary", "");
        server.setDefaultResponseBinary(true);
        server.setResponseBinaryPath(responseFile.toString());
        endpointInitiator.setUrlEndpoint("/binary");

        endpointInitiator.addMethod(server);

        assertEquals("/binary", endpointInitiator.getUrlEndpoint());
        assertArrayEquals(new byte[]{1, 2, 3}, endpointInitiator.getMethods().get(Method.POST).getResponseData());

        Map<Method, MethodInitiator> replacementMethods = new HashMap<>();
        replacementMethods.put(Method.DELETE, new MethodInitiator());
        endpointInitiator.setMethods(replacementMethods);
        assertSame(replacementMethods, endpointInitiator.getMethods());

        endpointInitiator.removeMethod(Method.DELETE);
        assertFalse(endpointInitiator.getMethods().containsKey(Method.DELETE));
    }

    @Test
    void addMethodUsesEmptyResponseWhenTextBodyIsBlank() {
        EndpointInitiator endpointInitiator = new EndpointInitiator();
        Server server = server("SER-3", Method.PUT, "/empty", "");

        endpointInitiator.addMethod(server);

        assertArrayEquals(new byte[0], endpointInitiator.getMethods().get(Method.PUT).getResponseData());
        assertTrue(endpointInitiator.getMethods().get(Method.PUT).getHeaders().isEmpty());
    }

    private static Server server(String serverId, Method method, String endpoint, String responseData) {
        Server server = new Server();
        server.setServerId(serverId);
        server.setServerName("Server-" + serverId);
        server.setMethod(method);
        server.setUrlEndpoint(endpoint);
        server.setResponseCode(201);
        server.setDelay(125L);
        server.setResponseData(responseData);
        return server;
    }
}
