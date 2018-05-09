package com.example.albert.partymaps.Fragment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;

import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.albert.partymaps.Activity.Main2Activity;
import com.example.albert.partymaps.Activity.UsersListActivity;
import com.example.albert.partymaps.Model.Event;
import com.example.albert.partymaps.Model.User;
import com.example.albert.partymaps.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment  {


    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final int PICK_IMAGE = 100;
    private Uri filePath;
    //Firebase
    public TextView nomUsuari ;
    public TextView correuUsuari ;
    public TextView dataNaixement ;
    private Button followButton;
    public TextView numEventos ;
    public boolean seguido = false;
    public User user ;
    FirebaseStorage storage;
    StorageReference storageReference;
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri imageUri;
    private String activity;
    ImageView foto_gallery;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        nomUsuari = (TextView) view.findViewById(R.id.nom_usuari);
        correuUsuari = (TextView) view.findViewById(R.id.correu_usuari);
        dataNaixement = (TextView) view.findViewById(R.id.fecha_usuari);
        numEventos = (TextView) view.findViewById(R.id.num_eventos);
        followButton = (Button) view.findViewById(R.id.followButton);
        foto_gallery = (ImageView) view.findViewById(R.id.profile_image);
        activity = getArguments().getString("activity");
        if(activity.equals("Main2Activity")){
            followButton.setVisibility(view.GONE);
            setOwnProfile();
            numEventos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((Main2Activity)getActivity()).showEvents(mAuth.getUid());
                }
            });
        }else{
            user = getArguments().getParcelable("user");
            nomUsuari.setText(user.getName());
            numEventos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((UsersListActivity)getActivity()).showEvents(user.getUid());
                }
            });
            isFollowing();
            correuUsuari.setText(user.getMail());
            dataNaixement.setText(user.getDate());
            storageReference.child("images/profile/"+ user.getUid()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Picasso.get().load(task.getResult()).into(foto_gallery);
                    }
                }
            });
            db.collection("Events").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    int num_events = 0;
                    for (DocumentSnapshot document : task.getResult()) {

                        if(document.getString("event_maker").equals(user.getUid())){
                            num_events++;
                        }
                    }
                    numEventos.setText(String.valueOf(num_events));
                }

            });
            followButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if(seguido){
                        followButton.setText("Seguir");
                        db.collection("Users").document(mAuth.getUid()).collection("seguidos").document(user.getUid()).delete();
                        seguido = false;
                    }else {
                        Map<String, Object> seguidos = new HashMap<>();
                        seguidos.put("UID", user.getUid());
                        db.collection("Users").document(mAuth.getUid()).collection("seguidos").document(user.getUid()).set(seguidos);
                        followButton.setText("Dejar de seguir");
                        seguido = true;
                    }
                }
            });
        }


        return view;
    }

    private void isFollowing() {
        db.collection("Users").document(mAuth.getUid()).collection("seguidos").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.exists()) {
                    followButton.setText("Dejar de seguir");
                    seguido = true;
                } else {

                }
            }
        });
    }

    private void setOwnProfile(){

        foto_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        storageReference.child("images/profile/"+ mAuth.getUid()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Picasso.get().load(task.getResult()).into(foto_gallery);
                }
            }
        });


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
    }


    private void uploadImage() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();


            StorageReference ref = storageReference.child("images/profile/"+ mAuth.getUid());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //progressDialog.dismiss();
                            //Toast.makeText(getActivity(), "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //progressDialog.dismiss();
                            //Toast.makeText(getActivity(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            //progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }
    private void openGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            uploadImage();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                foto_gallery.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}


