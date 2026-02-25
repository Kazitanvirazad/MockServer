package com.server.app.server;

import com.server.app.constants.Method;
import com.server.app.model.data.Server;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import javafx.collections.ObservableSet;
import javafx.scene.control.ButtonType;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

import static com.server.app.constants.ApplicationConstants.DEFAULT_RESPONSE_LENGTH;
import static com.server.app.constants.ApplicationConstants.METHOD_NOT_ALLOWED_HTTP_CODE;
import static com.server.app.util.AppUtil.triggerConfirmationPrompt;
import static com.server.app.util.AppUtil.triggerErrorAlert;

/**
 * @author Kazi Tanvir Azad
 */
public class ServerInitiator {
    private static final Logger log = LogManager.getLogger(ServerInitiator.class);
    private int portNumber;
    private HttpServer httpServer;
    private final Map<String, EndpointInitiator> endPoints;

    public ServerInitiator(int portNumber) {
        this.endPoints = new HashMap<>();
        this.portNumber = portNumber;
    }

    private void initServer() {
        try {
            // Create and initialize httpServer
            this.httpServer = HttpServer.create(new InetSocketAddress(portNumber), 0);
            httpServer.setExecutor(null);
        } catch (IOException exception) {
            triggerErrorAlert("Something went wrong while initializing server",
                    exception.getMessage());
            throw new RuntimeException(exception);
        }
    }

    public void addEndpoint(Server server, final ObservableSet<String> activeServerIds) {
        String urlEndpoint = server.getUrlEndpoint();
        EndpointInitiator existingEndpointInitiator = endPoints.getOrDefault(urlEndpoint, null);
        // similar server with same url endpoint and method is already running
        if (Objects.nonNull(existingEndpointInitiator) && existingEndpointInitiator.getMethods().containsKey(server.getMethod())) {
            ButtonType promptResult = triggerConfirmationPrompt("""
                            Server with similar Endpoint and
                            method is already running.""",
                    """
                            Click OK to override the existing
                            server with the new one.""");
            // Pressed 'OK' to override the existing server
            if (ButtonType.OK == promptResult) {
                // getting the server to overridden
                MethodInitiator methodInitiatorToOverride = existingEndpointInitiator.getMethods().get(server.getMethod());
                String serverIdToOverride = Objects.nonNull(methodInitiatorToOverride.getServerId()) ?
                        methodInitiatorToOverride.getServerId() : null;
                // override the existing server
                existingEndpointInitiator.addMethod(server);
                // update the active servers set
                if (Objects.nonNull(serverIdToOverride)) {
                    activeServerIds.remove(serverIdToOverride);
                }
            } else {
                // don't do anything as user Pressed 'Cancel'
                throw new RuntimeException();
            }
        } else if (Objects.nonNull(existingEndpointInitiator)) {
            // similar server with same url endpoint is already running
            existingEndpointInitiator.addMethod(server);
        } else {
            // Create the fresh new server
            EndpointInitiator endpointInitiator = new EndpointInitiator();
            endpointInitiator.setUrlEndpoint(urlEndpoint);
            endpointInitiator.addMethod(server);
            endPoints.put(urlEndpoint, endpointInitiator);
        }
    }

    public void removeEndpoint(Server server) {
        stopServer();
        endPoints.remove(server.getUrlEndpoint());
        if (MapUtils.isNotEmpty(endPoints)) {
            startServer();
        }
    }

    public void startServer() {
        initServer();
        if (Objects.nonNull(httpServer)) {
            // Add server context logic from 'endPoints'
            initializeServerContext();
            httpServer.setExecutor(null);
            httpServer.start();
        }
    }

    private void initializeServerContext() {
        if (MapUtils.isNotEmpty(this.endPoints)) {
            BiConsumer<String, EndpointInitiator> endpointBiConsumer = new EndpointBiConsumer(this.httpServer);
            this.endPoints.forEach(endpointBiConsumer);
        }
    }

    public void restartServer() {
        stopServer();
        startServer();
    }

    public boolean isServerStopped() {
        return Objects.isNull(httpServer) && MapUtils.isEmpty(endPoints);
    }

    private void stopServer() {
        if (Objects.nonNull(httpServer)) {
            httpServer.stop(1);
            httpServer = null;
        }
    }

    private record EndpointBiConsumer(HttpServer httpServer) implements BiConsumer<String, EndpointInitiator> {
        @Override
        public void accept(String url, EndpointInitiator endpointInitiator) {
            HttpHandler httpHandler = new MockHttpHandler(endpointInitiator);
            this.httpServer.createContext(url, httpHandler);
        }
    }

    private record MockHttpHandler(EndpointInitiator endpointInitiator) implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<Method, MethodInitiator> methods = endpointInitiator.getMethods();
            OutputStream outputStream = exchange.getResponseBody();
            Method inputMethod;
            try {
                String exchangeMethod = exchange.getRequestMethod().trim().toUpperCase();
                inputMethod = Method.valueOf(exchangeMethod);
                // checking if exchange has valid http method
                if (methods.containsKey(inputMethod)) {
                    MethodInitiator methodInitiator = methods.get(inputMethod);
                    //adding headers to the response
                    methodInitiator.getHeaders().forEach(header -> {
                        exchange.getResponseHeaders().add(header.key(), header.value());
                    });
                    String responseBody = methodInitiator.getResponseData();
                    long responseLength;
                    // getting response content length
                    if (StringUtils.isNotEmpty(responseBody)) {
                        responseLength = responseBody.getBytes(StandardCharsets.UTF_8).length;
                    } else {
                        responseLength = DEFAULT_RESPONSE_LENGTH;
                    }
                    // adding http response code and response content length
                    exchange.sendResponseHeaders(methodInitiator.getResponseCode(), responseLength);
                    // adding delay
                    if (methodInitiator.getDelay() > 0) {
                        try {
                            Thread.sleep(methodInitiator.getDelay());
                        } catch (Exception ignore) {
                        }
                    }
                    // adding response body
                    outputStream.write(responseBody.getBytes(StandardCharsets.UTF_8));
                } else {
                    // sending method not allowed error
                    exchange.sendResponseHeaders(METHOD_NOT_ALLOWED_HTTP_CODE, DEFAULT_RESPONSE_LENGTH);
                }
            } catch (Exception exception) {
                // sending method not allowed error
                exchange.sendResponseHeaders(METHOD_NOT_ALLOWED_HTTP_CODE, DEFAULT_RESPONSE_LENGTH);
            }
            outputStream.close();
        }
    }
}
