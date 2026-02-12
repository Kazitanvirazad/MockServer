package com.server.app.dto;

import com.server.app.model.data.Header;

public record HeaderDto(String key, String value) {
    public HeaderDto(Header header) {
        this(header.getKey(), header.getValue());
    }
}
