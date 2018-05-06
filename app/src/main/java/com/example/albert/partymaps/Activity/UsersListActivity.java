package com.example.albert.partymaps.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

import com.example.albert.partymaps.Model.User;
import com.example.albert.partymaps.R;
import com.example.albert.partymaps.Util.UserAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.util.ArrayList;

public class UsersListActivity extends AppCompatActivity {

    private ArrayList<User> users = new ArrayList<User>();
    private ListView listView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {

        db.collection("Users").orderBy("name").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (DocumentSnapshot document : task.getResult()){

                        User user = new User(document.getString("name"),document.getString("mail"),document.getString("date"));
                        user.setUid(document.getId());
                        users.add(user);

                    }
                    listView = (ListView) findViewById(R.id.list_view_users);
                    UserAdapter userAdapter = new UserAdapter(getApplicationContext(),users);
                    listView.setAdapter(userAdapter);

                }
            }
        });

        return super.onCreateView(name, context, attrs);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }


}
