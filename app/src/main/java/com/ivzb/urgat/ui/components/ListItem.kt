package com.ivzb.urgat.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.theme.UrgatTheme

@Composable
fun ListItem(
    textLeft: String,
    subTextLeft: String,

    textRight: String,
    subTextRight: String,
    subTextRightFontWeight: FontWeight? = null,
    subTextRightColor: Color = Color.Unspecified,

    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(dimensionResource(R.dimen.list_item_padding)),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column (
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
            ) {

                if (textLeft.isNotBlank()) {
                    Text(
                        text = textLeft,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }

                if (subTextLeft.isNotBlank()) {
                    Text(
                        text = subTextLeft,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column (
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .weight(1f)
            ) {
                if (textRight.isNotBlank()) {
                    Text(
                        text = textRight,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }

                if (subTextRight.isNotBlank()) {
                    Text(
                        text = subTextRight,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = subTextRightFontWeight,
                        color = subTextRightColor,
                    )
                }
            }
        }
    }
}

@Preview
@DarkModePreview
@Composable
private fun ListItemPreview() {
    UrgatTheme {
        ListItem(
            textLeft = "NVDA",
            subTextLeft = "Stocks",

            textRight = "300",
            subTextRight = "USD",

            onClick = {},
        )
    }
}

@Preview
@DarkModePreview
@Composable
private fun ListItemSingleLeftText() {
    UrgatTheme {
        ListItem(
            textLeft = "NVDA",
            subTextLeft = "",

            textRight = "300",
            subTextRight = "USD",

            onClick = {},
        )
    }
}

@Preview
@DarkModePreview
@Composable
private fun ListItemSingleRightText() {
    UrgatTheme {
        ListItem(
            textLeft = "NVDA",
            subTextLeft = "Stocks",

            textRight = "300",
            subTextRight = "",

            onClick = {},
        )
    }
}
