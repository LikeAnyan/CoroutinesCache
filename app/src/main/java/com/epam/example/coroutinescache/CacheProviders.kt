package com.epam.example.coroutinescache

import com.epam.coroutinecache.annotations.Expirable
import com.epam.coroutinecache.annotations.LifeTime
import com.epam.coroutinecache.annotations.ProviderKey
import com.epam.coroutinecache.annotations.UseIfExpired
import kotlinx.coroutines.Deferred
import java.util.concurrent.TimeUnit

interface CacheProviders {

    @ProviderKey("TestKey", Data::class)
    @LifeTime(value = 1L, unit = TimeUnit.MINUTES)
    @Expirable
    @UseIfExpired
    fun getData(data: Deferred<Data>): Deferred<Data>
}