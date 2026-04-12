package com.ivzb.urgat.ui.fuelConsumption

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.AnnotatedString
import com.ivzb.urgat.R
import com.ivzb.urgat.data.fuelConsumption.FuelConsumption
import com.ivzb.urgat.domain.fuelConsumption.GroupedFuelConsumption
import com.ivzb.urgat.ui.components.ItemHeaderSmall
import com.ivzb.urgat.ui.components.ListItem
import com.ivzb.urgat.util.annotateFuelConsumptionTotals
import com.ivzb.urgat.util.format
import com.ivzb.urgat.util.paddingIf
import com.ivzb.urgat.util.toLocalDate

@Composable
fun TransactionsPage(
    fuelConsumption: List<GroupedFuelConsumption>,
    onFuelConsumptionClick: (FuelConsumption) -> Unit,

    header: (@Composable () -> Unit)? = null,

    listState: LazyListState,
    onListStateChanged: (LazyListState) -> Unit,
) {

    LaunchedEffect(Unit) {
        onListStateChanged(listState)
    }

    LazyColumn(
        contentPadding = PaddingValues(
            horizontal = dimensionResource(R.dimen.content_padding_horizontal),
            vertical = dimensionResource(R.dimen.content_padding_vertical),
        ),
        state = listState,
        modifier = Modifier
            .fillMaxSize(),
    ) {

        item {
            header?.invoke()
        }

        fuelConsumption.forEachIndexed { index, (group, total, fuelConsumption) ->
            item {
                ItemHeaderSmall(
                    text = group,
                    total = annotateFuelConsumptionTotals(total),
                    modifier = Modifier
                        .paddingIf(index == 0, bottom = dimensionResource(R.dimen.vertical_margin))
                        .paddingIf(index > 0, vertical = dimensionResource(R.dimen.vertical_margin))
                        .padding(horizontal = dimensionResource(R.dimen.list_item_padding))
                )
            }

            items(
                items = fuelConsumption,
                key = { fuelConsumption -> fuelConsumption.id },
            ) { fuelConsumption ->

                FuelConsumptionItem(
                    fuelConsumption = fuelConsumption,
                    onFuelConsumptionClick = onFuelConsumptionClick,
                )
            }
        }
    }
}

@Composable
fun FuelConsumptionItem(
    fuelConsumption: FuelConsumption,
    onFuelConsumptionClick: (FuelConsumption) -> Unit,
) {
    val date = remember { fuelConsumption.date.toLocalDate() }

    ListItem(
        textLeft = date.dayOfMonth.toString(),
        subTextLeft = date.month.name.take(3),

        textRight = fuelConsumption.carName,
        subTextRight = AnnotatedString("${fuelConsumption.amount.format()}L - ${fuelConsumption.total.format()} ${fuelConsumption.currency}"),

        onClick = { onFuelConsumptionClick(fuelConsumption) }
    )
}

