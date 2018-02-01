package com.example.albert.partymaps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.facebook.login.LoginManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button crear_evento = (Button) findViewById(R.id.crear_evento);
        Button buscar_evento = (Button) findViewById(R.id.buscar_evento);
        Button cerrar_sesion = (Button) findViewById(R.id.logout);


        crear_evento.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getBaseContext(), MapsActivity.class);
                startActivity(intent);

            }
        });
        buscar_evento.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {



            }
        });

        cerrar_sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logOut();
                finish();
            }
        });



    }
}
