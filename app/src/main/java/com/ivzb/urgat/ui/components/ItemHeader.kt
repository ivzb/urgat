package com.ivzb.urgat.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.theme.UrgatTheme
import com.ivzb.urgat.util.format
import java.math.BigDecimal

@Composable
fun ItemHeaderLarge(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier
            .semantics { heading() },
        style = MaterialTheme.typography.titleLarge
    )
}

@Composable
fun ItemHeaderMedium(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier
            .semantics { heading() },
        style = MaterialTheme.typography.titleMedium
    )
}

@Composable
fun ItemHeaderSmall(
    text: String,
    color: Color = Color.Unspecified,
    fontWeight: FontWeight? = null,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier
            .semantics { heading() },
        style = MaterialTheme.typography.titleSmall,
        color = color,
        fontWeight = fontWeight,
    )
}

@Composable
fun ItemHeaderSmall(
    text: String,
    total: String,
    totalColor: Color = Color.Unspecified,
    totalFontWeight: FontWeight? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        ItemHeaderSmall(
            text = text,
        )

        ItemHeaderSmall(
            text = total,
            color = totalColor,
            fontWeight = totalFontWeight,
        )
    }
}

@Composable
fun ItemHeaderWithMore(
    title: String,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        ItemHeaderLarge(text = title)

        TextButton(
            onClick = onMoreClick
        ) {

            Text(
                text = stringResource(R.string.more)
            )
        }
    }
}

@Preview
@DarkModePreview
@Composable
private fun ItemHeaderLargePreview() {
    UrgatTheme {
        ItemHeaderLarge(
            text = "Home",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview
@DarkModePreview
@Composable
private fun ItemHeaderMediumPreview() {
    UrgatTheme {
        ItemHeaderMedium(
            text = "Home",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview
@DarkModePreview
@Composable
private fun ItemHeaderSmallPreview() {
    UrgatTheme {
        ItemHeaderSmall(
            text = "Home",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview
@DarkModePreview
@Composable
private fun ItemHeaderWithMorePreview() {
    UrgatTheme {
        ItemHeaderWithMore(
            title = "Home",
            onMoreClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
