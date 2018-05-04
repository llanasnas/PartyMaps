package com.example.albert.partymaps.Activity;


import android.content.pm.PackageManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.Manifest;

import com.example.albert.partymaps.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseAuth mAuth;
    private static final String TAG = MapsActivity.class.getSimpleName();
    private static boolean marked=false;
    private static Marker mark;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }
    private void moveToCurrentLocation(LatLng currentLocation)
    {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,15));
        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

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
        mark = mMap.addMarker(new MarkerOptions().position(barcelona).title("Event"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(barcelona));

        moveToCurrentLocation(barcelona);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {


            @Override

            public void onMapClick(LatLng latLng) {
                CrearEventoActivity.takePos(latLng);
                if(marked){
                    mark.remove();
                    mark = mMap.addMarker(new MarkerOptions().position(latLng).title("fieston")
                            .position(latLng));

                }else {
                    mark.remove();
                    mark = mMap.addMarker(new MarkerOptions().position(latLng).title("fieston")
                            .position(latLng));
                    marked=true;
                }
            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
        marked=false;
    }
}
