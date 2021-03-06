package com.example.albert.partymaps;

import android.app.FragmentManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MisEventosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_eventos);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final ArrayList<Event> misEventos = new ArrayList<Event>();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        db.collection("Events").whereEqualTo("event_maker", user.getUid())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){

                    for (DocumentSnapshot document : task.getResult()) {
                        Event e = new Event(document.getString("name"), document.getString("music_type"), document.getString("description"),
                                document.getString("locality"), document.getString("date"), document.getString("time"), document.getString("ubication"));

                        misEventos.add(e);
                    }

                    ListFragment listFragment = new ListFragment();

                    Bundle bundle = new Bundle();
                    bundle.putString("activity","MisEventos");
                    bundle.putParcelableArrayList("eventos",misEventos);
                    listFragment.setArguments(bundle);
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().
                            add(R.id.miseventos, listFragment).
                            commit();
                }
            }
        });




    }
}
