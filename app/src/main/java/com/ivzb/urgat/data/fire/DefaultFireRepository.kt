package com.ivzb.urgat.data.fire

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ivzb.urgat.util.Tracker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.math.BigDecimal
import javax.inject.Inject

class DefaultFireRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
): FireRepository {

    override val withdrawalRateFlow: Flow<BigDecimal?> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Tracker.error(exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val value = preferences[FIRE_WITHDRAWAL_RATE]

            when {
                value == null -> DEFAULT_FIRE_WITHDRAWAL_RATE
                else -> value.toBigDecimalOrNull()
            }
        }

    override suspend fun updateWithdrawalRate(value: String) {
        dataStore.edit { preferences ->
            preferences[FIRE_WITHDRAWAL_RATE] = value
        }
    }

    companion object {

        private val FIRE_WITHDRAWAL_RATE = stringPreferencesKey("fire_withdrawal_rate")
        private val DEFAULT_FIRE_WITHDRAWAL_RATE = 4.toBigDecimal()

    }

}