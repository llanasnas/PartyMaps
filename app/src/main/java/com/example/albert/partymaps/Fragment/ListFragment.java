package com.example.albert.partymaps.Fragment;


import android.app.FragmentManager;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.albert.partymaps.Activity.RegisterActivity;
import com.example.albert.partymaps.Model.Event;
import com.example.albert.partymaps.Util.EventAdapter;
import com.example.albert.partymaps.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ArrayList<Event> events = new ArrayList<>();
    private FirebaseAuth mAuth;
    EventAdapter adapter;
    private String activity;
    private ArrayList<Event> eventos = new ArrayList<Event>();
    ListView listView;
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onPause() {
        super.onPause();
        eventos.clear();
        events.clear();
        if (adapter != null){
            adapter.notifyDataSetChanged();
            getEvents();
        }

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_, container, false);
        listView = view.findViewById(R.id.listViewEvents);
        listView.setOnItemClickListener(this);
        activity = getArguments().getString("activity");
        if(activity.equals("Main2Activity")){

            getEvents();

        }else if(activity.equals("BuscarEvento")){

            events = getArguments().getParcelableArrayList("eventos");
            adapter = new EventAdapter(events, getActivity());
            listView.setAdapter(adapter);

        }else if(activity.equals("MisEventos")){

            events = getArguments().getParcelableArrayList("eventos");
            adapter = new EventAdapter(events, getActivity());
            listView.setAdapter(adapter);
        }else if(activity.equals("FavoritosActivity")){

            events = getArguments().getParcelableArrayList("eventos");
            adapter = new EventAdapter(events, getActivity());
            listView.setAdapter(adapter);
        }
        return view;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public ArrayList<Event> getEvents() {

        readData(new Firestorecallback() {
            @Override
            public void onCallback(ArrayList<Event> list) {
                events = list;
                Log.d(TAG,"Tamaño" + events.size());
                EventAdapter adapter = new EventAdapter(events, getActivity());
                listView.setAdapter(adapter);


            }
        });
        return events;

    }


    private void readData(final Firestorecallback firestorecallback) {
        db.collection("Events").orderBy("date")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Event e = new Event(document.getString("name"), document.getString("music_type"), document.getString("description"),
                                        document.getString("locality"), document.getString("date"), document.getString("time"), document.getString("ubication"));

                                eventos.add(e);

                            }
                            firestorecallback.onCallback(eventos);

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


    }

    private interface Firestorecallback {
        void onCallback(ArrayList<Event> list);
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        Event event = events.get(i);
        DescriptionFragment descriptionFragment = new DescriptionFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("evento",event);
        descriptionFragment.setArguments(bundle);

        if(activity.equals("Main2Activity")){

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().
                    replace(R.id.main,descriptionFragment).
                    addToBackStack(null).
                    commit();

        } else if(activity.equals("BuscarEvento")){


            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().
                    replace(R.id.buscar_eventos, descriptionFragment).
                    addToBackStack(null).
                    commit();
        } else if(activity.equals("MisEventos")){

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().
                    replace(R.id.miseventos, descriptionFragment).
                    commit();

        } else if(activity.equals("FavoritosActivity")){

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().
                    replace(R.id.favoritos, descriptionFragment).
                    commit();

        }

    }
}
