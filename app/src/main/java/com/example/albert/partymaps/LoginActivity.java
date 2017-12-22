package com.example.albert.partymaps;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TextView logo = (TextView) findViewById(R.id.logo);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/RubikMonoOne-Regular.ttf");
        logo.setTypeface(font);
        EditText user = (EditText) findViewById(R.id.user);
        EditText password = (EditText) findViewById(R.id.password);
        Button submit = (Button) findViewById(R.id.submit);
        TextView createAccount = (TextView) findViewById(R.id.create);


        createAccount.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                    Intent intent = new Intent(getBaseContext(), RegisterActivity.class);
                    startActivity(intent);

            }
        });


    }


    @Override
    public void onClick(View view) {


        EditText user = (EditText) view.findViewById(R.id.user);
        EditText password = (EditText) view.findViewById(R.id.password);
        String contrasenya = password.getText().toString();
        String usuari = user.getText().toString();
    }
}

