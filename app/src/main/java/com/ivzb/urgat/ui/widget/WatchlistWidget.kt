package com.ivzb.urgat.ui.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
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
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.ivzb.urgat.MainActivity
import com.ivzb.urgat.R
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.data.watchlist.WatchlistAsset
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.financeColor
import java.math.BigDecimal

/**
 * Home screen app widget showing the very same assets as the finance watchlist.
 */
class WatchlistWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode = SizeMode.Responsive(
        setOf(SMALL_SIZE, MEDIUM_SIZE, LARGE_SIZE)
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val watchlistFlow = context.watchlistWidgetEntryPoint()
            .observeWatchlistAssets()

        provideContent {
            val state by watchlistFlow.collectAsState(initial = Async.Loading)

            UrgatGlanceTheme {
                WatchlistWidgetContent(state)
            }
        }
    }

    @Composable
    private fun WatchlistWidgetContent(state: Async<List<WatchlistAsset>>) {
        val context = LocalContext.current
        val size = LocalSize.current

        val isCompact = size.width < MEDIUM_SIZE.width

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
                        text = context.getString(R.string.watchlist),
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
                        title = context.getString(R.string.loading),
                        message = null,
                    )

                    is Async.Error -> Message(
                        title = context.getString(R.string.watchlist_widget_error),
                        message = context.getString(R.string.watchlist_widget_error_message).takeUnless { isCompact },
                    )

                    is Async.Success -> {
                        val assets = state.data

                        if (assets.isEmpty()) {
                            Message(
                                title = context.getString(R.string.watchlist_widget_empty),
                                message = context.getString(R.string.watchlist_widget_empty_message).takeUnless { isCompact },
                            )
                        } else {
                            WatchlistAssets(
                                assets = assets,
                                isCompact = isCompact,
                            )
                        }
                    }
                }
            }

            if (showRefresh) {
                // laid over the content, so the tap area can spread into the widget padding
                Box(
                    modifier = GlanceModifier.fillMaxSize(),
                    contentAlignment = Alignment.TopEnd,
                ) {
                    RefreshButton(
                        modifier = GlanceModifier.size(REFRESH_TAP_TARGET),
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
                        modifier = GlanceModifier.size(32.dp),
                        color = GlanceTheme.colors.primary,
                    )
                }
            }
        }
    }

    /**
     * The whole watchlist is listed, however tall the widget is - the leftovers are scrolled to.
     */
    @Composable
    private fun WatchlistAssets(
        assets: List<WatchlistAsset>,
        isCompact: Boolean,
    ) {
        LazyColumn(
            modifier = GlanceModifier.fillMaxSize(),
        ) {
            items(
                items = assets,
                itemId = { asset -> asset.id.hashCode().toLong() },
            ) { asset ->

                WatchlistAssetItem(
                    asset = asset,
                    isCompact = isCompact,
                )
            }
        }
    }

    @Composable
    private fun WatchlistAssetItem(
        asset: WatchlistAsset,
        isCompact: Boolean,
    ) {
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                // a click anywhere but on a row is caught by the widget itself
                .clickable(actionStartActivity<MainActivity>()),
            verticalAlignment = Alignment.Vertical.CenterVertically,
        ) {

            Column(
                modifier = GlanceModifier.defaultWeight(),
            ) {

                Text(
                    text = asset.name.uppercase(),
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                    maxLines = 1,
                )

                val description = asset.description

                if (!isCompact && !description.isNullOrBlank()) {
                    Text(
                        text = description,
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurfaceVariant,
                            fontSize = 11.sp,
                        ),
                        maxLines = 1,
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.Horizontal.End,
            ) {

                Text(
                    text = asset.formatMarketPrice(),
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                    maxLines = 1,
                )

                Text(
                    text = asset.formatPriceChange(),
                    style = TextStyle(
                        color = priceChangeColor(asset.priceChange),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                    maxLines = 1,
                )
            }
        }
    }

    /** The app's green/red, falling back to the widget theme when there is nothing to compare to. */
    @Composable
    private fun priceChangeColor(priceChange: BigDecimal?): ColorProvider {
        val color = financeColor(priceChange)

        return if (color == Color.Unspecified) {
            GlanceTheme.colors.onSurfaceVariant
        } else {
            ColorProvider(color)
        }
    }

    @Composable
    private fun RefreshButton(modifier: GlanceModifier) {
        val context = LocalContext.current

        Box(
            modifier = modifier
                .clickable(actionRunCallback<RefreshWatchlistAction>()),
            contentAlignment = Alignment.Center,
        ) {

            Image(
                provider = ImageProvider(R.drawable.ic_refresh),
                contentDescription = context.getString(R.string.watchlist_widget_refresh),
                colorFilter = ColorFilter.tint(GlanceTheme.colors.onSurfaceVariant),
                modifier = GlanceModifier.size(REFRESH_ICON_SIZE),
            )
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

        /** Only the symbol and its price fit. */
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
        internal val REFRESH_STARTED_AT = longPreferencesKey("watchlist_widget_refresh_started_at")

        /** How long the refresh waits for fresh quotes before giving up. */
        internal const val REFRESH_TIMEOUT_MILLIS = 5_000L

        /** After this long an unfinished refresh is considered abandoned. */
        private const val REFRESH_STALE_AFTER_MILLIS = 15_000L

    }

    @Composable
    private fun WatchlistWidgetPreview(size: DpSize, state: Async<List<WatchlistAsset>>) {
        CompositionLocalProvider(
            LocalSize provides size,
            // the preview host leaves the widget state out, which currentState() cannot cope with
            LocalState provides emptyPreferences(),
        ) {
            UrgatGlanceTheme {
                WatchlistWidgetContent(state)
            }
        }
    }

    @OptIn(ExperimentalGlancePreviewApi::class)
    @Preview(widthDp = 140, heightDp = 110)
    @Composable
    private fun WatchlistWidgetSmallPreview() {
        WatchlistWidgetPreview(SMALL_SIZE, Async.Success(SampleData.SAMPLE_WATCHLIST_ASSETS))
    }

    @OptIn(ExperimentalGlancePreviewApi::class)
    @Preview(widthDp = 220, heightDp = 110)
    @Composable
    private fun WatchlistWidgetMediumPreview() {
        WatchlistWidgetPreview(MEDIUM_SIZE, Async.Success(SampleData.SAMPLE_WATCHLIST_ASSETS))
    }

    @OptIn(ExperimentalGlancePreviewApi::class)
    @Preview(widthDp = 220, heightDp = 220)
    @Composable
    private fun WatchlistWidgetLargePreview() {
        WatchlistWidgetPreview(LARGE_SIZE, Async.Success(SampleData.SAMPLE_WATCHLIST_ASSETS))
    }

    @OptIn(ExperimentalGlancePreviewApi::class)
    @Preview(widthDp = 220, heightDp = 220)
    @Composable
    private fun WatchlistWidgetEmptyPreview() {
        WatchlistWidgetPreview(LARGE_SIZE, Async.Success(listOf()))
    }

    @OptIn(ExperimentalGlancePreviewApi::class)
    @Preview(widthDp = 220, heightDp = 220)
    @Composable
    private fun WatchlistWidgetLoadingPreview() {
        WatchlistWidgetPreview(LARGE_SIZE, Async.Loading)
    }

    @OptIn(ExperimentalGlancePreviewApi::class)
    @Preview(widthDp = 220, heightDp = 220)
    @Composable
    private fun WatchlistWidgetErrorPreview() {
        WatchlistWidgetPreview(LARGE_SIZE, Async.Error)
    }

}

