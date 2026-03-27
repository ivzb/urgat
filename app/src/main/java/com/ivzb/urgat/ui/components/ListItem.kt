package com.ivzb.urgat.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.theme.UrgatTheme
import com.ivzb.urgat.ui.theme.dataColor

@Composable
fun ListItem(
    textLeft: String,
    subTextLeft: String = "",

    textRight: String = "",
    subTextRight: AnnotatedString = AnnotatedString(""),
    subTextRightCanBeEmpty: Boolean = false,

    colorIndex: Int? = null,

    onClick: () -> Unit = {},
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        shadowElevation = 2.dp
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .clickable { onClick() },
        ) {

            colorIndex?.let {
                Box(
                    modifier = Modifier
                        .width(dimensionResource(R.dimen.expense_category_color_width))
                        .fillMaxHeight()
                        .background(color = dataColor(colorIndex))
                )
            }

            Row(
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.list_item_padding)),
                verticalAlignment = Alignment.CenterVertically
            ) {


                Column(
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

                Column(
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

                    if (subTextRight.text.isNotBlank() || subTextRightCanBeEmpty) {
                        Text(
                            text = subTextRight,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
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
            subTextRight = AnnotatedString("USD"),

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
            subTextRight = AnnotatedString("USD"),

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
            subTextRight = AnnotatedString(""),

            onClick = {},
        )
    }
}

@Preview
@DarkModePreview
@Composable
private fun ListItemWithColorPreview() {
    UrgatTheme {
        ListItem(
            textLeft = "NVDA",
            subTextLeft = "Stocks",

            textRight = "300",
            subTextRight = AnnotatedString("USD"),

            colorIndex = 0,

            onClick = {},
        )
    }
}