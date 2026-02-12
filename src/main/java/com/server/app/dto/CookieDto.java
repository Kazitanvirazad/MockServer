package com.server.app.dto;

import com.server.app.model.data.Cookie;

public record CookieDto(boolean httpOnly,
                        boolean partitioned,
                        boolean secure,
                        Long maxAge,
                        String name,
                        String value,
                        String domain,
                        String expires,
                        String path,
                        String sameSite) {
    public CookieDto(Cookie cookie) {
        this(cookie.isHttpOnly(),
                cookie.isPartitioned(),
                cookie.isSecure(),
                cookie.getMaxAge(),
                cookie.getName(),
                cookie.getValue(),
                cookie.getDomain(),
                cookie.getExpires(),
                cookie.getPath(),
                cookie.getSameSite());
    }
}
