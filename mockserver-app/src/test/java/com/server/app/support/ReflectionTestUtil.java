package com.server.app.support;

import java.lang.reflect.Field;

public final class ReflectionTestUtil {
    private ReflectionTestUtil() {
        throw new AssertionError("Initialization of this class is not allowed");
    }

    public static void setField(Object target, String fieldName, Object value) {
        Class<?> currentType = target.getClass();
        while (currentType != null) {
            try {
                Field field = currentType.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(target, value);
                return;
            } catch (NoSuchFieldException exception) {
                currentType = currentType.getSuperclass();
            } catch (IllegalAccessException exception) {
                throw new IllegalStateException("Failed to set field: " + fieldName, exception);
            }
        }
        throw new IllegalArgumentException("Unknown field: " + fieldName);
    }

    public static Object getField(Object target, String fieldName) {
        Class<?> currentType = target.getClass();
        while (currentType != null) {
            try {
                Field field = currentType.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(target);
            } catch (NoSuchFieldException exception) {
                currentType = currentType.getSuperclass();
            } catch (IllegalAccessException exception) {
                throw new IllegalStateException("Failed to get field: " + fieldName, exception);
            }
        }
        throw new IllegalArgumentException("Unknown field: " + fieldName);
    }
}
