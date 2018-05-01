package com.example.albert.partymaps.Fragment;


import android.Manifest;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.albert.partymaps.Activity.BuscarEventoActivity;
import com.example.albert.partymaps.Activity.RegisterActivity;
import com.example.albert.partymaps.Model.Event;
import com.example.albert.partymaps.Util.GPSTracker;
import com.example.albert.partymaps.R;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 */
public class BuscarEventoFragment extends Fragment  {

    private ArrayList<Event> events = new ArrayList<Event>();
    private ArrayList<Event> eventos = new ArrayList<Event>();
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FusedLocationProviderClient mFusedLocationClient;
    GoogleApiClient client;
    LocationRequest mLocationRequest;
    PendingResult<LocationSettingsResult> result;
    static final Integer GPS_SETTINGS = 0x7;
    static final Integer LOCATION = 0x1;



    public BuscarEventoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = new GoogleApiClient.Builder(getActivity())
                .addApi(AppIndex.API)
                .addApi(LocationServices.API)
                .build();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_buscar_evento, container, false);

        final EditText nombre = (EditText) view.findViewById(R.id.bpn);
        final Spinner localities = (Spinner) view.findViewById(R.id.buscar_por_localidad);
        final Spinner musicTypes = (Spinner) view.findViewById(R.id.estilo_de_musica);
        final SeekBar kilometros = view.findViewById(R.id.kilometros);
        final Switch activarGPS = view.findViewById(R.id.buscar_distancia);
        final TextView numeroKilometros = view.findViewById(R.id.numero_kilometros);
        numeroKilometros.setText("Kilometros: 1");
        String[] arraySpinner = getResources().getStringArray(R.array.cities);
        ArrayAdapter<String> adapterLocalities = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item,
                arraySpinner);
        localities.setAdapter(adapterLocalities);
        String[] arraymusic = getResources().getStringArray(R.array.music_styles);
        ArrayAdapter<String> adapterMusica = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item,
                arraymusic);
        events = getActivity().getIntent().getParcelableArrayListExtra("events");
        musicTypes.setAdapter(adapterMusica);


        activarGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activarGPS.isChecked()){
                    askForPermission(Manifest.permission.ACCESS_FINE_LOCATION,LOCATION);
                }
            }
        });



        kilometros.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                numeroKilometros.setText("Kilometros: " + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        AppCompatButton buscar = (AppCompatButton) view.findViewById(R.id.buscar_boton);

        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (nombre.getText().toString().isEmpty() && musicTypes.getSelectedItem().toString().equals("Estilo musical") && localities.getSelectedItem().toString().equals("Localidad") && !activarGPS.isChecked()) {

                    nombre.setError("Busca por nombre, localidad, estilo musical, o distancia ");

                }else if((activarGPS.isChecked() && !BuscarEventoActivity.devolverPermisos())){

                    nombre.setError("Para buscar por distancia debes aceptar los permisos");

                }else {

                    String nom = "", localidad = "", musica = "";

                    if (!nombre.getText().toString().isEmpty()) {
                        nom = nombre.getText().toString();
                    }
                    if (!localities.getSelectedItem().toString().equals("Localidad")) {
                        localidad = localities.getSelectedItem().toString();
                    }
                    if (!musicTypes.getSelectedItem().toString().equals("Estilo musical")) {
                        musica = musicTypes.getSelectedItem().toString();
                    }
                    Boolean added;
                    for (Event event : events) {
                        added = false;
                        while (!added) {
                            if (!nom.equals("")) {
                                if (event.getName().contains(nom)) {
                                    eventos.add(event);
                                    break;
                                }
                            }
                            if (!localidad.equals("")) {
                                if (event.getLocality().equals(localidad)) {
                                    eventos.add(event);
                                    break;
                                }
                            }
                            if(!musica.equals("")){
                                if(event.getMusic_type().equals(musica)){
                                    eventos.add(event);
                                    break;
                                }
                            }

                            if (activarGPS.isChecked() && BuscarEventoActivity.devolverPermisos()){
                                GPSTracker gpsTracker = new GPSTracker(getApplicationContext());
                                Float distanciaMaxima = Float.parseFloat(numeroKilometros.getText().toString().substring(11,numeroKilometros.getText().toString().length()))*1000;
                                if (gpsTracker.getLocation() != null){
                                    Location locationUser = gpsTracker.getLocation();
                                    Location locationEvent = new Location("evento");
                                    String ubicacionEvento = event.getUbication();

                                    ubicacionEvento = ubicacionEvento.replaceAll("lat/lng:","");

                                    ubicacionEvento = ubicacionEvento.substring(2,ubicacionEvento.length()-1);

                                    String[] latLong = ubicacionEvento.split(",");

                                    locationEvent.setLatitude(Float.parseFloat(latLong[0]));

                                    locationEvent.setLongitude(Float.parseFloat(latLong[1]));

                                    Float distancia = locationUser.distanceTo(locationEvent);

                                    if (distancia <= distanciaMaxima){
                                        eventos.add(event);
                                        break;
                                    }
                                }
                            }
                            added = true;
                        }
                    }
                    if (eventos.isEmpty()){
                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                        alertDialog.setTitle("Vaya...");
                        alertDialog.setMessage("No se han encontrado eventos con tus criterios de búsqueda");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Aceptar",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }else{
                        ListFragment listFragment = new ListFragment();
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList("eventos",eventos);
                        bundle.putString("activity","BuscarEvento");
                        listFragment.setArguments(bundle);
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().
                                replace(R.id.buscar_eventos, listFragment).
                                addToBackStack(null).
                                commit();
                    }

                }
            }
        });
        return view;
    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (android.support.v4.app.ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                android.support.v4.app.ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, requestCode);

            } else {

                android.support.v4.app.ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, requestCode);
            }
        } else {
            Toast.makeText(getActivity(), "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }
}
