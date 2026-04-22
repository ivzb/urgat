package com.ivzb.urgat.ui.trades

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.ivzb.urgat.R
import com.ivzb.urgat.data.trade.Trade
import com.ivzb.urgat.domain.trades.GroupedTrades
import com.ivzb.urgat.ui.components.ItemHeaderSmall
import com.ivzb.urgat.ui.components.ListItem
import com.ivzb.urgat.util.annotateFinance
import com.ivzb.urgat.util.isFirst
import com.ivzb.urgat.util.paddingIf

@Composable
fun TransactionsPage(
    trades: List<GroupedTrades>,
    onTradeClick: (Trade) -> Unit,

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

        trades.forEachIndexed { index, (group, total, trades) ->
            item {
                ItemHeaderSmall(
                    text = group,
                    modifier = Modifier
                        .paddingIf(
                            index.isFirst(),
                            bottom = dimensionResource(R.dimen.vertical_margin)
                        )
                        .paddingIf(
                            !index.isFirst(),
                            vertical = dimensionResource(R.dimen.vertical_margin)
                        )
                        .padding(horizontal = dimensionResource(R.dimen.list_item_padding))
                )
            }

            items(
                items = trades,
                key = { trade -> trade.id },
            ) { trade ->
                
                TradeItem(
                    trade = trade,
                    onTradeClick = onTradeClick
                )
            }
        }
    }
}

@Composable
fun TradeItem(
    trade: Trade,
    onTradeClick: (Trade) -> Unit
) {

    ListItem(
        textLeft = trade.name,
        subTextLeft = trade.orderType,

        textRight = trade.shares.toString(),
        subTextRight = annotateFinance(
            amount = trade.totalPrice,
            currency = trade.currency,
        ),

        onClick = { onTradeClick(trade) }
    )
}