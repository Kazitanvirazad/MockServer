package com.server.app.function;

/**
 * author: Kazi Tanvir Azad
 */
@FunctionalInterface
public interface CheckedFunction<T, R> {
    R apply(T t) throws Exception;
}
