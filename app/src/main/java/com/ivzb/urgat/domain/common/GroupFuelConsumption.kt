package com.ivzb.urgat.domain.common

import com.ivzb.urgat.data.fuelConsumption.FuelConsumption
import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionCategory
import com.ivzb.urgat.domain.common.Group.byDateMonthly
import com.ivzb.urgat.domain.common.Group.byDateYearly
import com.ivzb.urgat.domain.fuelConsumption.GroupedFuelConsumptionCategories
import com.ivzb.urgat.ui.components.GroupedItems
import kotlin.collections.component1
import kotlin.collections.component2

object GroupFuelConsumption {

    fun groupFuelConsumptionCategoriesMonthly(fuelConsumption: List<FuelConsumption>, random: () -> String): List<GroupedFuelConsumptionCategories> =
        byDateMonthly(fuelConsumption, FuelConsumption::date, FuelConsumption::total)
            .map { (group, total, items) ->
                GroupedItems(
                    group = group,
                    total = total,
                    items = groupFuelConsumption(items, random),
                )
            }

    fun groupFuelConsumptionCategoriesYearly(fuelConsumption: List<FuelConsumption>, random: () -> String): List<GroupedFuelConsumptionCategories> =
        byDateYearly(fuelConsumption, FuelConsumption::date, FuelConsumption::total)
            .map { (group, total, items) ->
                GroupedItems(
                    group = group,
                    total = total,
                    items = groupFuelConsumption(items, random),
                )
            }

    fun groupFuelConsumptionCategoriesAllTime(fuelConsumption: List<FuelConsumption>, random: () -> String): List<FuelConsumptionCategory> =
        groupFuelConsumption(fuelConsumption, random)

    private fun groupFuelConsumption(fuelConsumption: List<FuelConsumption>, random: () -> String): List<FuelConsumptionCategory> =
        fuelConsumption
            .groupBy { it.type }
            .map { (category, fuelConsumption) ->
                FuelConsumptionCategory(
                    id = random(),
                    name = category,
                    totals = FuelConsumption.total(fuelConsumption),

                    fuelConsumption = fuelConsumption,
                )
            }
            .sortedBy { it.name }

}
