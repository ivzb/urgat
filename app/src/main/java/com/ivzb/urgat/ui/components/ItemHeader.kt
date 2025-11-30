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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.theme.UrgatTheme

@Composable
fun ItemHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        modifier = modifier
            .semantics { heading() },
        style = MaterialTheme.typography.titleLarge
    )
}

@Composable
fun ItemHeaderWithSeeAll(
    title: String,
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        ItemHeader(title = title)

        TextButton(
            onClick = onSeeAllClick
        ) {

            Text(
                text = stringResource(R.string.see_all)
            )
        }
    }
}

@Preview
@Composable
private fun ItemHeaderPreview() {
    UrgatTheme {
        ItemHeader(
            title = "Home",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview
@Composable
private fun ItemHeaderWithSeeAllPreview() {
    UrgatTheme {
        ItemHeaderWithSeeAll(
            title = "Home",
            onSeeAllClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
