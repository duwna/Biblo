package com.duwna.biblo.ui.group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.duwna.biblo.MainActivity
import com.duwna.biblo.R
import com.duwna.biblo.entities.items.GroupItem
import com.duwna.biblo.ui.group.bills.BillsFragment
import com.duwna.biblo.ui.group.chat.ChatFragment
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_group.*

class GroupFragment : Fragment() {

    private val args: GroupFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_group, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pagerAdapter = PagerAdapter(this, args.groupItem)
        view_pager.adapter = pagerAdapter

        TabLayoutMediator(tab_layout, view_pager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.title_bills)
                else -> getString(R.string.title_chat)
            }
        }.attach()
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).toolbar.title = args.groupItem.name

    }
}

class PagerAdapter(
    fragment: Fragment, private val groupItem: GroupItem
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> BillsFragment.newInstance(groupItem)
            else -> ChatFragment.newInstance(groupItem)
        }
    }
}