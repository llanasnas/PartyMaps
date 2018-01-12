package com.example.albert.partymaps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);




        TextView tornar = (TextView) findViewById(R.id.link_login);


        AppCompatButton submit = (AppCompatButton) findViewById(R.id.btn_signup);




        submit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                boolean mail=false,pass=false,repass=true,name=false,fecha=false;
                EditText correo = (EditText) findViewById(R.id.input_email);
                EditText password = (EditText) findViewById(R.id.input_password);
                EditText repassword = (EditText) findViewById(R.id.input_repassword);
                EditText nombre = (EditText) findViewById(R.id.input_name);
                EditText fecha_nacimiento = (EditText) findViewById(R.id.date);
                //Validem les dades del registre -->>>
                if (!validarEmail(String.valueOf(correo.getText()))){
                    correo.setError("Email no válido");
                }else{mail=true;}
                if(!validarNombre(String.valueOf(nombre.getText()))){
                    nombre.setError("Este campo no puede estar vacio");
                }else{name=true;}
                if(!validarPassword(String.valueOf(password.getText()))){
                    password.setError("Formato de password incorrecto");
                }else{pass=true;}
                if(repassword.getText().equals(password.getText())){
                    repass=true;
                }

            }
        });



        tornar.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);

            }
        });


    }
    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }
    private boolean validarNombre(String nombre){

        if(nombre.length()==0){
            return false;
        }
        else{
            return true;
        }
    }
    public static boolean validarPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }


}
