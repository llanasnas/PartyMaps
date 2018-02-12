package com.example.albert.partymaps;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import android.Manifest;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseAuth mAuth;
    private static final String TAG = MapsActivity.class.getSimpleName();
    private static EditText datePicker;
    private static EditText timePicker ;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static boolean marked=false,nombre=false,descripcion=false,date=false,time=false,coord=false,musicType=false;
    private static Event evento = new Event();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
        }
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }
    private boolean isEmpty(String nombre){

        if(nombre.length()==0){
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng barcelona = new LatLng(41.390205, 2.154007);
        mMap.addMarker(new MarkerOptions().position(barcelona).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(barcelona));



        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            Marker mark;

            @Override

            public void onMapClick(LatLng latLng) {

                if(marked){
                    mark.remove();
                    mark = mMap.addMarker(new MarkerOptions().position(latLng).title("fieston")
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.fieston))
                            .anchor(0.0f, 1.0f).position(latLng));
                            evento.setUbication(String.valueOf(latLng));
                }else {
                    mark = mMap.addMarker(new MarkerOptions().position(latLng).title("fieston")
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.fieston))
                            .anchor(0.0f, 1.0f).position(latLng));
                            evento.setUbication(String.valueOf(latLng));
                    marked=true;

                }

            }
        });
    }
}
