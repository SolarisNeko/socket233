package com.neko233.socket233.core.utils;

import java.io.Closeable;

/**
 * close must success, if error no one can help you..
 */

public class CloseableUtils {

    public static void close(AutoCloseable... autoClosable) {
        for (AutoCloseable autoCloseable : autoClosable) {
            if (autoCloseable == null) {
                return;
            }
            try {
                autoCloseable.close();
            } catch (Exception ignored) {

            }
        }
    }

    public static void close(Closeable... autoClosable) {
        for (AutoCloseable autoCloseable : autoClosable) {
            if (autoCloseable == null) {
                return;
            }
            try {
                autoCloseable.close();
            } catch (Exception ignored) {
            }
        }
    }

}
