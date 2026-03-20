package com.server.core.dto;

import com.server.core.model.data.Header;

/**
 * @author Kazi Tanvir Azad
 */
public record HeaderDto(String key, String value) {
    public HeaderDto(Header header) {
        this(header.getKey(), header.getValue());
    }
}
