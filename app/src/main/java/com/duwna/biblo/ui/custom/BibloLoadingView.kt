package com.duwna.biblo.ui.custom

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.animation.doOnEnd
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import com.duwna.biblo.R

class BibloLoadingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    init {
        setImageResource(R.mipmap.ic_launcher_foreground)
        ObjectAnimator.ofFloat(this, "rotationY", 0.0f, 360f).apply {
            duration = 2000
            repeatCount = ObjectAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }

    }

    fun hide() {
        if (ViewCompat.isAttachedToWindow(this)) {
            ViewAnimationUtils.createCircularReveal(
                this,
                width / 2,
                height / 2,
                maxOf(width, height) / 2f,
                0f
            )
                .apply {
                    duration = 500
                    doOnEnd { isVisible = false }
                }.start()
        } else {
            isVisible = false
        }
    }

    fun show() {
        isVisible = true
    }
}