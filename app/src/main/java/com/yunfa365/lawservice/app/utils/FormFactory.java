package com.yunfa365.lawservice.app.utils;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.agnetty.utils.RefUtil;
import com.yunfa365.lawservice.app.R;

import org.json.JSONException;
import org.json.JSONObject;

public class FormFactory {
    public static View createGroupTitle(Context context, String groupName) {
        View view = View.inflate(context, R.layout.form_group_title, null);
        TextView title = view.findViewById(R.id.text);
        title.setText(groupName);
        return view;
    }

    public static View createTextField(Context context, String labelStr, String valueStr) {
        View view = View.inflate(context, R.layout.form_text_field, null);
        TextView label = view.findViewById(R.id.label);
        TextView value = view.findViewById(R.id.value);
        label.setText(labelStr);
        value.setText(valueStr);
        view.setMinimumHeight(ScreenUtil.dip2px(44));
        return view;
    }

    public static View createLinkField(Context context, String labelStr, String valueStr, View.OnClickListener listener) {
        View view = View.inflate(context, R.layout.form_link_field, null);
        TextView label = view.findViewById(R.id.label);
        TextView value = view.findViewById(R.id.value);
        label.setText(labelStr);
        value.setText(valueStr);
        view.setOnClickListener(listener);
        view.setMinimumHeight(ScreenUtil.dip2px(44));
        return view;
    }

    private static View createForwordTextField(Context context, String labelStr, String valueStr, View.OnClickListener listener) {
        View view = View.inflate(context, R.layout.form_forword_text_field, null);
        TextView label = view.findViewById(R.id.label);
        TextView value = view.findViewById(R.id.value);
        label.setText(labelStr);
        value.setText(valueStr);
        view.setOnClickListener(listener);
        view.setMinimumHeight(ScreenUtil.dip2px(44));
        return view;
    }

    public static View createFieldByJson(Context context, JSONObject jsonObject, Object object) throws JSONException, NoSuchFieldException, IllegalAccessException {
        String field = jsonObject.getString("field");
        String label = jsonObject.getString("label");
        String style = jsonObject.optString("style", "text");
        String valueFormat = jsonObject.optString("valueFormat");
        String forwordHost = jsonObject.optString("forwordHost");
        String forwordId = jsonObject.optString("forwordId");
        String valueStr = jsonObject.optString("value");

        if (StringUtil.isNotEmpty(valueStr)) {

        } else if (StringUtil.isEmpty(valueFormat)) {
            valueStr = (String)RefUtil.getField(object, field);
        } else {

            String[] fields = field.split(",");
            Object[] agrs = new Object[fields.length];
            for (int i = 0; i < fields.length; i++) {
                String f = fields[i];
                Object fStr = RefUtil.getField(object, f);
                agrs[i] = fStr;
            }
            valueStr = String.format(valueFormat, agrs);;
        }
        View view = null;
        if (StringUtil.isNotEmpty(forwordHost)) {
            String fId = RefUtil.getField(object, forwordId).toString();
            view = createForwordTextField(context, label + "：", valueStr, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUtil.appUriForword(context, forwordHost, fId);
                }
            });
        } else if ("text".equalsIgnoreCase(style)) {
            view = createTextField(context, label + "：", valueStr);
        }
        return view;
    }
}
