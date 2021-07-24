package com.duwna.biblo.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.duwna.biblo.R
import com.duwna.biblo.utils.attrValue
import com.duwna.biblo.utils.dpToIntPx
import com.duwna.biblo.utils.dpToPx
import com.duwna.biblo.utils.setMarginOptionally

class MemberView private constructor(
    context: Context, attributes: AttributeSet?
) : LinearLayout(context, attributes) {


    constructor(
        context: Context,
        name: String,
        avatarUrl: String?,
        attributes: AttributeSet? = null
    ) : this(context, attributes) {

        orientation = HORIZONTAL
        gravity = Gravity.CENTER
        setBackgroundResource(R.drawable.background_member_view)

        val padding = context.dpToIntPx(8)
        setPadding(
            if (avatarUrl == null) padding else 0,
            0,
            padding,
            0
        )

        val margin = context.dpToIntPx(8)
        val height = context.dpToIntPx(40)

        val newLayoutParams =
            LayoutParams(LayoutParams.WRAP_CONTENT, height).apply {
                setMargins(0, 0, margin, margin)
            }
        layoutParams = newLayoutParams

        addViews(avatarUrl, context, name)
    }

    private fun addViews(avatarUrl: String?, context: Context, name: String) {
        if (avatarUrl != null) {
            val imageView = AvatarImageView(context).apply {

                val size = context.dpToIntPx(40)
                layoutParams = LayoutParams(size, size)

                borderWidth = context.dpToPx(1)
                borderColor = context.attrValue(R.attr.colorOnSurface)

                setMarginOptionally(right = context.dpToIntPx(8))

                isAvatarMode = true
                Glide.with(this).load(avatarUrl).into(this)
            }

            addView(imageView)
        }

        val textView = TextView(context).apply {
            text = name
            setTextColor(context.attrValue(R.attr.colorOnSurface))
            textSize = 14f
        }

        addView(textView)
    }

}