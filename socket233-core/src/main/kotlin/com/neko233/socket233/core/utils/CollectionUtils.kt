package com.neko233.socket233.core.utils

object CollectionUtils {

    @JvmStatic
    fun <T> isEmpty(collection: Collection<T>?): Boolean {
        return collection == null || collection.isEmpty()
    }

    @JvmStatic
    fun <T> isNotEmpty(collection: Collection<T>?): Boolean {
        return !isEmpty(collection)
    }
}
