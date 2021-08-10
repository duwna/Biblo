package com.duwna.biblo.ui.group.chat

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.duwna.biblo.R
import kotlinx.android.synthetic.main.fragment_image_view.*

class ImageViewFragment : Fragment(R.layout.fragment_image_view) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(this).load(arguments?.getString("url")).into(iv_photo)
    }

    companion object {
        fun args(url: String?) = bundleOf("url" to url)
    }
}