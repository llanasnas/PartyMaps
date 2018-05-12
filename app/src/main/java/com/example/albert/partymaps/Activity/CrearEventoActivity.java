package com.example.albert.partymaps.Activity;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
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
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.albert.partymaps.Model.Event;
import com.example.albert.partymaps.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

public class CrearEventoActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static GoogleMap mMap;
    private FirebaseAuth mAuth;
    private static final String TAG = CrearEventoActivity.class.getSimpleName();
    private static EditText datePicker;
    private static EditText timePicker;
    private static int RESULT_LOAD_IMG = 1;
    private ImageView imatgeEvento;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageReference = storage.getReference();
    private static LatLng position;
    private InputStream imageStream;
    private Uri imageUri;
    private Bitmap selectedImage;
    EditText description ;
    String imgDecodableString;
    private static Marker mark;
    private static MapFragment mapFragment;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static boolean marked=false,nombre=false,descripcion=false,date=false,time=false,coord=false,musicType=false,imageUpload=false;
    private static Event evento = new Event();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_crear_evento);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

       final Spinner spinner = (Spinner) findViewById(R.id.spinner_city);
       final Spinner music_type = (Spinner) findViewById(R.id.estilo_musica);

        String[] arraySpinnerMusica = getResources().getStringArray(R.array.music_styles);
        String[] arraySpinner = getResources().getStringArray(R.array.cities);
        ArrayAdapter<String> adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,
                arraySpinner);
        ArrayAdapter<String> adapterMusica = new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,
                arraySpinnerMusica);
       spinner.setAdapter(adapter);
       music_type.setAdapter(adapterMusica);
        timePicker = (EditText) findViewById(R.id.time_picker);
        datePicker = (EditText) findViewById(R.id.date_picker);
        imatgeEvento = (ImageView) findViewById(R.id.imatge_evento);
        imatgeEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
            }
        });

        datePicker.setInputType(InputType.TYPE_NULL);
        timePicker.setInputType(InputType.TYPE_NULL);
        AppCompatButton submit = (AppCompatButton) findViewById(R.id.confirm_event);
        description = (EditText) findViewById(R.id.description);
        description.setScroller(new Scroller(getApplicationContext()));
        description.setVerticalScrollBarEnabled(true);
        description.setMaxLines(30);

        submit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                nombre=false;descripcion=false;
                EditText name = (EditText) findViewById(R.id.nombre_evento);

                Spinner music = (Spinner) findViewById(R.id.estilo_musica);

                if(isEmpty(name.getText().toString())){
                    name.setError("Este campo no puede estar vacio");
                }else{nombre=true;}
                if(!music.getSelectedItem().toString().equals("Estilo Musical")){musicType=true;}
                if(isEmpty(description.getText().toString())){
                    description.setError("Este campo no puede estar vacio");
                }else{descripcion=true;}
                if(marked){coord=true;}else{
                    Toast.makeText(getApplicationContext(), "Inserta una ubicación",
                            Toast.LENGTH_SHORT).show();
                }
                if(!date){datePicker.setError("Debes asignar una fecha");}
                if(!time){timePicker.setError("Debes asignar una hora");}
                if (checkDatePast(datePicker.getText().toString())){
                    datePicker.setError("No puedes crear eventos en el pasado");
                }



                if(coord&&nombre&&descripcion&date&&time&&musicType){
                    evento.setName(name.getText().toString());
                    evento.setDescription(description.getText().toString());
                    evento.setLocality(spinner.getSelectedItem().toString());
                    evento.setMusic_type(music.getSelectedItem().toString());
                    FirebaseUser user = mAuth.getCurrentUser();
                    String uid = user.getUid();



                    Map<String, Object> user_add = new HashMap<>();
                    user_add.put("name", evento.getName());
                    user_add.put("music_type",evento.getMusic_type());
                    user_add.put("description",evento.getDescription());
                    user_add.put("locality", evento.getLocality());
                    user_add.put("date", evento.getDate());
                    user_add.put("time",evento.getTime());
                    user_add.put("ubication",evento.getUbication());
                    user_add.put("event_maker",uid);

                    final DocumentReference ref = db.collection("Events").document();
                    ref
                            .set(user_add)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully written!");
                                    Toast.makeText(getApplicationContext(), "Evento creado con éxito",
                                            Toast.LENGTH_SHORT).show();
                                            if(imageUpload){
                                                String id = ref.getId();
                                                storageReference = storage.getReferenceFromUrl("gs://partymaps-51476.appspot.com").child("images/events/"+id);
                                                imatgeEvento.setDrawingCacheEnabled(true);
                                                imatgeEvento.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                                                imatgeEvento.layout(0, 0, imatgeEvento.getMeasuredWidth(), imatgeEvento.getMeasuredHeight());
                                                imatgeEvento.buildDrawingCache();
                                                Bitmap bitmap = Bitmap.createBitmap(imatgeEvento.getDrawingCache());
                                                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                                                byte[] data = outputStream.toByteArray();

                                                UploadTask uploadTask = storageReference.putBytes(data);
                                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception exception) {
                                                    }
                                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                        Toast.makeText(getApplicationContext(), "Imágen subida con éxito",
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }

                                            finish();
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

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            try {
                imageUri = data.getData();
                imageStream = getContentResolver().openInputStream(imageUri);

                selectedImage = BitmapFactory.decodeStream(imageStream);

                selectedImage  = downscaleToMaxAllowedDimension(selectedImage);
                imatgeEvento.setImageBitmap(selectedImage);

                imageUpload=true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(CrearEventoActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(CrearEventoActivity.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }
    private static Bitmap downscaleToMaxAllowedDimension(Bitmap bitmap) {
        int MAX_ALLOWED_RESOLUTION = 1024;
        int outWidth;
        int outHeight;
        int inWidth = bitmap.getWidth();
        int inHeight = bitmap.getHeight();
        if(inWidth > inHeight){
            outWidth = MAX_ALLOWED_RESOLUTION;
            outHeight = (inHeight * MAX_ALLOWED_RESOLUTION) / inWidth;
        } else {
            outHeight = MAX_ALLOWED_RESOLUTION;
            outWidth = (inWidth * MAX_ALLOWED_RESOLUTION) / inHeight;
        }

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, outWidth, outHeight, false);

        return resizedBitmap;
    }


    private boolean checkDatePast(String date){

        Date dateCheck = new Date();
        Date dateActu = new Date();
        SimpleDateFormat format=new SimpleDateFormat("dd/MM/yyyy");
        Calendar fechaActu = Calendar.getInstance();
        try {
            dateCheck = format.parse(date);
            if (dateCheck.before(fechaActu.getTime())){
                return true;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
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
        DialogFragment newFragment = new CrearEventoActivity.TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new CrearEventoActivity.DatePickerFragment();
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
            month++;
            String dia,mes;
            if(day<10){
                dia = "0" + day;
            }else{
                dia = String.valueOf(day);
            }
            if(month<10){
                mes = "0" + month;
            }else{
                mes = String.valueOf(month);
            }
            String fecha = dia + "/" + mes + "/" + year;

            try {
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                Date now = new Date();
                Date eventDate = format.parse(fecha);
                if(!eventDate.after(now)){
                    datePicker.setText("");
                    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("Espera McFly...");
                    alertDialog.setMessage("No se pueden crear eventos en el pasado");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Aceptar",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    datePicker.setText("");
                                }
                            });
                    alertDialog.show();
                }else{
                    date=true;
                    evento.setDate(fecha);
                    datePicker.setText(fecha);

                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

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
        moveToCurrentLocation(latLng);

    }
    static private void moveToCurrentLocation(LatLng currentLocation)
    {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,15));
        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 155, null);

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
            String hora,minuts;
            if(hourOfDay<10){
                hora = "0" + hourOfDay;
            }else{
                hora = String.valueOf(hourOfDay);
            }
            if(minute<10){
                minuts = "0" + minute;
            }else{
                minuts = String.valueOf(minute);
            }
            time = true;
            hora = hora + ":" + minuts;
            evento.setTime(hora);
            timePicker.setText(hora);

        }
    }

}

