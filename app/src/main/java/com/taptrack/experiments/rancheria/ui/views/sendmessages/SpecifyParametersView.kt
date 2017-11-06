package com.taptrack.experiments.rancheria.ui.views.sendmessages

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.taptrack.experiments.rancheria.business.CommandOption

public class SpecifyParametersView : FrameLayout {
    constructor(context: Context) : super(context) {
        initialize(context)
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initialize(context)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialize(context)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initialize(context)
    }

    fun initialize(context: Context) {

    }

    public fun setClass(option: CommandOption) {

    }
}