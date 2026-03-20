package com.server.core.server;

import com.server.core.config.CommonConfig;
import com.server.core.constants.Method;
import com.server.core.model.data.Server;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.server.core.constants.CommonConstants.COOKIE_HEADER_KEY;

/**
 * @author Kazi Tanvir Azad
 */
public class EndpointInitiator {
    private String urlEndpoint;
    private Map<Method, MethodInitiator> methods;

    public EndpointInitiator() {
        this.methods = new HashMap<>();
    }

    /**
     * Adds the {@link MethodInitiator} based on the {@link Server} data in the {@link Method} map
     *
     * @param server {@link Server} to be used to add the {@link MethodInitiator} based on its data in the {@link Method} map
     */
    public void addMethod(Server server) {
        MethodInitiator methodInitiator = new MethodInitiator();
        methodInitiator.setDelay(server.getDelay());
        methodInitiator.setResponseCode(server.getResponseCode());
        methodInitiator.setServerId(server.getServerId());
        methodInitiator.setServerName(server.getServerName());
        byte[] response;
        if (server.isDefaultResponseBinary()) {
            response = CommonConfig.INSTANCE.getIoUtil().readFile(server.getResponseBinaryPath());
        } else {
            response = StringUtils.isNotBlank(server.getResponseData()) ?
                    server.getResponseData().getBytes(StandardCharsets.UTF_8) : new byte[0];
        }
        methodInitiator.setResponseData(response);
        if (CollectionUtils.isNotEmpty(server.getHeaders())) {
            server.getHeaders().forEach(header ->
                    methodInitiator.addHeader(new KeyValue(header.getKey(), header.getValue())));
        }
        if (CollectionUtils.isNotEmpty(server.getCookies())) {
            server.getCookies().forEach(cookie ->
                    methodInitiator.addHeader(new KeyValue(COOKIE_HEADER_KEY, cookie.value())));
        }
        methods.put(server.getMethod(), methodInitiator);
    }

    public Map<Method, MethodInitiator> getMethods() {
        return methods;
    }

    public void setMethods(Map<Method, MethodInitiator> methods) {
        this.methods = methods;
    }

    public void removeMethod(Method method) {
        this.methods.remove(method);
    }

    public String getUrlEndpoint() {
        return urlEndpoint;
    }

    public void setUrlEndpoint(String urlEndpoint) {
        this.urlEndpoint = urlEndpoint;
    }
}
