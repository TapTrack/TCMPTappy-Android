package com.taptrack.experiments.rancheria.ui.views.utilviews

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

class UnswipableViewPager : ViewPager {
    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }
}
