package com.neko233.socket233.core.engine;

/**
 * @author SolarisNeko
 * Date on 2023-06-01
 */
public interface LifeCycle {

    default void init() throws Throwable {
    }

    default void preCreate() throws Throwable {
    }

    default void create() throws Throwable {
    }

    default void postCreate() throws Throwable {
    }


    default void shutdown() {
    }


    default void preDestroy() {
    }

    default void destroy() {
    }

    default void postDestroy() {
    }

}
