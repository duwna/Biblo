package com.duwna.biblo.ui.bills

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.duwna.biblo.ui.base.BaseFragment
import com.duwna.biblo.ui.base.IViewModelState
import com.duwna.biblo.R
import com.duwna.biblo.utils.log

class BillsFragment : BaseFragment<BillsViewModel>() {

    private val args: BillsFragmentArgs by navArgs()
    override val viewModel: BillsViewModel by viewModels()

    override val layout: Int = R.layout.fragment_bills

    override fun setupViews() {
        log(args)
    }

    override fun bindState(state: IViewModelState) {

    }

}