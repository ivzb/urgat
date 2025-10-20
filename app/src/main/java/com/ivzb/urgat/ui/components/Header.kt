package com.ivzb.urgat.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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

@Preview
@LandscapePreview
@Composable
private fun HeaderPreview() {
    UrgatTheme {
        Header(
            title = "Home",
            modifier = Modifier.padding(16.dp)
        )
    }
}