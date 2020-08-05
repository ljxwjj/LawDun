package com.yunfa365.lawservice.app.ui.view.form;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.yunfa365.lawservice.app.R;

public class TextViewField extends LinearLayout {
    private String labelStr;
    private String valueStr;
    private String hintStr;

    private TextView label;
    private TextView value;

    public TextViewField(Context context) {
        this(context, null);
    }

    public TextViewField(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextViewField(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public TextViewField(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray attributes = null;
        try {
            attributes = context.obtainStyledAttributes(attrs, R.styleable.SingleInputField);
            labelStr = attributes.getString(R.styleable.SingleInputField_labelStr);
            valueStr = attributes.getString(R.styleable.SingleInputField_valueStr);
            hintStr = attributes.getString(R.styleable.SingleInputField_hintStr);
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
        inflate(context, R.layout.field_text_view, this);
        label = findViewById(R.id.label);
        value = findViewById(R.id.value);
        label.setText(labelStr);
        value.setText(valueStr);
        value.setHint(hintStr);
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

    public CharSequence getValue() {
        return value.getText();
    }

    public void setError(CharSequence error) {
        value.setError(error);
    }
}
