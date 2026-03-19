package com.server.core.dto;

import com.server.core.model.data.Collection;
import org.apache.commons.collections4.CollectionUtils;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

/**
 * @author Kazi Tanvir Azad
 */
public record CollectionDto(String collectionName,
                            Timestamp createdOn,
                            List<ServerDto> servers) {
    public CollectionDto(Collection collection) {
        List<ServerDto> servers = null;
        if (CollectionUtils.isNotEmpty(collection.getServers())) {
            servers = collection.getServers()
                    .stream()
                    .filter(Objects::nonNull)
                    .map(ServerDto::new)
                    .toList();
        }
        this(collection.getCollectionName(),
                collection.getCreatedOn(),
                servers);
    }
}
