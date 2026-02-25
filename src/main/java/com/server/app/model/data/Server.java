package com.server.app.model.data;

import com.server.app.constants.Method;
import com.server.app.dto.ServerDto;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.server.app.constants.ApplicationConstants.COLON;
import static com.server.app.constants.ApplicationConstants.DEFAULT_PATH;
import static com.server.app.constants.ApplicationConstants.EMPTY_SPACE;
import static com.server.app.constants.ApplicationConstants.EMPTY_STRING;

/**
 * @author Kazi Tanvir Azad
 */
public class Server implements Comparable<Server> {
    private long delay;
    private int port;
    private Integer responseCode;
    private String serverId;
    private String collectionId;
    private String serverName;
    private String urlEndpoint;
    private String responseData;
    private Method method;
    private Timestamp createdOn;
    private Timestamp modifiedOn;
    private List<Header> headers;
    private List<Cookie> cookies;

    public Server(ServerDto serverDto) {
        this();
        this.delay = serverDto.delay();
        this.port = serverDto.port();
        this.responseCode = serverDto.responseCode();
        this.serverName = serverDto.serverName();
        if (StringUtils.isNotBlank(serverDto.urlEndpoint())) this.urlEndpoint = serverDto.urlEndpoint();
        if (StringUtils.isNotBlank(serverDto.responseData())) this.responseData = serverDto.responseData();
        if (null != serverDto.method()) this.method = serverDto.method();
        if (Objects.nonNull(serverDto.createdOn())) this.createdOn = serverDto.createdOn();
        else this.createdOn = Timestamp.from(Instant.now());
        this.modifiedOn = Timestamp.from(Instant.now());
        if (CollectionUtils.isNotEmpty(serverDto.headers())) {
            this.headers = serverDto.headers()
                    .stream()
                    .filter(Objects::nonNull)
                    .filter(headerDto -> StringUtils.isNotBlank(headerDto.key())
                            && StringUtils.isNotBlank(headerDto.value()))
                    .map(Header::new)
                    .collect(Collectors.toList());
        }
        if (CollectionUtils.isNotEmpty(serverDto.cookies())) {
            this.cookies = serverDto.cookies()
                    .stream()
                    .filter(Objects::nonNull)
                    .filter(cookieDto -> StringUtils.isNotBlank(cookieDto.name())
                            && StringUtils.isNotBlank(cookieDto.value()))
                    .map(Cookie::new)
                    .collect(Collectors.toList());
        }
    }

    public Server(List<Cookie> cookies, long delay, List<Header> headers, Method method, int port, Integer responseCode,
                  String responseData, String serverId, String serverName, String urlEndpoint) {
        this();
        this.cookies = cookies;
        this.delay = delay;
        this.headers = headers;
        if (null != method) this.method = method;
        this.port = port;
        this.responseCode = responseCode;
        if (StringUtils.isNotBlank(responseData)) this.responseData = responseData;
        this.serverId = serverId;
        this.serverName = serverName;
        if (StringUtils.isNotBlank(urlEndpoint)) this.urlEndpoint = urlEndpoint;
    }

    public Server(long delay, List<Header> headers, Method method, int port, Integer responseCode, String responseData,
                  String serverId, String serverName, String urlEndpoint) {
        this();
        this.delay = delay;
        this.headers = headers;
        if (null != method) this.method = method;
        this.port = port;
        this.responseCode = responseCode;
        if (StringUtils.isNotBlank(responseData)) this.responseData = responseData;
        this.serverId = serverId;
        this.serverName = serverName;
        if (StringUtils.isNotBlank(urlEndpoint)) this.urlEndpoint = urlEndpoint;
    }

    public Server(long delay, Method method, int port, Integer responseCode, String serverId, String serverName,
                  String urlEndpoint) {
        this();
        this.delay = delay;
        if (null != method) this.method = method;
        this.port = port;
        this.responseCode = responseCode;
        this.serverId = serverId;
        this.serverName = serverName;
        if (StringUtils.isNotBlank(urlEndpoint)) this.urlEndpoint = urlEndpoint;
    }

    public Server() {
        this.responseCode = 200;
        this.method = Method.GET;
        this.delay = 0L;
        this.urlEndpoint = DEFAULT_PATH;
        this.responseData = EMPTY_STRING;
    }

    @Override
    public String toString() {
        return method.name() + EMPTY_SPACE + "http://localhost" + COLON + port +
                (null != urlEndpoint && urlEndpoint.startsWith(DEFAULT_PATH) ? EMPTY_STRING : DEFAULT_PATH)
                + urlEndpoint + " - " + serverName + " - " + responseCode;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public List<Cookie> getCookies() {
        return cookies;
    }

    public void setCookies(List<Cookie> cookies) {
        this.cookies = cookies;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public void setHeaders(List<Header> headers) {
        this.headers = headers;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public void setMethod(String method) {
        try {
            this.method = Method.valueOf(method);
        } catch (Exception ignore) {
        }
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseData() {
        return responseData;
    }

    public void setResponseData(String responseData) {
        this.responseData = responseData;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getUrlEndpoint() {
        return urlEndpoint;
    }

    public void setUrlEndpoint(String urlEndpoint) {
        this.urlEndpoint = urlEndpoint;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = Timestamp.valueOf(createdOn);
    }

    public Timestamp getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Timestamp modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public void setModifiedOn(String modifiedOn) {
        this.modifiedOn = Timestamp.valueOf(modifiedOn);
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    @Override
    public int compareTo(Server o) {
        // Sorting in descending order to view the recently updated in the top of the tableview
        if (this.modifiedOn.before(o.getModifiedOn())) {
            return 1;
        }
        if (this.modifiedOn.after(o.getModifiedOn())) {
            return -1;
        } else {
            return 0;
        }
    }
}
