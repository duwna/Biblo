package com.duwna.biblo.ui.bills

import com.duwna.biblo.ui.base.BaseViewModel
import com.duwna.biblo.ui.base.IViewModelState

class BillsViewModel: BaseViewModel<BillsState>(BillsState()) {

}

data class BillsState(
    val isLoading: Boolean = false
) : IViewModelState