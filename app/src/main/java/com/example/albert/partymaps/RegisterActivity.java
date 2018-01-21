package com.example.albert.partymaps;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        mAuth = FirebaseAuth.getInstance();

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
                if (!validarEmail(correo.getText().toString())){
                    correo.setError("Email no válido");
                }else{mail=true;}
                if(!validarNombre(nombre.getText().toString())){
                    nombre.setError("Este campo no puede estar vacio");
                }else{name=true;}
                if(password.getText().toString().length()<8 &&!validarPassword(password.getText().toString())){
                    password.setError("contraseña no válida");
                }else{pass=true;}
                if(repassword.getText().toString().equals(password.getText().toString())){
                    repass=true;
                }else{
                    repassword.setError("No coinciden los passwords");
                }if(fecha_nacimiento.getText().toString().length()==0){fecha_nacimiento.setError("Fecha no válida");}else{fecha=true;}



                 if (mail&&pass&repass&name&fecha){
                     final user usuario = new user(nombre.getText().toString(),correo.getText().toString(),fecha_nacimiento.getText().toString());
                     mAuth.createUserWithEmailAndPassword(correo.getText().toString(), password.getText().toString())
                             .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                 @Override
                                 public void onComplete(@NonNull Task<AuthResult> task) {
                                     if (task.isSuccessful()) {
                                         // Sign in success, update UI with the signed-in user's information
                                         Toast.makeText(getApplicationContext(), "Te has registrao pescao.",
                                                 Toast.LENGTH_SHORT).show();
                                         Log.d(TAG, "createUserWithEmail:success");
                                         FirebaseUser user = mAuth.getCurrentUser();
                                         updateUI(user);
                                         String uid = user.getUid();


                                         Map<String, Object> user_add = new HashMap<>();
                                         user_add.put("name", usuario.getName());
                                         user_add.put("mail",usuario.getMail());
                                         user_add.put("date", usuario.getDate());


                                         db.collection("users").document(uid)
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


                                     } else {
                                         // If sign in fails, display a message to the user.
                                         Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                         Toast.makeText(getApplicationContext(), "Authentication failed.",
                                                 Toast.LENGTH_SHORT).show();
                                         updateUI(null);
                                     }

                                 }
                             });





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






    public void updateUI(FirebaseUser user){





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
