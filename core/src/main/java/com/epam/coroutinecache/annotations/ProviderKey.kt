package com.epam.coroutinecache.annotations

import kotlin.reflect.KClass

/**
 * Annotation that describes key, which will be used to store function result
 *
 * @param key - data's key in cache
 * @param entryClass - entry's class in cache, for types such as List<T>, Map<K, T> pass only T class
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class ProviderKey(val key: String, val entryClass: KClass<*> = ProviderKey::class)