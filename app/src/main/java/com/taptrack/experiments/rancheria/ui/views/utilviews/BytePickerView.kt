package com.taptrack.experiments.rancheria.ui.views.utilviews

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.AttributeSet
import android.widget.NumberPicker
import com.taptrack.experiments.rancheria.R
import timber.log.Timber
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

class BytePickerView : NumberPicker {
    private var hasScannedValues = false

    constructor(context: Context) : super(context) {
        scanForValues(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        scanForValues(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        scanForValues(context, attrs)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        scanForValues(context, attrs)
    }

    private fun scanForValues(context: Context, attrs: AttributeSet?) {
        if (hasScannedValues) {
            return
        }

        descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        hasScannedValues = true

        wrapSelectorWheel = true
        setFormatter(HexFormatter())
        maxValue = 255
        minValue = 0

        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.BytePickerView, 0, 0)
        try {

            if (a.hasValue(R.styleable.BytePickerView_initialValue))
                value = a.getInt(R.styleable.BytePickerView_initialValue, 0)
            else
                value = 0

            if (a.hasValue(R.styleable.BytePickerView_disableDivider))
                disableDivider()
        } finally {
            a.recycle()
        }
    }

    private fun disableDivider() {
        val parentFields = NumberPicker::class.java.declaredFields
        for (f in parentFields) {
            if (f.name == "mSelectionDivider") {
                f.isAccessible = true
                try {
                    f.set(this, ColorDrawable(Color.TRANSPARENT))
                } catch (e: IllegalArgumentException) {
                    Timber.e(e, null)
                } catch (e: IllegalAccessException) {
                    Timber.e(e, null)
                }

                break
            }
        }
    }

    fun changeCurrentByOne(increment: Boolean) {
        val method: Method
        try {
            // refelction call for
            // higherPicker.changeValueByOne(true);
            method = this.javaClass.superclass!!.getDeclaredMethod("changeValueByOne", Boolean::class.javaPrimitiveType)
            method.isAccessible = true
            method.invoke(this, increment)
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }

    }

    private class HexFormatter : NumberPicker.Formatter {
        override fun format(value: Int): String {
            return String.format("%02X", value.toByte())
        }
    }
}
