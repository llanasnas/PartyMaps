package com.example.albert.partymaps;


import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {


    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        final TextView nomUsuari = (TextView) view.findViewById(R.id.nom_usuari);
        final TextView correuUsuari = (TextView) view.findViewById(R.id.correu_usuari);
        final TextView dataNaixement = (TextView) view.findViewById(R.id.fecha_usuari);
        final TextView numEventos = (TextView) view.findViewById(R.id.num_eventos);

        db.collection("Users").document(mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                DocumentSnapshot userData = task.getResult();

                nomUsuari.setText(userData.getString("name"));
                correuUsuari.setText(userData.getString("mail"));
                dataNaixement.setText(userData.getString("date"));


            }
        });

        db.collection("Events").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                int num_events = 0;
                for (DocumentSnapshot document : task.getResult()) {

                        if(document.getString("event_maker").equals(mAuth.getUid())){
                            num_events++;
                        }
                }
                numEventos.setText(String.valueOf(num_events));
            }

        });





        return view;
    }

}
