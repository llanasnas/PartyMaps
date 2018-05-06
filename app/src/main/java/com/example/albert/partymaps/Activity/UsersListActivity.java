package com.example.albert.partymaps.Activity;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.albert.partymaps.Fragment.ProfileFragment;
import com.example.albert.partymaps.Model.User;
import com.example.albert.partymaps.R;
import com.example.albert.partymaps.Util.UserAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.util.ArrayList;

public class UsersListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, SearchView.OnQueryTextListener{

    private ArrayList<User> users = new ArrayList<User>();
    private ListView listView;
    private SearchView mSearchView;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listView = (ListView) findViewById(R.id.list_view_users);
        listView.setOnItemClickListener(this);
        mSearchView = findViewById(R.id.searchView);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        listView.setTextFilterEnabled(true);
        setupSearchView();
        db.collection("Users").orderBy("name").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (DocumentSnapshot document : task.getResult()){
                        if(!document.getId().equals(mAuth.getUid())) {
                            User user = new User(document.getString("name"), document.getString("mail"), document.getString("date"));
                            user.setUid(document.getId());
                            users.add(user);
                        }
                    }

                    UserAdapter userAdapter = new UserAdapter(getApplicationContext(),users);
                    listView.setAdapter(userAdapter);
                }
            }
        });
    }
    private void setupSearchView()
    {
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setQueryHint("Buscar PartyMappers");
    }

    @Override
    public boolean onQueryTextChange(String newText)
    {

        if (TextUtils.isEmpty(newText)) {
            listView.clearTextFilter();
        } else {
            listView.setFilterText(newText);
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query)
    {
        return false;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


        User user = users.get(position);
        ProfileFragment profileFragment = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putString("activity","UsersListActivity");
        bundle.putParcelable("user",user);
        profileFragment.setArguments(bundle);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().
                replace(R.id.userList,profileFragment).
                addToBackStack(null).
                commit();


    }
}
