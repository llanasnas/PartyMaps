package com.example.albert.partymaps.Fragment;


import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

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
            if (events.isEmpty()){
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("Un momento...");
                alertDialog.setMessage("Parece que aún no has creado ningún evento...");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Volver",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                getActivity().finish();
                            }
                        });
                alertDialog.show();
            }else {
                adapter = new EventAdapter(events, getActivity());
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

        }else if(activity.equals("FavoritosActivity")){

            events = getArguments().getParcelableArrayList("eventos");

            if (events.isEmpty()){
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("Espera...");
                alertDialog.setMessage("Parece que no tienes ningún evento favorito...");
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Volver",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                getActivity().finish();
                            }
                        });


                alertDialog.show();
            }else{
                adapter = new EventAdapter(events, getActivity());
                listView.setAdapter(adapter);
            }

        }else if(activity.equals("profile")){
            events = getArguments().getParcelableArrayList("events");
            if (events.isEmpty()) {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("Un momento...");
                alertDialog.setMessage("Parece que aún no ha creado ningún evento...");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Volver",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                getActivity().finish();
                            }
                        });
                alertDialog.show();
            }else{
                adapter = new EventAdapter(events, getActivity());
                listView.setAdapter(adapter);
            }
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
                Collections.sort(events);
                EventAdapter adapter = new EventAdapter(events, getActivity());
                listView.setAdapter(adapter);


            }
        });
        return events;

    }


    private void readData(final Firestorecallback firestorecallback) {

        db.collection("Events")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {

                                try {
                                    SimpleDateFormat fecha = new SimpleDateFormat("dd/MM/yyyy");
                                    Date fechaActual = new Date();
                                    Date fechaEvent = fecha.parse(document.getString("date"));
                                    if(fechaEvent.after(fechaActual)){
                                        String date = document.getString("date");
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        Event e = new Event(document.getString("name"), document.getString("music_type"), document.getString("description"),
                                                document.getString("locality"), document.getString("date"), document.getString("time"), document.getString("ubication"));
                                            e.setId(document.getId());
                                        eventos.add(e);
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }


                            }
                            Collections.sort(eventos);
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

        }else if(activity.equals("profile")){

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().
                    replace(R.id.userList, descriptionFragment).
                    addToBackStack(null).
                    commit();
        }

    }
}
