package com.ivzb.urgat.util

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ivzb.urgat.data.dataStore.FakePreferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class DataStoreTest {

    private enum class Fruit { APPLE, PEAR }

    private val dataStore = FakePreferencesDataStore()

    @Test
    fun `dataStoreEnumFlow emits default value for an unknown enum name`() = runTest {
        val key = stringPreferencesKey("fruit")
        dataStore.edit { it[key] = "BANANA" }

        val value = dataStoreEnumFlow(dataStore, key, Fruit.PEAR).first()

        assertEquals(Fruit.PEAR, value)
    }

    @Test
    fun `dataStoreLongFlow emits default value when the key holds another type`() = runTest {
        dataStore.edit { it[stringPreferencesKey("count")] = "not a long" }

        val value = dataStoreLongFlow(dataStore, longPreferencesKey("count"), 7L).first()

        assertEquals(7L, value)
    }

    @Test(expected = IllegalStateException::class)
    fun `dataStoreFlow rethrows unexpected exceptions`() = runTest {
        val key = stringPreferencesKey("fruit")

        dataStoreFlow<String, String>(dataStore, key, "default") { error("bug") }.first()
    }

}
