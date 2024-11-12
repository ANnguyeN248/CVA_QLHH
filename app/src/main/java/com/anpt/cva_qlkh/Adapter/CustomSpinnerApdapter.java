package com.anpt.cva_qlkh.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class CustomSpinnerApdapter extends ArrayAdapter<String> {
    private Context context;
    private List<String> dataList;

    public CustomSpinnerApdapter(Context context, int resource, List<String> dataList) {
        super(context, resource, dataList);
        this.context = context;
        this.dataList = dataList;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(android.R.layout.simple_spinner_item, null);
        }

        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(dataList.get(position));

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, null);
        }

        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(dataList.get(position));

        return convertView;
    }
}
