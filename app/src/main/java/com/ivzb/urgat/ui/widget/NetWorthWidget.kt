package com.ivzb.urgat.ui.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.LocalState
import androidx.glance.action.clickable
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.ivzb.urgat.MainActivity
import com.ivzb.urgat.R
import com.ivzb.urgat.data.fire.NetWorth
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.format
import com.ivzb.urgat.util.formatCurrency

/**
 * Home screen app widget showing the accumulated stash (net worth) coming from the FI/RE data.
 */
class NetWorthWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode = SizeMode.Responsive(
        setOf(ROW_SIZE, WIDE_ROW_SIZE, SMALL_SIZE, MEDIUM_SIZE, LARGE_SIZE)
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val netWorthFlow = context.netWorthWidgetEntryPoint()
            .observeNetWorthUseCase()
            .invoke()

        provideContent {
            val state by netWorthFlow.collectAsState(initial = Async.Loading)

            UrgatGlanceTheme {
                NetWorthWidgetContent(state)
            }
        }
    }

    @Composable
    private fun NetWorthWidgetContent(state: Async<NetWorth?>) {
        val size = LocalSize.current

        val isRow = size.height < SMALL_SIZE.height
        val isCompact = size.width < MEDIUM_SIZE.width
        val showBreakdown = size.height >= LARGE_SIZE.height

        val refreshStartedAt = currentState(REFRESH_STARTED_AT) ?: 0L
        val isRefreshing = refreshStartedAt > 0L &&
                System.currentTimeMillis() - refreshStartedAt < REFRESH_STALE_AFTER_MILLIS

        val showRefresh = !isCompact && !isRefreshing

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .appWidgetBackground()
                .background(ImageProvider(R.drawable.widget_background))
                .clickable(actionStartActivity<MainActivity>()),
            contentAlignment = Alignment.Center,
        ) {

            if (isRow) {
                SingleRowContent(
                    state = state,
                    isCompact = isCompact,
                    showRefresh = showRefresh,
                )
            } else {
                StackedContent(
                    state = state,
                    isCompact = isCompact,
                    showBreakdown = showBreakdown,
                    showRefresh = showRefresh,
                )
            }

            if (showRefresh) {
                // laid over the content, so the tap area can spread into the widget padding
                Box(
                    modifier = GlanceModifier.fillMaxSize(),
                    contentAlignment = if (isRow) Alignment.CenterEnd else Alignment.TopEnd,
                ) {
                    RefreshButton(
                        modifier = if (isRow) {
                            GlanceModifier
                                .fillMaxHeight()
                                .width(REFRESH_TAP_TARGET)
                        } else {
                            GlanceModifier.size(REFRESH_TAP_TARGET)
                        },
                    )
                }
            }

            if (isRefreshing) {
                Box(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(ImageProvider(R.drawable.widget_scrim)),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        modifier = GlanceModifier.size(if (isRow) 20.dp else 32.dp),
                        color = GlanceTheme.colors.primary,
                    )
                }
            }
        }
    }

    /**
     * A single home screen row is only tall enough for one line, so the label moves next to the
     * amount and the breakdown is dropped entirely.
     */
    @Composable
    private fun SingleRowContent(
        state: Async<NetWorth?>,
        isCompact: Boolean,
        showRefresh: Boolean,
    ) {
        val context = LocalContext.current

        Row(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(
                    start = 16.dp,
                    // room for the refresh button laid over the row
                    end = if (showRefresh) REFRESH_TAP_TARGET else 16.dp,
                    top = 8.dp,
                    bottom = 8.dp,
                ),
            verticalAlignment = Alignment.Vertical.CenterVertically,
        ) {

            if (!isCompact) {
                Text(
                    text = context.getString(R.string.net_worth),
                    modifier = GlanceModifier.defaultWeight(),
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurfaceVariant,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                    maxLines = 1,
                )
            }

            Text(
                text = headline(state),
                // without a label there is nothing else to give the leftover space to
                modifier = if (isCompact) GlanceModifier.defaultWeight() else GlanceModifier,
                style = TextStyle(
                    color = GlanceTheme.colors.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                ),
                maxLines = 1,
            )
        }
    }

    @Composable
    private fun StackedContent(
        state: Async<NetWorth?>,
        isCompact: Boolean,
        showBreakdown: Boolean,
        showRefresh: Boolean,
    ) {
        val context = LocalContext.current

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(16.dp),
        ) {

            Row(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    // room for the refresh button laid over the header
                    .padding(end = if (showRefresh) REFRESH_TAP_TARGET else 0.dp),
                verticalAlignment = Alignment.Vertical.CenterVertically,
            ) {

                Text(
                    text = context.getString(R.string.net_worth),
                    modifier = GlanceModifier.defaultWeight(),
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurfaceVariant,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                    maxLines = 1,
                )
            }

            Spacer(modifier = GlanceModifier.height(6.dp))

            when (state) {
                is Async.Loading -> Message(
                    title = headline(state),
                    message = null,
                )

                is Async.Error -> Message(
                    title = headline(state),
                    message = context.getString(R.string.net_worth_widget_error_message).takeUnless { isCompact },
                )

                is Async.Success -> {
                    val netWorth = state.data

                    if (netWorth != null) {
                        NetWorthAmount(
                            netWorth = netWorth,
                            isCompact = isCompact,
                            showBreakdown = showBreakdown,
                        )
                    } else {
                        Message(
                            title = headline(state),
                            message = context.getString(R.string.net_worth_widget_empty_message).takeUnless { isCompact },
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun RefreshButton(modifier: GlanceModifier) {
        val context = LocalContext.current

        Box(
            modifier = modifier
                .clickable(actionRunCallback<RefreshNetWorthAction>()),
            contentAlignment = Alignment.Center,
        ) {

            Image(
                provider = ImageProvider(R.drawable.ic_refresh),
                contentDescription = context.getString(R.string.net_worth_widget_refresh),
                colorFilter = ColorFilter.tint(GlanceTheme.colors.onSurfaceVariant),
                modifier = GlanceModifier.size(REFRESH_ICON_SIZE),
            )
        }
    }

    /** The one line every size has room for, whatever the state is. */
    @Composable
    private fun headline(state: Async<NetWorth?>): String {
        val context = LocalContext.current

        return when (state) {
            is Async.Loading -> context.getString(R.string.loading)
            is Async.Error -> context.getString(R.string.net_worth_widget_error)
            is Async.Success -> state.data?.formatted() ?: context.getString(R.string.net_worth_widget_empty)
        }
    }

    @Composable
    private fun NetWorthAmount(
        netWorth: NetWorth,
        isCompact: Boolean,
        showBreakdown: Boolean,
    ) {
        Text(
            text = netWorth.formatted(),
            style = TextStyle(
                color = GlanceTheme.colors.onSurface,
                fontSize = if (isCompact) 18.sp else 24.sp,
                fontWeight = FontWeight.Bold,
            ),
            maxLines = 1,
        )

        if (!showBreakdown || netWorth.breakdown.isEmpty()) {
            return
        }

        val currency = netWorth.currency.formatCurrency()

        Spacer(modifier = GlanceModifier.height(10.dp))

        netWorth.breakdown.take(MAX_BREAKDOWN_ITEMS).forEach { item ->
            Row(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp),
                verticalAlignment = Alignment.Vertical.CenterVertically,
            ) {

                Text(
                    text = item.name,
                    modifier = GlanceModifier.defaultWeight(),
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurfaceVariant,
                        fontSize = 12.sp,
                    ),
                    maxLines = 1,
                )

                Text(
                    text = "${item.amount.format()} $currency",
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                    maxLines = 1,
                )
            }
        }
    }

    @Composable
    private fun Message(title: String, message: String?) {
        Text(
            text = title,
            style = TextStyle(
                color = GlanceTheme.colors.onSurface,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
            ),
            maxLines = 2,
        )

        if (message == null) {
            return
        }

        Spacer(modifier = GlanceModifier.height(4.dp))

        Text(
            text = message,
            style = TextStyle(
                color = GlanceTheme.colors.onSurfaceVariant,
                fontSize = 12.sp,
            ),
            maxLines = 3,
        )
    }

    companion object {

        /** A single home screen row - only the amount fits. */
        internal val ROW_SIZE = DpSize(140.dp, 40.dp)

        /** A single home screen row, wide enough to also label the amount. */
        internal val WIDE_ROW_SIZE = DpSize(220.dp, 40.dp)

        internal val SMALL_SIZE = DpSize(140.dp, 110.dp)

        internal val MEDIUM_SIZE = DpSize(220.dp, 110.dp)

        internal val LARGE_SIZE = DpSize(220.dp, 220.dp)

        private val REFRESH_ICON_SIZE = 18.dp

        private val REFRESH_TAP_TARGET = 48.dp

        /**
         * When the ongoing refresh started, `0` when there is none.
         *
         * A timestamp instead of a flag, so a refresh interrupted by process death can never
         * leave the widget spinning forever.
         */
        internal val REFRESH_STARTED_AT = longPreferencesKey("net_worth_widget_refresh_started_at")

        /** How long the refresh waits for fresh quotes before giving up. */
        internal const val REFRESH_TIMEOUT_MILLIS = 5_000L

        /** After this long an unfinished refresh is considered abandoned. */
        private const val REFRESH_STALE_AFTER_MILLIS = 15_000L

        private const val MAX_BREAKDOWN_ITEMS = 5

    }

    @Composable
    private fun NetWorthWidgetPreview(size: DpSize, state: Async<NetWorth?>) {
        CompositionLocalProvider(
            LocalSize provides size,
            // the preview host leaves the widget state out, which currentState() cannot cope with
            LocalState provides emptyPreferences(),
        ) {
            UrgatGlanceTheme {
                NetWorthWidgetContent(state)
            }
        }
    }

    @OptIn(ExperimentalGlancePreviewApi::class)
    @Preview(widthDp = 140, heightDp = 40)
    @Composable
    private fun NetWorthWidgetRowPreview() {
        NetWorthWidgetPreview(ROW_SIZE, Async.Success(SampleData.SAMPLE_NET_WORTH))
    }

    @OptIn(ExperimentalGlancePreviewApi::class)
    @Preview(widthDp = 220, heightDp = 40)
    @Composable
    private fun NetWorthWidgetWideRowPreview() {
        NetWorthWidgetPreview(WIDE_ROW_SIZE, Async.Success(SampleData.SAMPLE_NET_WORTH))
    }

    @OptIn(ExperimentalGlancePreviewApi::class)
    @Preview(widthDp = 140, heightDp = 110)
    @Composable
    private fun NetWorthWidgetSmallPreview() {
        NetWorthWidgetPreview(SMALL_SIZE, Async.Success(SampleData.SAMPLE_NET_WORTH))
    }

    @OptIn(ExperimentalGlancePreviewApi::class)
    @Preview(widthDp = 220, heightDp = 110)
    @Composable
    private fun NetWorthWidgetMediumPreview() {
        NetWorthWidgetPreview(MEDIUM_SIZE, Async.Success(SampleData.SAMPLE_NET_WORTH))
    }

    @OptIn(ExperimentalGlancePreviewApi::class)
    @Preview(widthDp = 220, heightDp = 220)
    @Composable
    private fun NetWorthWidgetLargePreview() {
        NetWorthWidgetPreview(LARGE_SIZE, Async.Success(SampleData.SAMPLE_NET_WORTH))
    }

    @OptIn(ExperimentalGlancePreviewApi::class)
    @Preview(widthDp = 220, heightDp = 220)
    @Composable
    private fun NetWorthWidgetEmptyPreview() {
        NetWorthWidgetPreview(LARGE_SIZE, Async.Success(null))
    }

    @OptIn(ExperimentalGlancePreviewApi::class)
    @Preview(widthDp = 220, heightDp = 220)
    @Composable
    private fun NetWorthWidgetLoadingPreview() {
        NetWorthWidgetPreview(LARGE_SIZE, Async.Loading)
    }

    @OptIn(ExperimentalGlancePreviewApi::class)
    @Preview(widthDp = 220, heightDp = 220)
    @Composable
    private fun NetWorthWidgetErrorPreview() {
        NetWorthWidgetPreview(LARGE_SIZE, Async.Error)
    }

}

private fun NetWorth.formatted(): String = "${amount.format()} ${currency.formatCurrency()}"
