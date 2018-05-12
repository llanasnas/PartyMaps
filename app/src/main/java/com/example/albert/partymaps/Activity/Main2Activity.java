package com.example.albert.partymaps.Activity;

import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.albert.partymaps.Model.Event;
import com.example.albert.partymaps.Fragment.ListFragment;
import com.example.albert.partymaps.Fragment.ProfileFragment;
import com.example.albert.partymaps.Model.User;
import com.example.albert.partymaps.R;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListFragment listFragment;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageReference = storage.getReference();
    ArrayList<User> users = new ArrayList<User>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Inicio");
        db.collection("Users").document(FirebaseAuth.getInstance().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                DocumentSnapshot doc = task.getResult();
                NavigationView mnavigationView = (NavigationView) findViewById(R.id.nav_view);
                View header = mnavigationView.getHeaderView(0);
                TextView nameNavigation = (TextView) header.findViewById(R.id.nom_navigation);
                TextView emailNavigation = (TextView) header.findViewById(R.id.email_navigation);
                final ImageView imatgePerfil = (ImageView) header.findViewById(R.id.imatge_navigation);
                nameNavigation.setText(doc.getString("name"));
                emailNavigation.setText(doc.getString("mail"));
                storageReference.child("images/profile/" + FirebaseAuth.getInstance().getUid()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Picasso.get().load(task.getResult()).into(imatgePerfil);
                        }
                    }
                });
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        listFragment = new ListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("activity","Main2Activity");
        listFragment.setArguments(bundle);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().
                add(R.id.main, listFragment).
                commit();

    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onRestart()
    {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_buscar) {
            ArrayList<Event> listEvents = listFragment.getEvents();
            Intent intent = new Intent(getBaseContext(),BuscarEventoActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("events",listEvents); // Be sure con is not null here
            intent.putExtras(bundle);
            startActivity(intent);
        } else if (id == R.id.nav_crear) {

            Intent intent = new Intent(getBaseContext(), CrearEventoActivity.class);
            startActivity(intent);
        }else if(id == R.id.nav_misEventos){

            Intent intent = new Intent(getBaseContext(),MisEventosActivity.class);
            Bundle bundle = new Bundle();// Be sure con is not null here
            intent.putExtras(bundle);
            startActivity(intent);


        }else if (id == R.id.usuarios) {

            Intent intent = new Intent(getBaseContext(),UsersListActivity.class);
            startActivity(intent);

        }else if (id == R.id.nav_amigos) {


            db.collection("Users").document(FirebaseAuth.getInstance().getUid()).collection("seguidos").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){

                        for(DocumentSnapshot document: task.getResult()){


                            db.collection("Users").document(document.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        User user = new User(document.getString("name"), document.getString("mail"), document.getString("date"));
                                        user.setUid(document.getId());
                                        users.add(user);
                                    }
                                    Bundle bundle = new Bundle();
                                    bundle.putParcelableArrayList("users",users);
                                    bundle.putString("seguidos","seguidos");
                                    Intent intent = new Intent(getBaseContext(),UsersListActivity.class);
                                    intent.putExtras(bundle);
                                    startActivity(intent);

                                }
                            });
                        }

                    }
                }
            });


        }else if (id == R.id.favoritos) {

            Intent intent = new Intent(getBaseContext(),FavoritosActivity.class);
            Bundle bundle = new Bundle();// Be sure con is not null here
            intent.putExtras(bundle);
            startActivity(intent);


        }else if (id == R.id.nav_perfil) {

            ProfileFragment fragment = new ProfileFragment();
            FragmentManager fragmentManager = getFragmentManager();
            Bundle bundle = new Bundle();
            bundle.putString("activity","Main2Activity");
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction().
                    addToBackStack(null).
                    add(R.id.main, fragment).
                    commit();

        } else if (id == R.id.nav_cerrar) {
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
            this.finish();
        } else if (id == R.id.nav_send) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
