package com.duwna.biblo.ui.groups.add.members

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.duwna.biblo.R

class AddMembersFragment : Fragment() {

    companion object {
        fun newInstance() = AddMembersFragment()
    }

    private lateinit var viewModel: AddMembersViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_members_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AddMembersViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
