package com.server.core.function;

/**
 * @author Kazi Tanvir Azad
 */
@FunctionalInterface
public interface CheckedConsumer<T> {
    void accept(T t) throws Exception;
}
