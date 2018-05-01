package com.example.albert.partymaps.Activity;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.albert.partymaps.Model.Event;
import com.example.albert.partymaps.Fragment.ListFragment;
import com.example.albert.partymaps.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FavoritosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Eventos favoritos");

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final ArrayList<Event> misEventos = new ArrayList<Event>();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();
        final ArrayList<String> eventos = new ArrayList();
        final ArrayList<String> nombres = new ArrayList();

        db.collection("Users").document(uid).collection("favoritos")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){


                    for (DocumentSnapshot document : task.getResult()) {

                        eventos.add(document.getId());

                    }

                }

                for(String evento: eventos){

                    db.collection("Events").document(evento).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                DocumentSnapshot document = task.getResult();
                                Event e = new Event(document.getString("name"), document.getString("music_type"), document.getString("description"),
                                        document.getString("locality"), document.getString("date"), document.getString("time"), document.getString("ubication"));

                                if(e.getName() != null){
                                    misEventos.add(e);
                                }

                            }

                            ListFragment listFragment = new ListFragment();

                            Bundle bundle = new Bundle();
                            bundle.putString("activity","FavoritosActivity");
                            bundle.putParcelableArrayList("eventos",misEventos);
                            listFragment.setArguments(bundle);
                            FragmentManager fragmentManager = getFragmentManager();
                            fragmentManager.beginTransaction().
                                    add(R.id.favoritos, listFragment).
                                    commit();

                        }
                    });

                }

            }
        });



    }

}
