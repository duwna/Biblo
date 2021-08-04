package com.duwna.biblo.ui.group

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.duwna.biblo.MainActivity
import com.duwna.biblo.R
import com.duwna.biblo.entities.items.GroupItem
import com.duwna.biblo.ui.dialogs.ImageActionDialog
import com.duwna.biblo.ui.group.bills.BillsFragment
import com.duwna.biblo.ui.group.chat.ChatFragment
import com.duwna.biblo.utils.tryOrNull
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_group.*
import java.lang.IllegalArgumentException

class GroupFragment : Fragment() {

    private val args: GroupFragmentArgs by navArgs()
    lateinit var pagerAdapter: PagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_group, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pagerAdapter = PagerAdapter(this, args.groupItem)
        view_pager.adapter = pagerAdapter

        TabLayoutMediator(tab_layout, view_pager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.title_bills)
                else -> getString(R.string.title_chat)
            }
        }.attach()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener(ImageActionDialog.IMAGE_ACTIONS_KEY) { _, bundle ->
            val result = bundle[ImageActionDialog.SELECT_ACTION_KEY] as? String
            if (result == ImageActionDialog.DELETE_ACTION_KEY) pagerAdapter.chatFragment.setImageUri(null)
            else pagerAdapter.chatFragment.setImageUri(tryOrNull { Uri.parse(result) })
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        (activity as MainActivity).toolbar.title = args.groupItem.name
    }
}

class PagerAdapter(
    fragment: Fragment, private val groupItem: GroupItem
) : FragmentStateAdapter(fragment) {

    val chatFragment: ChatFragment by lazy { ChatFragment.newInstance(groupItem) }
    val billsFragment: BillsFragment by lazy { BillsFragment.newInstance(groupItem) }

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> billsFragment
            1 -> chatFragment
            else -> throw IllegalArgumentException("No screen on position $position")
        }
    }
}