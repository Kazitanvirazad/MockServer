package com.server.app.model.data;

import com.server.app.dto.CollectionDto;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.server.app.util.AppUtil.RESPONSE_CODE_RANGE;
import static com.server.app.util.AppUtil.SERVER_PORT_RANGE;

/**
 * @author Kazi Tanvir Azad
 */
public class Collection implements Comparable<Collection> {
    private String collectionId;
    private String collectionName;
    private Timestamp createdOn;
    private Timestamp modifiedOn;
    private List<Server> servers;

    public Collection(CollectionDto collectionDto) {
        this.collectionName = collectionDto.collectionName();
        if (Objects.nonNull(collectionDto.createdOn())) this.createdOn = collectionDto.createdOn();
        else this.createdOn = Timestamp.from(Instant.now());
        this.modifiedOn = Timestamp.from(Instant.now());
        if (CollectionUtils.isNotEmpty(collectionDto.servers())) {
            this.servers = collectionDto.servers()
                    .stream()
                    .filter(Objects::nonNull)
                    .filter(serverDto -> SERVER_PORT_RANGE.contains(serverDto.port())
                            && StringUtils.isNotBlank(serverDto.serverName())
                            && serverDto.delay() >= 0L
                            && RESPONSE_CODE_RANGE.contains(serverDto.responseCode()))
                    .map(Server::new)
                    .collect(Collectors.toList());
        }
    }

    public Collection(String collectionId, String collectionName, List<Server> servers) {
        this.collectionId = collectionId;
        this.collectionName = collectionName;
        this.servers = servers;
    }

    public Collection(String collectionId, String collectionName) {
        this.collectionId = collectionId;
        this.collectionName = collectionName;
    }

    public Collection(String collectionName) {
        this.collectionName = collectionName;
    }

    public Collection() {
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public List<Server> getServers() {
        return servers;
    }

    public void setServers(List<Server> servers) {
        this.servers = servers;
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Collection that)) return false;
        return Objects.equals(getCollectionId(), that.getCollectionId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getCollectionId());
    }

    @Override
    public String toString() {
        return collectionName;
    }

    @Override
    public int compareTo(Collection o) {
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
