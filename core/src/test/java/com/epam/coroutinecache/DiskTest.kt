package com.epam.coroutinecache

import com.epam.coroutinecache.core.Record
import com.epam.coroutinecache.utils.MockDataString
import com.epam.coroutinecache.utils.Types
import org.junit.*

import org.junit.Assert.*

class DiskTest: BaseTest() {


    @Test
    fun testRetrieveRecordAsObject() {
        diskCache.saveRecord(KEY, Record(MockDataString(VALUE_STRING)))

        val retrievedData = diskCache.getRecord<MockDataString>(KEY, MockDataString::class.java)
        assertEquals(retrievedData?.getData()?.getMessage(), VALUE_STRING)
    }

    @Test
    fun testRetrieveRecordAsCollection() {
        val mocks = listOf(MockDataString(VALUE_STRING), MockDataString(VALUE_STRING + 1))
        diskCache.saveRecord(KEY, Record(mocks))

        val retrievedData = diskCache.getRecord<List<MockDataString>>(KEY, Types.newParameterizedType(List::class.java, MockDataString::class.java))
        for (i in 0 until mocks.size) {
            assertEquals(mocks[i], retrievedData?.getData()?.get(i))
        }
    }

    @Test
    fun testRetrieveRecordAsArray() {
        val mocks = arrayOf(MockDataString(VALUE_STRING), MockDataString(VALUE_STRING + 1))
        diskCache.saveRecord(KEY, Record(mocks))

        val retrievedData = diskCache.getRecord<Array<MockDataString>>(KEY, Types.arrayOf(MockDataString::class.java))
        assertArrayEquals(mocks, retrievedData?.getData())
    }

    @Test
    fun testRetrieveRecordAsMap() {
        val testMap = HashMap<Int, MockDataString>()
        testMap[0] = MockDataString(VALUE_STRING)
        testMap[1] = MockDataString(VALUE_STRING + 1)
        diskCache.saveRecord(KEY, Record(testMap))

        val retrievedData = diskCache.getRecord<Map<Int, MockDataString>>(KEY, Types.newParameterizedType(Map::class.java, Int::class.javaObjectType, MockDataString::class.java))
        for ((k, v) in testMap) {
            assertEquals(v, retrievedData?.getData()?.get(k))
        }
    }

    @Test
    fun testRetrieveRecordAsMultilevelMap() {
        val innerMap = HashMap<Any, Any>()
        innerMap["test"] = VALUE_STRING
        innerMap["test2"] = VALUE_STRING + 1

        val testMap = HashMap<Int, Any>()
        testMap[0] = VALUE_STRING + 2
        testMap[1] = VALUE_STRING + 3
        testMap[2] = innerMap

        diskCache.saveRecord(KEY, Record(testMap))

        val retrievedData = diskCache.getRecord<Map<Any, Any>>(KEY, Types.newParameterizedType(Map::class.java, Integer::class.javaObjectType, Any::class.java))
        assertEquals(retrievedData?.getData()?.get(0), VALUE_STRING + 2)
        assertEquals(retrievedData?.getData()?.get(1), VALUE_STRING + 3)

        val mapObject = retrievedData?.getData()?.get(2)
        assertTrue(mapObject is Map<*, *>)
        mapObject as Map<*, *>
        assertEquals(mapObject["test"], VALUE_STRING)
        assertEquals(mapObject["test2"], VALUE_STRING + 1)
    }

    @Test
    fun testRetrieveTwoLevelList() {
        val inputList = listOf(listOf(MockDataString(VALUE_STRING), MockDataString(VALUE_STRING + 1)), listOf(MockDataString(VALUE_STRING + 2), MockDataString(VALUE_STRING + 3)))
        diskCache.saveRecord(KEY, Record(inputList))

        val retrievedData = diskCache.getRecord<List<List<MockDataString>>>(KEY, Types.newParameterizedType(List::class.java, Types.newParameterizedType(List::class.java, MockDataString::class.java)))
        for (i in 0 until inputList.size) {
            assertEquals(inputList[i], retrievedData?.getData()?.get(i))
            for (j in 0 until inputList[i].size) {
                assertEquals(inputList[i][j], retrievedData?.getData()?.get(i)?.get(j))
            }
        }
    }

    @Test
    fun testRetrieveSet() {
        val inputSet = setOf(MockDataString(VALUE_STRING), MockDataString(VALUE_STRING + 1), MockDataString(VALUE_STRING + 2))
        diskCache.saveRecord(KEY, Record(inputSet))

        val retrievedData = diskCache.getRecord<Set<MockDataString>>(KEY, Types.newParameterizedType(Set::class.java, MockDataString::class.java))
        for (i in 0 until inputSet.size) {
            assertEquals(inputSet.elementAt(i), retrievedData?.getData()?.elementAt(i))
        }
    }

    @Test
    fun testRemoveKeys() {
        for (i in 0 until 100) {
            diskCache.saveRecord(i.toString(), Record(VALUE_STRING))
        }

        assertEquals(diskCache.allKeys().size, 100)
        diskCache.deleteAll()
        assertEquals(diskCache.allKeys().size, 0)
    }

    companion object {
        private const val KEY: String = "store/key"
        private const val VALUE_STRING: String = "Stored data"
    }
}