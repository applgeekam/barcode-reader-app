package com.example.barcodereader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class AbstractAdapater extends BaseAdapter {


    ArrayList<HashMap<String,String>> liste;
    Context ActivityContext;
    LayoutInflater inflater;

    public AbstractAdapater(Context context, ArrayList<HashMap<String,String>> data){
        inflater=LayoutInflater.from(context);
        ActivityContext = context;
        liste = data;
    }

    //le nombre total d'element
    @Override
    public int getCount() {
        return liste.size();
    }
    //un element de la liste
    @Override
    public Object getItem(int position) {
        return liste.get(position);
    }
    //un identifiant
    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public abstract View getView(int position, View convertView, ViewGroup parent);
}
