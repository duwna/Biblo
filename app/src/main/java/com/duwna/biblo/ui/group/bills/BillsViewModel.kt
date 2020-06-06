package com.duwna.biblo.ui.group.bills

import androidx.lifecycle.viewModelScope
import com.duwna.biblo.ui.base.BaseViewModel
import com.duwna.biblo.ui.base.IViewModelState
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BillsViewModel: BaseViewModel<BillsState>(BillsState()) {

    init {
        viewModelScope.launch(IO) {
            delay(3000)
            postUpdateState { copy(isLoading = false) }
        }
    }
}

data class BillsState(
    val isLoading: Boolean = true,
    val bills: List<String> = emptyList()
) : IViewModelState