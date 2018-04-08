package com.example.albert.partymaps;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * A simple {@link Fragment} subclass.
 */
public class DescriptionFragment extends Fragment  {


    private static GoogleMap mMap;
    private static LatLng position;
    private static Marker mark;
    private static Event event;
    private boolean favorite = false;
    private static MapFragment mapFragment;

    public DescriptionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_description, container, false);
        event = getArguments().getParcelable("evento");
        String ubicacion = event.getUbication().substring(10,event.getUbication().length()-1);
        String[] ubi = ubicacion.split(",");
        final LatLng latLng = new LatLng(Double.parseDouble(ubi[0]),Double.parseDouble(ubi[1]));
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {

                    googleMap.addMarker(new MarkerOptions().position(latLng)
                            .title(event.getName()));
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                }
            });
        }
        getChildFragmentManager().beginTransaction().replace(R.id.mapa, mapFragment).commit();
        TextView name = (TextView) view.findViewById(R.id.name_description);
        TextView description = (TextView) view.findViewById(R.id.description_description);
        TextView fecha = (TextView) view.findViewById(R.id.date_description);
        TextView hora = (TextView) view.findViewById(R.id.hour_description);
        final ImageView star = (ImageView) view.findViewById(R.id.star);

        star.setOnClickListener(new View.OnClickListener() {
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
        });

        name.setText(event.getName());
        description.setText(event.getDescription());
        fecha.setText(event.getDate());
        hora.setText(event.getTime());



        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




    }
    private void reloadMap(){

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                googleMap.addMarker(new MarkerOptions().position(position)
                        .title(event.getName()));
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(position));
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        reloadMap();
    }
}
