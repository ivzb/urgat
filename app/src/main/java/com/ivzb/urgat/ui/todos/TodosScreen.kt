package com.ivzb.urgat.ui.todos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ivzb.urgat.R
import com.ivzb.urgat.data.todo.Todo

@Composable
fun TodoItem(
    todo: Todo,
    onCheckedChange: (Boolean) -> Unit,
    onTodoClick: (Todo) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        shadowElevation = 2.dp
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .clickable { onTodoClick(todo) }
                .padding(dimensionResource(id = R.dimen.list_item_padding))
        ) {

            Checkbox(
                checked = todo.isCompleted,
                onCheckedChange = onCheckedChange,
                modifier = Modifier
                    .align(Alignment.Top)
            )

            Text(
                text = todo.content,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis,
                textDecoration = if (todo.isCompleted) {
                    TextDecoration.LineThrough
                } else {
                    null
                }
            )
        }
    }
}
