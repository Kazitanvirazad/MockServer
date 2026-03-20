package com.server.core.server;

import com.server.core.config.CommonConfig;
import com.server.core.constants.Method;
import com.server.core.model.data.Server;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import javafx.collections.ObservableSet;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

import static com.server.core.constants.CommonConstants.DEFAULT_RESPONSE_LENGTH;
import static com.server.core.constants.CommonConstants.METHOD_NOT_ALLOWED_HTTP_CODE;

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

    /**
     * Initialize the {@link HttpServer} with port number
     */
    private void initServer(boolean silent) {
        try {
            // Create and initialize httpServer
            this.httpServer = HttpServer.create(new InetSocketAddress(portNumber), 0);
            httpServer.setExecutor(null);
        } catch (IOException exception) {
            if (!silent) {
                CommonConfig.INSTANCE.notification()
                        .triggerErrorNotification("Something went wrong while initializing server",
                                exception.getMessage());
            }
            throw new RuntimeException(exception);
        }
    }

    /**
     * Adds the {@link Server} to the endPoints map. If there is similar server is already added
     * then overrides the server by taking user's consent
     *
     * @param server          {@link Server} to be added in the {@link EndpointInitiator}
     * @param activeServerIds {@link ObservableSet} of currently server ids
     * @apiNote {@link Server}s are mapped based on the hierarchy of port number > url endpoint > method
     */
    public void addEndpoint(Server server, final ObservableSet<String> activeServerIds) {
        String urlEndpoint = server.getUrlEndpoint();
        EndpointInitiator existingEndpointInitiator = endPoints.getOrDefault(urlEndpoint, null);
        // similar server with same url endpoint and method is already running
        if (Objects.nonNull(existingEndpointInitiator) && existingEndpointInitiator.getMethods().containsKey(server.getMethod())) {
            boolean promptResult = CommonConfig.INSTANCE.notification()
                    .triggerConfirmationPrompt("""
                                    Server with similar Endpoint and
                                    method is already running.""",
                            """
                                    Accept to override the existing
                                    server with the new one.""");
            // User responded 'true' to override the existing server
            if (promptResult) {
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
                // don't do anything as user responded 'false'
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

    /**
     * Stops the server and the removes it from the endPoints map and then restarts the server
     *
     * @param server {@link Server} to be removed
     */
    public void removeEndpoint(Server server, boolean silent) {
        stopServer();
        EndpointInitiator existingEndpoints = endPoints.getOrDefault(server.getUrlEndpoint(), null);
        if (Objects.nonNull(existingEndpoints)) {
            existingEndpoints.removeMethod(server.getMethod());
            if (MapUtils.isEmpty(existingEndpoints.getMethods())) {
                endPoints.remove(server.getUrlEndpoint());
            }
        }
        if (MapUtils.isNotEmpty(endPoints)) {
            startServer(silent);
        }
    }

    /**
     * Initialize the {@link HttpServer} with port number, initializes the server context and starts the server
     */
    public void startServer(boolean silent) {
        initServer(silent);
        if (Objects.nonNull(httpServer)) {
            // Add server context logic from 'endPoints'
            initializeServerContext();
            httpServer.setExecutor(null);
            httpServer.start();
        }
    }

    /**
     * Initializes the server context
     */
    private void initializeServerContext() {
        if (MapUtils.isNotEmpty(this.endPoints)) {
            BiConsumer<String, EndpointInitiator> endpointBiConsumer = new EndpointBiConsumer(this.httpServer);
            this.endPoints.forEach(endpointBiConsumer);
        }
    }

    /**
     * Performs server restart
     */
    public void restartServer(boolean silent) {
        stopServer();
        startServer(silent);
    }

    /**
     * Checks if the server is stopped
     *
     * @return {@code boolean} True if server is already stopped, False otherwise
     */
    public boolean isServerStopped() {
        return Objects.isNull(httpServer) && MapUtils.isEmpty(endPoints);
    }

    /**
     * Stops the server
     */
    private void stopServer() {
        if (Objects.nonNull(httpServer)) {
            httpServer.stop(1);
            httpServer = null;
        }
    }

    /**
     * {@link BiConsumer} for setting {@link HttpServer} context
     *
     * @param httpServer {@link HttpServer} where the context will be created
     */
    private record EndpointBiConsumer(HttpServer httpServer) implements BiConsumer<String, EndpointInitiator> {
        @Override
        public void accept(String url, EndpointInitiator endpointInitiator) {
            HttpHandler httpHandler = new MockHttpHandler(endpointInitiator);
            this.httpServer.createContext(url, httpHandler);
        }
    }

    /**
     * {@link HttpHandler} for handling all the mock server's {@link HttpExchange} request and response
     *
     * @param endpointInitiator {@link EndpointInitiator} data to used for setting {@link HttpExchange} data
     */
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
                    byte[] responseBody = methodInitiator.getResponseData();
                    long responseLength;
                    // getting response content length
                    if (ArrayUtils.isNotEmpty(responseBody)) {
                        responseLength = responseBody.length;
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
                    outputStream.write(responseBody);
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
