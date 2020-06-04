package com.duwna.biblo.ui.custom

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.duwna.biblo.R
import com.duwna.biblo.utils.*

class MemberView private constructor(
    context: Context, attributes: AttributeSet?
) : LinearLayout(context, attributes) {

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER
        setBackgroundResource(R.drawable.background_member_view)

        val padding = context.dpToIntPx(8)
        setPadding(padding, padding, padding, padding)

        val margin = context.dpToIntPx(4)
        val newLayoutParams =
            LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                setMargins(margin, margin, margin, margin)
            }
        layoutParams = newLayoutParams
    }

    constructor(
        context: Context,
        name: String,
        avatarUrl: String?,
        attributes: AttributeSet? = null
    ) : this(context, attributes) {

        val textView = TextView(context).apply {
            text = name
            setTextColor(context.attrValue(R.attr.colorOnSurface))
            textSize = 16f
        }

        val imageView = AvatarImageView(context).apply {

            val size = context.dpToIntPx(30)
            layoutParams = LayoutParams(size, size)

            borderWidth = context.dpToPx(1)
            borderColor = context.attrValue(R.attr.colorOnSurface)

            setMarginOptionally(right = context.dpToIntPx(4))

            isAvatarMode = true
            if (avatarUrl != null) Glide.with(this).load(avatarUrl).into(this)
            else setImageResource(R.drawable.ic_baseline_account_circle_24)
        }

        addView(imageView)
        addView(textView)
    }

}