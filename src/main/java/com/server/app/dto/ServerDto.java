package com.server.app.dto;

import com.server.app.constants.Method;
import com.server.app.model.data.Server;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

public record ServerDto(long delay,
                        int port,
                        Integer responseCode,
                        String serverName,
                        String urlEndpoint,
                        String responseData,
                        Method method,
                        Timestamp createdOn,
                        List<HeaderDto> headers,
                        List<CookieDto> cookies) {
    public ServerDto(Server server) {
        List<HeaderDto> headers = null;
        List<CookieDto> cookies = null;
        if (CollectionUtils.isNotEmpty(server.getHeaders())) {
            headers = server.getHeaders()
                    .stream()
                    .filter(ObjectUtils::isNotEmpty)
                    .map(HeaderDto::new)
                    .collect(Collectors.toList());
        }
        if (CollectionUtils.isNotEmpty(server.getCookies())) {
            cookies = server.getCookies()
                    .stream()
                    .filter(ObjectUtils::isNotEmpty)
                    .map(CookieDto::new)
                    .collect(Collectors.toList());
        }
        this(server.getDelay(),
                server.getPort(),
                server.getResponseCode(),
                server.getServerName(),
                server.getUrlEndpoint(),
                server.getResponseData(),
                server.getMethod(),
                server.getCreatedOn(),
                headers,
                cookies);
    }
}
