package com.taptrack.experiments.rancheria.ui.views.sendmessages;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.NumberPicker;

import com.taptrack.experiments.rancheria.R;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import timber.log.Timber;

public class BytePickerView extends NumberPicker {
    private boolean hasScannedValues = false;

    public BytePickerView(Context context) {
        super(context);
        scanForValues(context,null);
    }

    public BytePickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        scanForValues(context, attrs);
    }

    public BytePickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        scanForValues(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BytePickerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        scanForValues(context, attrs);
    }

    private void scanForValues(Context context, AttributeSet attrs) {
        if (hasScannedValues)
            return;

        hasScannedValues = true;

        setWrapSelectorWheel(true);
        setFormatter(new HexFormatter());
        setMaxValue(255);
        setMinValue(0);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.BytePickerView, 0, 0);
        try {

            if (a.hasValue(R.styleable.BytePickerView_initialValue))
                setValue(a.getInt(R.styleable.BytePickerView_initialValue, 0));
            else
                setValue(0);

            if (a.hasValue(R.styleable.BytePickerView_disableDivider))
                disableDivider();
        } finally {
            a.recycle();
        }
    }

    private void disableDivider() {
        Field[] parentFields = NumberPicker.class.getDeclaredFields();
        for (Field f : parentFields) {
            if (f.getName().equals("mSelectionDivider")) {
                f.setAccessible(true);
                try {
                    f.set(this, new ColorDrawable(Color.TRANSPARENT));
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    Timber.e(e, null);
                }
                break;
            }
        }
    }

    public void changeCurrentByOne(boolean increment) {
        Method method;
        try {
            // refelction call for
            // higherPicker.changeValueByOne(true);
            method = this.getClass().getSuperclass().getDeclaredMethod("changeValueByOne", boolean.class);
            method.setAccessible(true);
            method.invoke(this, increment);
        } catch (final NoSuchMethodException | IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static class HexFormatter implements Formatter {
        @Override
        public String format(int value) {
            return String.format("%02X",(byte)value);
        }
    }
}
