package com.yunfa365.lawservice.app.ui.view.form;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.Editable;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.yunfa365.lawservice.app.R;

public class SingleInputField extends LinearLayout {
    private String labelStr;
    private String valueStr;
    private String hintStr;
    private boolean isSelect;
    private int inputType;
    private int maxLength;

    private TextView label;
    private EditText value;

    public SingleInputField(Context context) {
        this(context, null);
    }

    public SingleInputField(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SingleInputField(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SingleInputField(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray attributes = null;
        try {
            attributes = context.obtainStyledAttributes(attrs, R.styleable.SingleInputField);
            labelStr = attributes.getString(R.styleable.SingleInputField_labelStr);
            valueStr = attributes.getString(R.styleable.SingleInputField_valueStr);
            hintStr = attributes.getString(R.styleable.SingleInputField_hintStr);
            isSelect = attributes.getBoolean(R.styleable.SingleInputField_isSelect, false);
            inputType = attributes.getInt(R.styleable.SingleInputField_android_inputType, EditorInfo.TYPE_NULL);
            maxLength = attributes.getInt(R.styleable.SingleInputField_android_maxLength, -1);
        } finally {
            if(attributes != null) {
                attributes.recycle();
            }
        }
        initView(context);
    }

    private void initView(Context context) {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_VERTICAL);
        inflate(context, R.layout.field_single_input, this);
        label = findViewById(R.id.label);
        value = findViewById(R.id.value);
        label.setText(labelStr);
        value.setText(valueStr);
        value.setHint(hintStr);

        if (isSelect) {
            value.setFocusable(false);
            value.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.mipmap.select_down_gray, 0);
        }
        if (inputType != EditorInfo.TYPE_NULL) {
            value.setInputType(inputType);
        }
        if (maxLength >= 0) {
            value.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        }
    }

    public void setLabelStr(String str) {
        label.setText(str);
    }

    public void setValueStr(String str) {
        value.setText(str);
    }

    public void setHintStr(String hintStr) {
        value.setHint(hintStr);
    }

    public Editable getValue() {
        return value.getText();
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        value.setOnClickListener(l);
    }

    public void setError(CharSequence error) {
        value.setError(error);
    }

    @Override
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        return value.requestFocus();
    }

    public boolean hasFocusable() {
        return value.isFocusable();
    }

    private void preview(Context context, TypedArray a) {

    }
}
