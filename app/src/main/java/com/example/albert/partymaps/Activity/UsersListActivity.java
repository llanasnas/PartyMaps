package com.example.albert.partymaps.Activity;


import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import com.example.albert.partymaps.Fragment.ListUsers;
import com.example.albert.partymaps.R;

public class UsersListActivity extends AppCompatActivity {

    private ListUsers listUsers = new ListUsers();






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        bundle.putString("activity",getClass().getSimpleName());
        listUsers.setArguments(bundle);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().add(R.id.userList, listUsers).
                commit();



    }



}
