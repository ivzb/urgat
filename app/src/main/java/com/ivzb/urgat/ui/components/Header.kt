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
fun Header(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        modifier = modifier
            .semantics { heading() },
        style = MaterialTheme.typography.headlineSmall
    )
}

@Composable
fun SeeAllHeader(
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

        Header(title = title)

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
private fun HeaderPreview() {
    UrgatTheme {
        Header(
            title = "Home",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview
@Composable
private fun SeeAllHeaderPreview() {
    UrgatTheme {
        SeeAllHeader(
            title = "Home",
            onSeeAllClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
