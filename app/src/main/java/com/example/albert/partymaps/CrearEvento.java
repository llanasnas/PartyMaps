package com.example.albert.partymaps;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.Manifest;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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

public class CrearEvento extends AppCompatActivity implements OnMapReadyCallback {

    private static GoogleMap mMap;
    private FirebaseAuth mAuth;
    private static final String TAG = CrearEvento.class.getSimpleName();
    private static EditText datePicker;
    private static EditText timePicker;
    private static LatLng position;
    private static Marker mark;
    private static MapFragment mapFragment;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static boolean marked=false,nombre=false,descripcion=false,date=false,time=false,coord=false,musicType=false;
    private static Event evento = new Event();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
        }
        setContentView(R.layout.activity_crear_evento);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

       final Spinner spinner = (Spinner) findViewById(R.id.spinner_city);

        String[] arraySpinner = getResources().getStringArray(R.array.cities);
        ArrayAdapter<String> adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,
               arraySpinner);
       spinner.setAdapter(adapter);
        timePicker = (EditText) findViewById(R.id.time_picker);
        datePicker = (EditText) findViewById(R.id.date_picker);


        datePicker.setInputType(InputType.TYPE_NULL);
        timePicker.setInputType(InputType.TYPE_NULL);
        AppCompatButton submit = (AppCompatButton) findViewById(R.id.confirm_event);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
        }

        submit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                nombre=false;descripcion=false;
                EditText name = (EditText) findViewById(R.id.nombre_evento);
                EditText description = (EditText) findViewById(R.id.description);
                EditText music = (EditText) findViewById(R.id.estilo_musica);

                if(isEmpty(name.getText().toString())){
                    name.setError("Este campo no puede estar vacio");
                }else{nombre=true;}
                if(!isEmpty(music.getText().toString())){musicType=true;}
                if(isEmpty(description.getText().toString())){
                    description.setError("Este campo no puede estar vacio");
                }else{descripcion=true;}
                if(marked){coord=true;}else{
                    Toast.makeText(getApplicationContext(), "Inserta una ubicación",
                            Toast.LENGTH_SHORT).show();
                }
                if(!date){datePicker.setError("Debes asignar una fecha");}
                if(!time){timePicker.setError("Debes asignar una hora");}


                if(coord&&nombre&&descripcion&date&&time&&musicType){
                    evento.setName(name.getText().toString());
                    evento.setDescription(description.getText().toString());
                    evento.setLocality(spinner.getSelectedItem().toString());
                    evento.setMusic_type(music.getText().toString());
                    FirebaseUser user = mAuth.getCurrentUser();
                    String uid = user.getUid();


                    Map<String, Object> user_add = new HashMap<>();
                    user_add.put("event_name", evento.getName());
                    user_add.put("music_style",evento.getMusic_type());
                    user_add.put("Description",evento.getDescription());
                    user_add.put("locality", evento.getLocality());
                    user_add.put("date", evento.getDate());
                    user_add.put("Time",evento.getTime());
                    user_add.put("ubication",evento.getUbication());
                    user_add.put("event_maker",uid);


                    db.collection("Events").document()
                            .set(user_add)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully written!");
                                    Toast.makeText(getApplicationContext(), "tot correcte",
                                            Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error writing document", e);
                                    Toast.makeText(getApplicationContext(), "Guardado failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                    //Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    //startActivity(intent);


                }

            }
        });


        datePicker.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                showDatePickerDialog(view);
            }
        });
        timePicker.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                showTimePickerDialog(view);
            }
        });



    }
    private boolean isEmpty(String nombre){

        if(nombre.length()==0){
            return true;
        }
        else{
            return false;
        }

    }
    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new CrearEvento.TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new CrearEvento.DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
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
        LatLng posicio;
        if(marked){
            mark.remove();
            mark = mMap.addMarker(new MarkerOptions().position(position).title("Fiesta"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
        }else {
            posicio = new LatLng(41.390205, 2.154007);
            mark = mMap.addMarker(new MarkerOptions().position(posicio).title("Fiesta"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(posicio));
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            Marker mark;

            @Override
            public void onMapClick(LatLng latLng) {

                Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
                startActivity(intent);

            }
        });
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            date=true;
            String fecha = day + "/" + month + "/" + year;
            evento.setDate(fecha);
            datePicker.setText(fecha);


        }
    }
    public static void takePos(LatLng latLng){

        marked = true;
        evento.setUbication(String.valueOf(latLng));
        position = latLng;
        mark.remove();
        mark = mMap.addMarker(new MarkerOptions().position(latLng).title("fieston")
                .position(latLng));
        evento.setUbication(String.valueOf(latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.moveCamera(CameraUpdateFactory.zoomIn());


    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user

            time = true;
            String hora = hourOfDay + ":" + minute;
            evento.setTime(hora);
            timePicker.setText(hora);

        }
    }

}

