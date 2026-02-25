package com.server.app.util;

import com.server.app.config.AppConfig;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tools.jackson.databind.JavaType;

import java.util.List;
import java.util.Optional;

/**
 * @author Kazi Tanvir Azad
 */
public final class Serializer {
    private static final Logger log = LogManager.getLogger(Serializer.class);

    private Serializer() {
        throw new AssertionError("Initialization of this class is not allowed");
    }

    public static <T> Optional<String> serialize(T pojo) {
        try {
            return Optional.ofNullable(AppConfig.INSTANCE.getMapper().writeValueAsString(pojo));
        } catch (RuntimeException exception) {
            log.error(exception.getMessage());
        }
        return Optional.empty();
    }

    public static <T> Optional<T> deSerialize(String json, Class<T> clazz) {
        if (StringUtils.isBlank(json)) {
            return Optional.empty();
        }
        try {
            return Optional.ofNullable(AppConfig.INSTANCE.getMapper().readValue(json, clazz));
        } catch (RuntimeException exception) {
            log.error(exception.getMessage());
        }
        return Optional.empty();
    }

    public static <T> Optional<String> serializeList(List<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return Optional.empty();
        }
        try {
            return Optional.ofNullable(AppConfig.INSTANCE.getMapper().writeValueAsString(list));
        } catch (RuntimeException exception) {
            log.error(exception.getMessage());
        }
        return Optional.empty();
    }

    public static <T> Optional<List<T>> deSerializeList(String json, Class<T> clazz) {
        if (StringUtils.isBlank(json)) {
            return Optional.empty();
        }
        JavaType type = AppConfig.INSTANCE.getMapper()
                .getTypeFactory().constructCollectionType(List.class, clazz);
        try {
            return Optional.ofNullable(AppConfig.INSTANCE.getMapper().readValue(json, type));
        } catch (RuntimeException exception) {
            log.error(exception.getMessage());
        }
        return Optional.empty();
    }
}
