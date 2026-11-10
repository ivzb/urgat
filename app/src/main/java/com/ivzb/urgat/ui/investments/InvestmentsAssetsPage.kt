package com.ivzb.urgat.ui.investments

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import com.ivzb.urgat.R
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.investment.InvestmentAsset
import com.ivzb.urgat.data.investment.InvestmentAverage
import com.ivzb.urgat.ui.components.EmptyState
import com.ivzb.urgat.ui.components.FilteringUiInfo
import com.ivzb.urgat.ui.components.ListItem
import com.ivzb.urgat.ui.components.charts.PieChart
import com.ivzb.urgat.ui.components.charts.PieChartItem
import com.ivzb.urgat.util.annotateChart
import com.ivzb.urgat.util.annotateCostPrice
import com.ivzb.urgat.util.annotateFinancePnL
import com.ivzb.urgat.util.annotateMarketPrice

@Composable
internal fun AssetsPage(
    group: String,
    total: AnnotatedString?,

    assets: List<InvestmentAsset>,
    expandedAssetNames: List<String>,

    average: InvestmentAverage,

    filteringUiInfo: FilteringUiInfo,
    onResetFilters: () -> Unit,

    onAssetClick: (String) -> Unit,
    onInvestmentClick: (Investment) -> Unit,

    selectedChartTitle: String?,
    onSelectedChartTitleChanged: (String?) -> Unit,

    listState: LazyListState,
    onListStateChanged: (LazyListState) -> Unit,
) {

    LaunchedEffect(Unit) {
        onListStateChanged(listState)
    }

    if (assets.isEmpty()) {
        EmptyState(
            filteringUiInfo = filteringUiInfo,
            onButtonClick = { onResetFilters() },
        )

        return
    }

    val averagePrefix = stringResource(R.string.average_monthly)

    LazyColumn(
        contentPadding = PaddingValues(
            horizontal = dimensionResource(R.dimen.horizontal_margin),
            vertical = dimensionResource(R.dimen.vertical_margin),
        ),
        state = listState,
        modifier = Modifier
            .fillMaxSize(),
    ) {

        item {
            AssetsChart(
                group = group,
                total = total,
                assets = assets,
                expandedAssetNames = expandedAssetNames,
                average = average,
                averagePrefix = averagePrefix,
                selectedChartTitle = selectedChartTitle,
                onSelectedChartTitleChanged = onSelectedChartTitleChanged,
            )
        }

        item {
            Spacer(
                modifier = Modifier
                    .height(dimensionResource(R.dimen.vertical_margin))
            )
        }

        assetItems(
            assets = assets,
            expandedAssetNames = expandedAssetNames,
            onAssetClick = onAssetClick,
            onInvestmentClick = onInvestmentClick,
        )
    }
}

@Composable
private fun AssetsChart(
    group: String,
    total: AnnotatedString?,
    assets: List<InvestmentAsset>,
    expandedAssetNames: List<String>,
    average: InvestmentAverage,
    averagePrefix: String,
    selectedChartTitle: String?,
    onSelectedChartTitleChanged: (String?) -> Unit,
) {
    val lastExpandedName = expandedAssetNames.lastOrNull()
    val expandedAsset = assets.firstOrNull { it.name == lastExpandedName }

    val averageTotals = expandedAsset
        ?.let { average.assets[it.name].orEmpty() }
        ?: average.totals

    // the average is what was put in per month, so it reads the cost price without its P&L
    val averageText = annotateCostPrice(averageTotals, withPnL = false, prefix = "$averagePrefix ")

    val data: List<PieChartItem>
    val defaultText: AnnotatedString

    if (expandedAsset == null) {
        // all assets pie chart
        data = assets.map {
            PieChartItem(
                value = it.marketPrice.toFloat(),
                title = it.name,
                total = annotateMarketPrice(it.totals),
            )
        }
        defaultText = annotateChart(group, total, averageText)
    } else {
        // expanded asset pie chart
        data = expandedAsset.investments
            .groupBy { it.name }
            .map { (name, investments) ->
                val investmentTotal = Investment.total(investments)

                PieChartItem(
                    value = investments.sumOf { it.totalMarketPrice }.toFloat(),
                    title = name,
                    total = annotateMarketPrice(investmentTotal),
                )
            }
        defaultText = annotateChart(expandedAsset.name, annotateMarketPrice(expandedAsset.totals), averageText)
    }

    PieChart(
        data = data,
        defaultText = defaultText,
        textStyle = MaterialTheme.typography.titleLarge,
        selectedTitle = selectedChartTitle,
        onSelectedTitleChanged = onSelectedChartTitleChanged,
    )
}

private fun LazyListScope.assetItems(
    assets: List<InvestmentAsset>,
    expandedAssetNames: List<String>,
    onAssetClick: (String) -> Unit,
    onInvestmentClick: (Investment) -> Unit,
) {
    assets.forEachIndexed { index, asset ->
        item(key = asset.id) {
            AssetItem(
                asset = asset,
                colorIndex = index,
                onClick = { onAssetClick(asset.name) },
            )
        }

        val isExpanded = expandedAssetNames.contains(asset.name)

        if (isExpanded) {
            subInvestments(asset.investments, onInvestmentClick)
        }
    }
}

@Composable
private fun AssetItem(
    asset: InvestmentAsset,
    colorIndex: Int,
    onClick: () -> Unit,
) {
    ListItem(
        textLeft = asset.name,
        subTextLeft = asset.category,

        textRight = asset.shares.toString(),
        subTextRight = annotateFinancePnL(
            text = asset.formatMarketPrice(),
            pnl = asset.unrealisedPnL,
        ),

        colorIndex = colorIndex,

        onClick = onClick,
    )
}
