package com.duwna.biblo.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton


class FabBehavior(context: Context, attributeSet: AttributeSet) :
    CoordinatorLayout.Behavior<ExtendedFloatingActionButton>(context, attributeSet) {

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: ExtendedFloatingActionButton,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean = axes == ViewCompat.SCROLL_AXIS_VERTICAL

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: ExtendedFloatingActionButton,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        if (dy > 0) child.shrink() else child.extend()
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
    }
}