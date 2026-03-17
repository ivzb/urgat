package com.ivzb.urgat.ui.weights

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.ivzb.urgat.data.weight.Weight
import com.ivzb.urgat.ui.components.ListItem
import com.ivzb.urgat.ui.components.charts.LineChart
import com.ivzb.urgat.ui.components.charts.LineChartItem
import com.ivzb.urgat.util.format
import com.ivzb.urgat.util.toLocalDate

@Composable
fun WeightsChart(
    items: List<Weight>,
    modifier: Modifier = Modifier
) {

    LineChart(
        items = items.map { LineChartItem(it.value.toFloat(), it.date) },
        modifier = modifier
    )
}

@Composable
fun WeightItem(
    weight: Weight,
    onWeightClick: (Weight) -> Unit,
) {
    val date = remember { weight.date.toLocalDate() }

    ListItem(
        textLeft = date.dayOfMonth.toString(),
        subTextLeft = date.month.name.take(3),

        textRight = weight.value.format(),
        subTextRight = "",

        onClick = { onWeightClick(weight) }
    )
}