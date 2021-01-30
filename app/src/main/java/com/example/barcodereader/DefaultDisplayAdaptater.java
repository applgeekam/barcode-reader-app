package com.example.barcodereader;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class DefaultDisplayAdaptater extends AbstractAdapater {


    public DefaultDisplayAdaptater(Context context, ArrayList<HashMap<String, String>> data) {
        super(context, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        HashMap<String, String> item = liste.get(position);

        if (Objects.equals(item.get("type"), "WIFI"))
        {
            convertView = inflater.inflate(R.layout.wifi_list,  parent, false);
            TextView type = (TextView) convertView.findViewById(R.id.wifi_item_type_value);
            type.setText(item.get("type"));
            TextView ssid = (TextView) convertView.findViewById(R.id.wifi_item_ssid_value);
            ssid.setText(item.get("ssid"));
            TextView password = (TextView) convertView.findViewById(R.id.wifi_item_password_value);
            password.setText(item.get("password"));
            TextView encryption = (TextView) convertView.findViewById(R.id.wifi_item_encryption_value);
            encryption.setText(item.get("encryption"));
        }
        else if(Objects.equals(item.get("type"), "URL"))
        {
            convertView = inflater.inflate(R.layout.url_list,  parent, false);
            TextView type = (TextView) convertView.findViewById(R.id.url_item_type_value);
            type.setText(item.get("type"));
            TextView title = (TextView) convertView.findViewById(R.id.url_item_title_value);
            title.setText(item.get("title"));
            TextView url = (TextView) convertView.findViewById(R.id.url_item_link_value);
            url.setText(item.get("url"));
        }
        else {
            convertView = inflater.inflate(R.layout.default_list,  parent, false);
            TextView type = (TextView) convertView.findViewById(R.id.default_item_type_value);
            type.setText(item.get("type"));
            TextView value = (TextView) convertView.findViewById(R.id.default_item_value_value);
            value.setText(item.get("value"));
            TextView encoded = (TextView) convertView.findViewById(R.id.default_item_encode_value);
            encoded.setText(item.get("encoded"));
        }

        return convertView;
    }

}
