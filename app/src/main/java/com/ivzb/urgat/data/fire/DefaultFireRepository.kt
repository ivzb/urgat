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

    override val withdrawalRateFlow: Flow<BigDecimal?> = dataStoreFlow(dataStore, FIRE_WITHDRAWAL_RATE, DEFAULT_FIRE_WITHDRAWAL_RATE)

    override val returnOfInvestmentFlow: Flow<BigDecimal?> = dataStoreFlow(dataStore, FIRE_RETURN_OF_INVESTMENT, DEFAULT_FIRE_RETURN_OF_INVESTMENT)

    override val taxRateFlow: Flow<BigDecimal?> = dataStoreFlow(dataStore, FIRE_TAX_RATE, DEFAULT_FIRE_TAX_RATE)

    override suspend fun updateWithdrawalRate(value: String) {
        dataStore.edit { preferences ->
            preferences[FIRE_WITHDRAWAL_RATE] = value
        }
    }

    override suspend fun updateReturnOfInvestment(value: String) {
        dataStore.edit { preferences ->
            preferences[FIRE_RETURN_OF_INVESTMENT] = value
        }
    }

    override suspend fun updateTaxRate(value: String) {
        dataStore.edit { preferences ->
            preferences[FIRE_TAX_RATE] = value
        }
    }

    companion object {

        private val FIRE_WITHDRAWAL_RATE = stringPreferencesKey("fire_withdrawal_rate")

        private val FIRE_RETURN_OF_INVESTMENT = stringPreferencesKey("fire_return_of_investment")

        private val FIRE_TAX_RATE = stringPreferencesKey("fire_tax_rate")

        private val DEFAULT_FIRE_WITHDRAWAL_RATE = 4.toBigDecimal()

        private val DEFAULT_FIRE_RETURN_OF_INVESTMENT = 7.toBigDecimal()

        private val DEFAULT_FIRE_TAX_RATE = 10.toBigDecimal()

        private fun dataStoreFlow(
            dataStore: DataStore<Preferences>,
            key: Preferences.Key<String>,
            defaultValue: BigDecimal,
        ): Flow<BigDecimal?> = dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    Tracker.error(exception)
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }.map { preferences ->
                val value = preferences[key]

                when {
                    value == null -> defaultValue
                    else -> value.toBigDecimalOrNull()
                }
            }

    }

}