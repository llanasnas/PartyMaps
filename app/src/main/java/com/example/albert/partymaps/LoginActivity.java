package com.example.albert.partymaps;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_login);
        TextView logo = (TextView) findViewById(R.id.logo);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/RubikMonoOne-Regular.ttf");
        logo.setTypeface(font);
        final EditText emailInput = (EditText) findViewById(R.id.user);
        final EditText passwordInput = (EditText) findViewById(R.id.password);
        Button submit = (Button) findViewById(R.id.submit);
        TextView createAccount = (TextView) findViewById(R.id.create);

        createAccount.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                    Intent intent = new Intent(getBaseContext(), RegisterActivity.class);
                    startActivity(intent);

            }
        });
        submit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString();
                final String password = passwordInput.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }



                //authenticate user
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.

                                if (task.isSuccessful()) {
                                    // there was an error
                                    Toast.makeText(LoginActivity.this, "Has entrao pescao", Toast.LENGTH_SHORT).show();;

                                } else {
                                    Toast.makeText(LoginActivity.this, "Email o contraseña incorrecta", Toast.LENGTH_SHORT).show();;
                                }
                            }
                        });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }
    /*private void updateUI(FirebaseUser user) {
        if (user != null) {
            ((TextView) findViewById(R.id.text_sign_in_status)).setText(
                    "User ID: " + user.getUid());
        } else {
            ((TextView) findViewById(R.id.text_sign_in_status)).setText(
                    "Error: sign in failed.");
        }
    }*/


    @Override
    public void onClick(View view) {


        EditText user = (EditText) view.findViewById(R.id.user);
        EditText password = (EditText) view.findViewById(R.id.password);
        String contrasenya = password.getText().toString();
        String usuari = user.getText().toString();
    }
}

