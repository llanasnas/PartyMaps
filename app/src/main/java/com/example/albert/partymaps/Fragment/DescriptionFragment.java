package com.example.albert.partymaps.Fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.albert.partymaps.Activity.CrearEventoActivity;
import com.example.albert.partymaps.Model.Event;
import com.example.albert.partymaps.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class DescriptionFragment extends Fragment  {


    private static GoogleMap mMap;
    private static LatLng position;
    private static final String TAG = CrearEventoActivity.class.getSimpleName();
    private FirebaseAuth mAuth;
    private static Marker mark;
    private boolean favorite = false;
    private static MapFragment mapFragment;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public DescriptionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_description, container, false);
        Toolbar toolbar = view.findViewById(R.id.toolbarDesc);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        if( ((AppCompatActivity)getActivity()).getSupportActionBar() != null){
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        }
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab_fav);


        final Event event = getArguments().getParcelable("evento");
        String ubicacion = event.getUbication().substring(10,event.getUbication().length()-1);
        String[] ubi = ubicacion.split(",");
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth = FirebaseAuth.getInstance();
                final FirebaseUser user = mAuth.getCurrentUser();
                CollectionReference events = db.collection("Events");

                events.whereEqualTo("date",event.getDate()).whereEqualTo("ubication",event.getUbication()).whereEqualTo("time",event.getTime()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                String id = document.getId();
                                String idevent = event.getDate() + event.getUbication() + event.getTime();
                                Map<String, Object> favoritos = new HashMap<>();
                                favoritos.put("concatenat",idevent);
                                favoritos.put("id",id);


                                db.collection("Users").document(user.getUid()).collection("favoritos").document(id).set(favoritos)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "DocumentSnapshot successfully written!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error writing document", e);
                                            }
                                        });


                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });






                Snackbar.make(view, "Añadido a tus favoritos", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }});
        final LatLng latLng = new LatLng(Double.parseDouble(ubi[0]),Double.parseDouble(ubi[1]));
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {

                    googleMap.addMarker(new MarkerOptions().position(latLng)
                            .title(event.getName()));
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                }
            });
        }
        getChildFragmentManager().beginTransaction().replace(R.id.mapa, mapFragment).commit();
        TextView name = (TextView) view.findViewById(R.id.name_description);
        TextView description = (TextView) view.findViewById(R.id.description_description);
        TextView fecha = (TextView) view.findViewById(R.id.date_description);
        //TextView hora = (TextView) view.findViewById(R.id.hour_description);
        //final ImageView star = (ImageView) view.findViewById(R.id.fav);

       /* star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(favorite){
                    star.setImageResource(R.drawable.ic_star_border_black_24dp);
                    favorite = false;
                }else {
                    star.setImageResource(R.drawable.ic_star_llena_24dp);
                    favorite = true;
                }
            }
        });*/


        name.setText(event.getName());
        toolbar.setTitle(event.getName());
        description.setText(event.getDescription());
        fecha.setText(event.getDate() + " " + event.getTime());
        //hora.setText(event.getTime());



        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }



}
