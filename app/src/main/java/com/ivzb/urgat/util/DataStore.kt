package com.ivzb.urgat.util

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.math.BigDecimal

fun dataStoreStringFlow(
    dataStore: DataStore<Preferences>,
    key: Preferences.Key<String>,
    defaultValue: String,
): Flow<String> = dataStoreFlow(dataStore, key, defaultValue) { value ->
    value ?: defaultValue
}

fun dataStoreBigDecimalFlow(
    dataStore: DataStore<Preferences>,
    key: Preferences.Key<String>,
    defaultValue: BigDecimal,
): Flow<BigDecimal?> = dataStoreFlow(dataStore, key, defaultValue) { value ->
    when {
        value == null -> defaultValue
        else -> value.toBigDecimalOrNull()
    }
}

inline fun <reified T : Enum<T>> dataStoreEnumFlow(
    dataStore: DataStore<Preferences>,
    key: Preferences.Key<String>,
    defaultValue: T,
): Flow<T> = dataStoreFlow(dataStore, key, defaultValue) { value ->
    when {
        value != null -> enumValueOf<T>(value)
        else -> defaultValue
    }
}

fun <T, R> dataStoreFlow(
    dataStore: DataStore<Preferences>,
    key: Preferences.Key<T>,
    defaultValue: R,
    transform: (T?) -> R,
): Flow<R> = dataStore.data
    .catch { exception ->
        Tracker.error(exception)
        emit(emptyPreferences())
    }.map { preferences ->
        try {
            transform(preferences[key])
        } catch (e: Exception) {
            Tracker.error(e)
            defaultValue
        }
    }