package com.server.core.function;

/**
 * @author Kazi Tanvir Azad
 */
@FunctionalInterface
public interface CheckedFunction<T, R> {
    R apply(T t) throws Exception;
}
