package com.server.app.model.data;

import com.server.app.dto.HeaderDto;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import static com.server.app.constants.ApplicationConstants.EQUALS_CHAR;
import static com.server.app.util.AppUtil.generateUUID7BasedId;
import static com.server.app.util.AppUtil.triggerErrorAlert;

/**
 * author: Kazi Tanvir Azad
 */
public class Header implements Serializable {
    @Serial
    private static final long serialVersionUID = 1369250911334029278L;
    private final transient String headerViewUUID;
    private String key;
    private String value;

    public Header(HeaderDto headerDto) {
        this();
        this.key = headerDto.key();
        this.value = headerDto.value();
    }

    public Header(String key, String value) {
        this();
        this.key = key;
        this.value = value;
    }

    public Header() {
        Optional<String> uid = generateUUID7BasedId();
        if (uid.isEmpty()) {
            triggerErrorAlert("Something went wrong while creating Header!",
                    "Please try again later.");
            throw new RuntimeException("UUID7 generate failed while creating Header!");
        }
        this.headerViewUUID = uid.get();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return key + EQUALS_CHAR + value;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Header header)) return false;
        return Objects.equals(headerViewUUID, header.headerViewUUID);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(headerViewUUID);
    }
}
