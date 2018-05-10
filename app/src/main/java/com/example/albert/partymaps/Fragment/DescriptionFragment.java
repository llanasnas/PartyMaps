package com.example.albert.partymaps.Fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.albert.partymaps.Activity.CrearEventoActivity;
import com.example.albert.partymaps.Model.Event;
import com.example.albert.partymaps.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 */
public class DescriptionFragment extends Fragment {


    private static GoogleMap mMap;
    private static LatLng position;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference();
    private static final String TAG = CrearEventoActivity.class.getSimpleName();
    private FirebaseAuth mAuth;
    private static Marker mark;
    private Event event;
    private static ImageView[] stars = new ImageView[5];
    private boolean favorite = false;
    private boolean[] starMarked = new boolean[5];
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
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        }
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab_fav);
        stars[0] = (ImageView) view.findViewById(R.id.star1);
        stars[1] = (ImageView) view.findViewById(R.id.star2);
        stars[2] = (ImageView) view.findViewById(R.id.star3);
        stars[3] = (ImageView) view.findViewById(R.id.star4);
        stars[4] = (ImageView) view.findViewById(R.id.star5);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        final String uid = user.getUid();
        event = getArguments().getParcelable("evento");
        String ubicacion = event.getUbication().substring(10, event.getUbication().length() - 1);
        String[] ubi = ubicacion.split(",");
        setStars();
        final long ONE_MEGABYTE = 1024 * 1024;
        storageReference = storage.getReferenceFromUrl("gs://partymaps-51476.appspot.com").child("images/events/"+event.getId());
        final ImageView mImageView = (ImageView) view.findViewById(R.id.image_description);
        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                mImageView.setImageBitmap(bitmap);
            }
        });
        if (getActivity().getLocalClassName().toString().equals("Activity.FavoritosActivity")) {
            fab.setImageResource(R.mipmap.ic_drop);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("No tan rápido...");
                    alertDialog.setMessage("¿Quieres eliminar este evento de tu lista de favoritos?");
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancelar",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Eliminar",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    final String idevent = event.getDate() + event.getUbication() + event.getTime();
                                    db.collection("Users").document(uid).collection("favoritos").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (DocumentSnapshot document : task.getResult()) {
                                                    if (document.getString("concatenat").equals(idevent)){
                                                        db.collection("Users").document(uid).collection("favoritos").document(document.getId()).delete();
                                                    }
                                                }

                                            }
                                            Toast.makeText(getApplicationContext(), "Evento eliminado de favoritos", Toast.LENGTH_SHORT).show();
                                            getActivity().finish();
                                        }
                                    });
                                }
                            });
                    alertDialog.show();
                }
            });




        } else if (getActivity().getLocalClassName().toString().equals("Activity.MisEventosActivity")) {

            fab.setImageResource(R.mipmap.ic_drop);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("Piénsatelo...");
                    alertDialog.setMessage("Si borras este evento desaparecerá para todos, ¿estás seguro?");
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancelar",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Eliminar",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    final String idevent = event.getDate() + event.getUbication() + event.getTime();
                                    db.collection("Events").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (DocumentSnapshot document : task.getResult()) {
                                                    if(document.getString("event_maker").equals(uid) && document.getString("ubication").equals(event.getUbication())){
                                                        db.collection("Events").document(document.getId()).delete();
                                                    }
                                                }

                                            }
                                            Toast.makeText(getApplicationContext(), "Has eliminado tu evento", Toast.LENGTH_SHORT).show();
                                            getActivity().finish();
                                        }
                                    });
                                }
                            });
                    alertDialog.show();
                }
            });

        } else {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CollectionReference events = db.collection("Events");

                    events.whereEqualTo("date", event.getDate()).whereEqualTo("ubication", event.getUbication()).whereEqualTo("time", event.getTime()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    String id = document.getId();
                                    String idevent = event.getDate() + event.getUbication() + event.getTime();
                                    Map<String, Object> favoritos = new HashMap<>();
                                    favoritos.put("concatenat", idevent);
                                    favoritos.put("id", id);


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
                }
            });
        }


        final LatLng latLng = new LatLng(Double.parseDouble(ubi[0]), Double.parseDouble(ubi[1]));
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {

                    googleMap.getUiSettings().setScrollGesturesEnabled(false);
                    googleMap.getUiSettings().setZoomGesturesEnabled(false);
                    googleMap.addMarker(new MarkerOptions().position(latLng)
                            .title(event.getName()));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                    // Zoom in, animating the camera.
                    googleMap.animateCamera(CameraUpdateFactory.zoomIn());
                    // Zoom out to zoom level 10, animating with a duration of 2 seconds.
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(17), 1000, null);
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
    public void setStars(){

        for(int i=0;i<5;i++){
            final int aux = i;
            stars[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                        switch (aux){

                            case 0:

                                for (int j=0;j<5;j++){
                                    if(j==0){
                                        stars[j].setImageResource(R.drawable.ic_star_llena_24dp);
                                    }else{
                                        stars[j].setImageResource(R.drawable.ic_star_border_black_24dp);
                                    }
                                }
                                break;

                            case 1:

                                for (int j=0;j<5;j++){
                                    if(j==0||j==1){
                                        stars[j].setImageResource(R.drawable.ic_star_llena_24dp);
                                    }else{
                                        stars[j].setImageResource(R.drawable.ic_star_border_black_24dp);
                                    }
                                }
                                break;
                            case 2:

                                for (int j=0;j<5;j++){
                                    if(j==0||j==1||j==2){
                                        stars[j].setImageResource(R.drawable.ic_star_llena_24dp);
                                    }else{
                                        stars[j].setImageResource(R.drawable.ic_star_border_black_24dp);
                                    }
                                }
                                break;
                            case 3:

                                for (int j=0;j<5;j++){
                                    if(j==0||j==1||j==2||j==3){
                                        stars[j].setImageResource(R.drawable.ic_star_llena_24dp);
                                    }else{
                                        stars[j].setImageResource(R.drawable.ic_star_border_black_24dp);
                                    }
                                }
                                break;
                            case 4:

                                for (int j=0;j<5;j++){
                                    if(j==0||j==1||j==2||j==3||j==4){
                                        stars[j].setImageResource(R.drawable.ic_star_llena_24dp);
                                    }else{
                                        stars[j].setImageResource(R.drawable.ic_star_border_black_24dp);
                                    }
                                }
                                break;

                        }
                }
            });

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        mapFragment = null;
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapFragment = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


}
