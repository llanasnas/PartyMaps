package com.example.albert.partymaps;


import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment implements AdapterView.OnItemClickListener{

    private Event[] events;

    public ListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_, container, false);

        ListView listView = view.findViewById(R.id.listViewEvents);
        EventAdapter adapter = new EventAdapter(events, getActivity());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        return view;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        events = getEvents();


    }

    public Event[] getEvents() {

        /*Event[] events  =  new Event[3];
        Date data = new Date(20,5,2010);
        Event event1 = new Event("nombre del niño","Badalona style","descripcion acúsica", "Barcelona","data","10:10","ubicacion" );
        Event event2 = new Event("Calidoscopio terremotrico 2 ","Badalona style","descripcion acúsica", "Coruña","data","10:10","ubicacion" );
        Event event3 = new Event("nombre del niño","Badalona style","descripcion acúsica", "Barcelona","data","10:10","ubicacion" );
        events[0] = event1;
        events[1] = event2;
        events[2] = event3;*/
        return events;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
