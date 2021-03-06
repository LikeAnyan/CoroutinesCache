package com.epam.coroutinecache.core.actions

import com.epam.coroutinecache.core.Memory
import com.epam.coroutinecache.core.Persistence
import com.epam.coroutinecache.core.Record
import com.epam.coroutinecache.core.Source
import com.epam.coroutinecache.internal.RecordExpiredChecker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async

/**
 * Action that retrieves record from the cache
 */
class GetRecordAction(
        private val deleteExpiredRecordsAction: DeleteExpiredRecordsAction,
        private val recordExpiredChecker: RecordExpiredChecker,
        private val diskCache: Persistence,
        private val memory: Memory,
        private val scope: CoroutineScope
) {

    /**
     * Async function that retrieves record from cache.
     * If record is expired and flag useIfExpired is true then return record and delete it from cache,
     * otherwise only delete record from cache and return null
     */
    suspend fun <T> getRecord(key: String, useRecordEvenIfExpired: Boolean = false): Record<T>? = scope.async {
        var record = memory.getRecord<T>(key)

        if (record != null) {
            record.setSource(Source.MEMORY)
        } else {
            record = diskCache.getRecord(key)
            if (record != null) {
                record.setSource(Source.PERSISTENCE)
                memory.saveRecord(key, record)
            } else {
                return@async null
            }
        }

        if (recordExpiredChecker.hasRecordExpired(record)) {
            deleteExpiredRecordsAction.deleteExpiredRecord(key, record)
            return@async if (useRecordEvenIfExpired) record else null
        }

        return@async record
    }.await()
}