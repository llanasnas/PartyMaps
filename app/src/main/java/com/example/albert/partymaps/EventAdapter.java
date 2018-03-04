package com.example.albert.partymaps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Gerard on 04/03/2018.
 */

public class EventAdapter extends BaseAdapter {

    private ArrayList<Event> events;
    private Context context;

    public EventAdapter(ArrayList<Event> events, Context context) {
        this.events = events;
        this.context = context;
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Object getItem(int i) {
        return events.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_ayout, viewGroup, false);
        }
        Event event = events.get(i);
        TextView name = (TextView) view.findViewById(R.id.nombre_evento);
        name.setText(event.getName());
        TextView locality = (TextView) view.findViewById(R.id.localidad);
        locality.setText(String.valueOf(event.getLocality()));

        return view;
    }

}
