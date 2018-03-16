package com.example.albert.partymaps;

import android.app.FragmentManager;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class BuscarEventoActivity extends AppCompatActivity {

    private ArrayList<Event> eventos = new ArrayList<Event>();
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_evento);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final EditText nombre = (EditText) findViewById(R.id.buscar_por_nombre);
        final Spinner localities = (Spinner) findViewById(R.id.buscar_por_localidad);
        final Spinner musicTypes = (Spinner) findViewById(R.id.estilo_de_musica);

        String[] arraySpinner = getResources().getStringArray(R.array.cities);
        ArrayAdapter<String> adapterLocalities = new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,
                arraySpinner);
        localities.setAdapter(adapterLocalities);
        String[] arraymusic = getResources().getStringArray(R.array.music_styles);
        ArrayAdapter<String> adapterMusica = new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,
                arraymusic);
        musicTypes.setAdapter(adapterMusica);

        AppCompatButton buscar = (AppCompatButton) findViewById(R.id.buscar_boton);

        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(nombre.getText().toString().isEmpty()&&musicTypes.getSelectedItem().toString().equals("Estilo musical")){

                    nombre.setError("");

                }   else {
                    ListFragment listFragment = new ListFragment();
                    //listFragment.setArguments(createBundle());
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().
                            add(R.id.main, listFragment).
                            commit();
                }
            }
        });






    }
    private void readData(final ListFragment.Firestorecallback firestorecallback) {
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

}
