package com.amadeus.audiobook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class List_view extends ArrayAdapter<File> {
    Context context;
    ViewHolder viewHolder;
    ArrayList<File> al_pdf;

    public List_view(Context context, ArrayList<File> al_pdf) {
        super(context, R.layout.activity_list_view, al_pdf);
        this.context = context;
        this.al_pdf = al_pdf;

    }


    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.activity_list_view, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tv_filename = (TextView) view.findViewById(R.id.tv_name);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();

        }

        viewHolder.tv_filename.setText(al_pdf.get(position).getName());
        return view;

    }

    public class ViewHolder {

        TextView tv_filename;
    }

}