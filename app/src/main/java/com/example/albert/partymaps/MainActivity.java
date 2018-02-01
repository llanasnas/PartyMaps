package com.example.albert.partymaps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button crear_evento = (Button) findViewById(R.id.crear_evento);
        Button buscar_evento = (Button) findViewById(R.id.buscar_evento);


        crear_evento.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {



            }
        });
        buscar_evento.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {



            }
        });



    }
}
