package com.example.albert.partymaps.Fragment;


import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.albert.partymaps.Activity.CrearEventoActivity;
import com.example.albert.partymaps.Activity.UsersDataChange;
import com.example.albert.partymaps.Model.User;
import com.example.albert.partymaps.R;
import com.example.albert.partymaps.Util.UserAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListUsers extends Fragment implements AdapterView.OnItemClickListener, SearchView.OnQueryTextListener,UsersDataChange {

    private ArrayList<User> users = new ArrayList<User>();
    private ListView listView;
    private SearchView mSearchView;
    private UserAdapter userAdapter;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ListUsers() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_list_users, container, false);
        listView = (ListView) view.findViewById(R.id.list_view_users);
        listView.setOnItemClickListener(this);
        mSearchView = view.findViewById(R.id.searchView);
        listView.setTextFilterEnabled(true);
        setupSearchView();
        if (getArguments().getString("seguidos").equals("seguidos")) {
            setSeguidos();
        }else if(getArguments().getString("activity").equals("profile")){
            setSeguidos();
        }else{
            setAllUsers();
        }
        
        return view;
    }
    private void setSeguidos(){

        users = getArguments().getParcelableArrayList("users");
        userAdapter = new UserAdapter(getActivity(),getActivity().getApplicationContext(),users);
        listView.setAdapter(userAdapter);

    }

    private void setAllUsers(){

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

                    userAdapter = new UserAdapter(getActivity(),getActivity().getApplicationContext(),users);
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        users = userAdapter.getUsers();
        User user = users.get(position);
        ProfileFragment profileFragment = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putString("activity","UsersListActivity");
        bundle.putParcelable("user",user);
        profileFragment.setArguments(bundle);


        getFragmentManager().beginTransaction().
                replace(R.id.userList,profileFragment).
                addToBackStack(null).
                commit();


    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }



    @Override
    public void changeData(ArrayList<User> users) {

    }
}
