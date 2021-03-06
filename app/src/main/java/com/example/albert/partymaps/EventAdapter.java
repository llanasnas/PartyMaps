package com.example.albert.partymaps;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

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
        TextView day = (TextView) view.findViewById(R.id.dia);
        TextView month = (TextView) view.findViewById(R.id.mes);
        SimpleDateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dateAsString = event.getDate();
        try {
            Date date = sourceFormat.parse(dateAsString);
            String mes = new SimpleDateFormat("MMM").format(date).toUpperCase();
            month.setText(mes);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        String day_final  = event.getDate().substring(0,2);
        day_final = day_final.replace("/","");
        day.setText(day_final);

        name.setText(event.getName());
        TextView locality = (TextView) view.findViewById(R.id.localidad);
        locality.setText(String.valueOf(event.getLocality()));

        return view;
    }

}
