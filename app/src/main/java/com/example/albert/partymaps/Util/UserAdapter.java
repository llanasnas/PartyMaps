package com.example.albert.partymaps.Util;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.albert.partymaps.Model.User;
import com.example.albert.partymaps.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Alumne on 04/05/2018.
 */

public class UserAdapter extends BaseAdapter implements Filterable{




    FirebaseStorage storage;
    StorageReference storageReference;
    private Context context;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ArrayList<User> users = new ArrayList<User>();
    private ArrayList<User> usersAux = new ArrayList<User>();

    public UserAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }



    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        }
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        final ImageView imatge = (ImageView) view.findViewById(R.id.imatge_user);
        TextView nom = (TextView) view.findViewById(R.id.nomUsuari);

        nom.setText(users.get(position).getName());

            storageReference.child("images/" + users.get(position).getUid().concat("/profileimage")).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                           Picasso.get().load(task.getResult()).into(imatge);
                    }
                }
            });

        return view;
    }
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<User> results = new ArrayList<User>();
                if (usersAux == null)
                    usersAux = users;
                if (constraint != null) {
                    if (usersAux != null && usersAux.size() > 0) {
                        for (final User g : usersAux) {
                            if (g.getName().toLowerCase()
                                    .contains(constraint.toString()))
                                results.add(g);
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                users = (ArrayList<User>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
