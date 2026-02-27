package com.ivzb.urgat.domain.note

import com.ivzb.urgat.data.note.Note
import com.ivzb.urgat.domain.expense.ObserveExpensesUiStateUseCase
import com.ivzb.urgat.ui.expenses.ExpensesFilterType
import com.ivzb.urgat.ui.expenses.ExpensesSortType
import com.ivzb.urgat.ui.expenses.ExpensesUiState
import com.ivzb.urgat.ui.investments.InvestmentsSortType
import com.ivzb.urgat.ui.notes.NotesUiState
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class FakeObserveNotesUseCase : ObserveNotesUseCase {

    private val flow = MutableStateFlow<Async<List<Note>>>(Async.Loading)

    var lastQuery: String = ""

    fun emit(value: Async<List<Note>>) {
        flow.value = value
    }

    override operator fun invoke(
        searchQueryFlow: Flow<String>?,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<Unit>?,
        sortTypeFlow: Flow<Unit>?,
        limitFlow: Flow<Int>?
    ): Flow<Async<List<Note>>> {
        // Record query changes for assertions
        CoroutineScope(Dispatchers.Unconfined).launch {
            searchQueryFlow?.collect { lastQuery = it }
        }
        return flow
    }

}

class FakeGetNoteUseCase : GetNoteUseCase {

    var toReturn: Note? = null
    var calledWithId: String? = null

    override suspend fun invoke(id: String): Note? {
        calledWithId = id
        return toReturn
    }
}

class FakeCreateNoteUseCase : CreateNoteUseCase {

    var created: String? = null

    override suspend fun invoke(content: String) {
        created = content
    }
}

class FakeUpdateNoteUseCase : UpdateNoteUseCase {

    var updated: List<Any> = listOf()

    override suspend fun invoke(id: String, content: String) {
        updated = listOf(id, content)
    }
}

class FakeDeleteNoteUseCase : DeleteNoteUseCase {

    var deleted: List<Any> = listOf()

    override suspend fun invoke(
        id: String,
    ) {
        deleted = listOf(id)
    }
}

class FakeObserveNotesUiStateUseCase: ObserveNotesUiStateUseCase {

    private val flow = MutableStateFlow<Async<NotesUiState>>(Async.Loading)

    var lastQuery: String = ""

    fun emit(value: Async<NotesUiState>) {
        flow.value = value
    }

    override fun invoke(
        searchQueryFlow: Flow<String>,
    ): Flow<Async<NotesUiState>> {
        // Record query changes for assertions
        CoroutineScope(Dispatchers.Unconfined).launch {
            searchQueryFlow?.collect { lastQuery = it }
        }
        return flow
    }

}