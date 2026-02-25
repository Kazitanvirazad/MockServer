package com.server.app.dto;

import com.server.app.model.data.Header;

/**
 * @author Kazi Tanvir Azad
 */
public record HeaderDto(String key, String value) {
    public HeaderDto(Header header) {
        this(header.getKey(), header.getValue());
    }
}
