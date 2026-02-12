package com.server.app.dto;

import com.server.app.model.data.Collection;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.sql.Timestamp;
import java.util.List;

public record CollectionDto(String collectionName,
                            Timestamp createdOn,
                            List<ServerDto> servers) {
    public CollectionDto(Collection collection) {
        List<ServerDto> servers = null;
        if (CollectionUtils.isNotEmpty(collection.getServers())) {
            servers = collection.getServers()
                    .stream()
                    .filter(ObjectUtils::isNotEmpty)
                    .map(ServerDto::new)
                    .toList();
        }
        this(collection.getCollectionName(),
                collection.getCreatedOn(),
                servers);
    }
}
