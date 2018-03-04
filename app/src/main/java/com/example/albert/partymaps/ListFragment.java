package com.example.albert.partymaps;


import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ArrayList<Event> events;
    private FirebaseAuth mAuth;
    private ArrayList<Event> eventos = new ArrayList<Event>();
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

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

    public ArrayList<Event> getEvents() {


        setEvents();

        return eventos;

    }

    public void setEvents() {

        db.collection("Events")
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

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
