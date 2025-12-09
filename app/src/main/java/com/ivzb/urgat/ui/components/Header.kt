package com.ivzb.urgat.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.library.LibraryHeaderItemId
import com.ivzb.urgat.ui.theme.UrgatTheme

@Composable
fun <T> Header(
    items: List<HeaderItem<T>>,
    onItemClick: (HeaderItem<T>) -> Unit,
    orientation: Int,
    modifier: Modifier = Modifier
) {

    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.library_header_item_padding)),
            modifier = modifier
                .fillMaxHeight()
                .clip(RoundedCornerShape(dimensionResource(R.dimen.menu_button_corner_radius)))
                .background(MaterialTheme.colorScheme.surface)
                .padding(dimensionResource(R.dimen.library_header_item_padding))
        ) {

            HeaderContent(
                items = items,
                onItemClick = onItemClick,
                modifier = Modifier
                    .defaultMinSize(minWidth = dimensionResource(R.dimen.library_header_item_min_width))
                    .weight(1f)
            )
        }
    } else {
        ElevatedCard(
            modifier = modifier
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.card_elevation)),
        ) {

            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.card_padding)),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.library_header_item_padding)),
                verticalAlignment = Alignment.CenterVertically,
            ) {

                HeaderContent(
                    items = items,
                    onItemClick = onItemClick,
                    modifier = Modifier
                        .weight(1f)
                )
            }
        }
    }
}

@Composable
private fun <T> HeaderContent(
    items: List<HeaderItem<T>>,
    onItemClick: (HeaderItem<T>) -> Unit,
    modifier: Modifier = Modifier
) {

    items.forEach {
        HeaderItem(
            item = it,
            onClick = onItemClick,
            modifier = modifier
        )
    }
}

@Composable
private fun <T> HeaderItem(
    item: HeaderItem<T>,
    onClick: (HeaderItem<T>) -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { onClick(item) },
        contentPadding = PaddingValues(
            horizontal = 0.dp,
            vertical = dimensionResource(R.dimen.menu_content_padding)
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary
        ),
        shape = RoundedCornerShape(dimensionResource(R.dimen.menu_button_corner_radius)),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                modifier = Modifier
                    .size(dimensionResource(R.dimen.menu_icon_size))
                    .padding(dimensionResource(R.dimen.menu_item_padding))
            )

            Text(
                text = stringResource(item.label),
                style = MaterialTheme.typography.labelMedium
                    .copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.menu_item_padding))
            )
        }
    }
}

@Preview
@DarkModePreview
@Composable
fun HeaderPreview() {
    UrgatTheme {
        Header(
            items = listOf(
                HeaderItem(
                    LibraryHeaderItemId.Todo,
                    Icons.Filled.Done,
                    R.string.add_todo
                ),
                HeaderItem(
                    LibraryHeaderItemId.Note,
                    Icons.Filled.Person,
                    R.string.add_note
                ),
            ),
            onItemClick = {},
            orientation = Configuration.ORIENTATION_PORTRAIT
        )
    }
}

@LandscapePreview
@DarkModeLandscapePreview
@Composable
fun HeaderLandscapePreview() {
    UrgatTheme {
        Header(
            items = listOf(
                HeaderItem(
                    LibraryHeaderItemId.Todo,
                    Icons.Filled.Done,
                    R.string.add_todo
                ),
                HeaderItem(
                    LibraryHeaderItemId.Note,
                    Icons.Filled.Person,
                    R.string.add_note
                ),
            ),
            onItemClick = {},
            orientation = Configuration.ORIENTATION_LANDSCAPE
        )
    }
}

@Preview
@DarkModePreview
@Composable
fun HeaderItemPreview() {
    UrgatTheme {
        HeaderItem(
            item = HeaderItem(
                LibraryHeaderItemId.Todo,
                Icons.Filled.Done,
                R.string.add_todo
            ),
            onClick = {},
        )
    }
}
