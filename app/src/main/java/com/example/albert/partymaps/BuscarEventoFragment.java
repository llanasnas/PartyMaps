package com.example.albert.partymaps;


import android.app.FragmentManager;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class BuscarEventoFragment extends Fragment {

    private ArrayList<Event> events = new ArrayList<Event>();
    private ArrayList<Event> eventos = new ArrayList<Event>();
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    public BuscarEventoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_buscar_evento, container, false);

        final EditText nombre = (EditText) view.findViewById(R.id.bpn);
        final Spinner localities = (Spinner) view.findViewById(R.id.buscar_por_localidad);
        final Spinner musicTypes = (Spinner) view.findViewById(R.id.estilo_de_musica);


        String[] arraySpinner = getResources().getStringArray(R.array.cities);
        ArrayAdapter<String> adapterLocalities = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item,
                arraySpinner);
        localities.setAdapter(adapterLocalities);
        String[] arraymusic = getResources().getStringArray(R.array.music_styles);
        ArrayAdapter<String> adapterMusica = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item,
                arraymusic);
        events = getActivity().getIntent().getParcelableArrayListExtra("events");
        musicTypes.setAdapter(adapterMusica);

        AppCompatButton buscar = (AppCompatButton) view.findViewById(R.id.buscar_boton);

        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (nombre.getText().toString().isEmpty() && musicTypes.getSelectedItem().toString().equals("Estilo musical") && localities.getSelectedItem().toString().equals("Localidad")) {

                    nombre.setError("Busca por nombre, localidad o estilo musical");

                } else {

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
                            added = true;
                        }
                    }
                }
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
        });
        return view;

    }

}
