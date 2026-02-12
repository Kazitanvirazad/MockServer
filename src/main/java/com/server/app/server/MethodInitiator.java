package com.server.app.server;

import java.util.ArrayList;
import java.util.List;

/**
 * author: Kazi Tanvir Azad
 */
public class MethodInitiator {
    private int responseCode;
    private long delay;
    private String serverId;
    private String serverName;
    private String responseData;
    private List<KeyValue> headers;

    public MethodInitiator() {
        this.headers = new ArrayList<>();
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public List<KeyValue> getHeaders() {
        return headers;
    }

    public void setHeaders(List<KeyValue> headers) {
        this.headers = headers;
    }

    public void addHeader(KeyValue header) {
        this.headers.add(header);
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseData() {
        return responseData;
    }

    public void setResponseData(String responseData) {
        this.responseData = responseData;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
}
