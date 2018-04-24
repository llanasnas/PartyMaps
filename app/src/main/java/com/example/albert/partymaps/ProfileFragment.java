package com.example.albert.partymaps;


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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.IOException;
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
    FirebaseStorage storage;
    StorageReference storageReference;
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri imageUri;
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
        final TextView nomUsuari = (TextView) view.findViewById(R.id.nom_usuari);
        final TextView correuUsuari = (TextView) view.findViewById(R.id.correu_usuari);
        final TextView dataNaixement = (TextView) view.findViewById(R.id.fecha_usuari);
        final TextView numEventos = (TextView) view.findViewById(R.id.num_eventos);
        foto_gallery = (ImageView) view.findViewById(R.id.profile_image);

        foto_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
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





        return view;
    }
    private void uploadImage() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();


            StorageReference ref = storageReference.child("images/"+ mAuth.getUid().concat("profileimage"));
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
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
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK
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


