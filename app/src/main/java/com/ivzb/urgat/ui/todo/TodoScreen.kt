package com.ivzb.urgat.ui.todo

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun TodoScreen(
    onAddTodo: () -> Unit,
    onClickTodo: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TodoViewModel = hiltViewModel()
) {

    Text("todo screen")
}
