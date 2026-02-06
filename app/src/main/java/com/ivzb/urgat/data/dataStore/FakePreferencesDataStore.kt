package com.ivzb.urgat.data.dataStore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class FakePreferencesDataStore(
    initialPreferences: Preferences = emptyPreferences()
) : DataStore<Preferences> {

    private val mutex = Mutex()
    private val state = MutableStateFlow(initialPreferences)

    override val data: Flow<Preferences> = state

    override suspend fun updateData(
        transform: suspend (t: Preferences) -> Preferences
    ): Preferences {
        mutex.withLock {
            val newPrefs = transform(state.value)
            state.value = newPrefs
            return newPrefs
        }
    }
}
