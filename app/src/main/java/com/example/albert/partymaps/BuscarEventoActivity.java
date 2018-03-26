package com.example.albert.partymaps;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class BuscarEventoActivity extends AppCompatActivity {

    private ArrayList<Event> events = new ArrayList<Event>();
    private ArrayList<Event> eventos = new ArrayList<Event>();
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_evento);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final EditText nombre = (EditText) findViewById(R.id.bpn);
        final Spinner localities = (Spinner) findViewById(R.id.buscar_por_localidad);
        final Spinner musicTypes = (Spinner) findViewById(R.id.estilo_de_musica);


        String[] arraySpinner = getResources().getStringArray(R.array.cities);
        ArrayAdapter<String> adapterLocalities = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,
                arraySpinner);
        localities.setAdapter(adapterLocalities);
        String[] arraymusic = getResources().getStringArray(R.array.music_styles);
        ArrayAdapter<String> adapterMusica = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,
                arraymusic);
        events = getIntent().getParcelableArrayListExtra("events");
        musicTypes.setAdapter(adapterMusica);

        AppCompatButton buscar = (AppCompatButton) findViewById(R.id.buscar_boton);

        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (nombre.getText().toString().isEmpty() && musicTypes.getSelectedItem().toString().equals("Estilo musical") && localities.getSelectedItem().toString().equals("Localidad")) {

                    nombre.setError("Busca por nombre, localidad o estilo musical");

                } else {

                    String nom = "", localidad = "", musica = "";

                    if (!nombre.getText().toString().isEmpty()) {
                        nom = nombre.getText().toString();
                    }
                    if (!localities.getSelectedItem().toString().equals("Localidad")) {
                        localidad = localities.getSelectedItem().toString();
                    }
                    if (!musicTypes.getSelectedItem().toString().equals("Estilo musical")) {
                        musica = musicTypes.getSelectedItem().toString();
                    }
                    Boolean added ;
                    for (Event event : events) {
                        added=false;
                        while(!added){
                            if (!nom.equals("")) {
                                if (event.getName().contains(nom)) {
                                    eventos.add(event);
                                    break;
                                }
                            }
                            if (!localidad.equals("")) {
                                if (event.getLocality().equals(localidad)) {
                                    eventos.add(event);
                                    break;
                                }
                            }
                            added = true;
                        }


                    }


                }
            }
        });


    }

}
