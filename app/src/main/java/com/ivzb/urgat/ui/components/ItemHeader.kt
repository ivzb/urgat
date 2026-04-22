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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextAlign.Companion
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.theme.UrgatTheme

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
    text: AnnotatedString,
    textAlign: TextAlign? = null,
    modifier: Modifier = Modifier,
) {

    Text(
        text = text,
        textAlign = textAlign ?: TextAlign.Unspecified,
        modifier = modifier
            .semantics { heading() },
        style = MaterialTheme.typography.titleSmall,
    )
}

@Composable
fun ItemHeaderSmall(
    text: String,
    total: AnnotatedString = AnnotatedString(""),
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        ItemHeaderSmall(
            text = AnnotatedString(text),
        )

        ItemHeaderSmall(
            text = total,
            textAlign = TextAlign.End,
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
            text = AnnotatedString("Home"),
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
