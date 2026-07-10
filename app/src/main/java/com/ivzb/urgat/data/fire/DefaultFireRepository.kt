package com.ivzb.urgat.data.fire

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ivzb.urgat.data.fire.FireRepository.Companion.DEFAULT_FIRE_DATE_RANGE_TYPE
import com.ivzb.urgat.data.fire.FireRepository.Companion.DEFAULT_FIRE_RETURN_OF_INVESTMENT
import com.ivzb.urgat.data.fire.FireRepository.Companion.DEFAULT_FIRE_TAX_RATE
import com.ivzb.urgat.data.fire.FireRepository.Companion.DEFAULT_FIRE_WITHDRAWAL_RATE
import com.ivzb.urgat.ui.fire.FireDateRangeType
import com.ivzb.urgat.util.dataStoreBigDecimalFlow
import com.ivzb.urgat.util.dataStoreEnumFlow
import com.ivzb.urgat.util.dataStoreLongFlow
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import javax.inject.Inject

class DefaultFireRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
): FireRepository {

    override val withdrawalRateFlow: Flow<BigDecimal?> = dataStoreBigDecimalFlow(dataStore, FIRE_WITHDRAWAL_RATE, DEFAULT_FIRE_WITHDRAWAL_RATE)

    override suspend fun updateWithdrawalRate(value: String) {
        dataStore.edit { preferences ->
            preferences[FIRE_WITHDRAWAL_RATE] = value
        }
    }

    override val returnOfInvestmentFlow: Flow<BigDecimal?> = dataStoreBigDecimalFlow(dataStore, FIRE_RETURN_OF_INVESTMENT, DEFAULT_FIRE_RETURN_OF_INVESTMENT)

    override suspend fun updateReturnOfInvestment(value: String) {
        dataStore.edit { preferences ->
            preferences[FIRE_RETURN_OF_INVESTMENT] = value
        }
    }

    override val taxRateFlow: Flow<BigDecimal?> = dataStoreBigDecimalFlow(dataStore, FIRE_TAX_RATE, DEFAULT_FIRE_TAX_RATE)

    override suspend fun updateTaxRate(value: String) {
        dataStore.edit { preferences ->
            preferences[FIRE_TAX_RATE] = value
        }
    }

    override val fireDateRangeTypeFlow: Flow<FireDateRangeType> = dataStoreEnumFlow(dataStore, FIRE_DATE_RANGE_TYPE, DEFAULT_FIRE_DATE_RANGE_TYPE)

    override suspend fun updateFireDateRangeType(value: FireDateRangeType) {
        dataStore.edit { preferences ->
            preferences[FIRE_DATE_RANGE_TYPE] = value.name
        }
    }

    override val fireCustomStartFlow: Flow<Long> = dataStoreLongFlow(dataStore, FIRE_CUSTOM_START, 0L)

    override suspend fun updateFireCustomStart(value: Long) {
        dataStore.edit { preferences ->
            preferences[FIRE_CUSTOM_START] = value
        }
    }

    override val fireCustomEndFlow: Flow<Long> = dataStoreLongFlow(dataStore, FIRE_CUSTOM_END, 0L)

    override suspend fun updateFireCustomEnd(value: Long) {
        dataStore.edit { preferences ->
            preferences[FIRE_CUSTOM_END] = value
        }
    }

    companion object {

        private val FIRE_WITHDRAWAL_RATE = stringPreferencesKey("fire_withdrawal_rate")

        private val FIRE_RETURN_OF_INVESTMENT = stringPreferencesKey("fire_return_of_investment")

        private val FIRE_TAX_RATE = stringPreferencesKey("fire_tax_rate")

        private val FIRE_DATE_RANGE_TYPE = stringPreferencesKey("fire_date_range_type")

        private val FIRE_CUSTOM_START = longPreferencesKey("fire_custom_start")

        private val FIRE_CUSTOM_END = longPreferencesKey("fire_custom_end")

    }

}