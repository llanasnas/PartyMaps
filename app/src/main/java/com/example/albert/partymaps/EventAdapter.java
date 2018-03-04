package com.example.albert.partymaps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Gerard on 04/03/2018.
 */

public class EventAdapter extends BaseAdapter {

    private Event[] events;
    private Context context;

    public EventAdapter(Event[] events, Context context) {
        this.events = events;
        this.context = context;
    }

    @Override
    public int getCount() {
        return events.length;
    }

    @Override
    public Object getItem(int i) {
        return events[i];
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
        Event event = events[i];
        TextView name = (TextView) view.findViewById(R.id.nombre_evento);
        name.setText(event.getName());
        TextView locality = (TextView) view.findViewById(R.id.localidad);
        locality.setText(String.valueOf(event.getLocality()));

        return view;
    }

}
