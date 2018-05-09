package com.example.albert.partymaps.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.albert.partymaps.Model.Event;
import com.example.albert.partymaps.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Gerard on 04/03/2018.
 */

public class EventAdapter extends BaseAdapter {

    private ArrayList<Event> events;
    private Context context;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference();
    private ImageView mImageView;

    public EventAdapter(ArrayList<Event> events, Context context) {
        this.events = events;
        this.context = context;
    }


    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Object getItem(int i) {
        return events.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_ayout, viewGroup, false);
        }
        Event event = events.get(i);
        TextView name = (TextView) view.findViewById(R.id.nombre_evento);
        TextView day = (TextView) view.findViewById(R.id.dia);
        ImageView imagen = (ImageView) view.findViewById(R.id.imagen);
        TextView month = (TextView) view.findViewById(R.id.mes);

        String imageName = event.getMusic_type();
        imageName = imageName.replaceAll(" ","_").toLowerCase();
        /*if(!imageName.equals("otros..")){
            int id_image = context.getResources().getIdentifier(imageName , "drawable", context.getPackageName());
            imagen.setImageResource(id_image);
        }*/
        SimpleDateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dateAsString = event.getDate();


        try {
            Date date = sourceFormat.parse(dateAsString);
            String mes = new SimpleDateFormat("MMM").format(date).toUpperCase();
            month.setText(mes);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        String day_final  = event.getDate().substring(0,2);
        day_final = day_final.replace("/","");
        day.setText(day_final);
        /*final long ONE_MEGABYTE = 1024 * 1024;
        storageReference = storage.getReferenceFromUrl("gs://partymaps-51476.appspot.com").child("images/events/"+event.getId());
        mImageView = (ImageView) view.findViewById(R.id.imagen);

        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                mImageView.setImageBitmap(bitmap);
            }
        });*/
        //setImage(event,mImageView);
        name.setText(event.getName());
        TextView locality = (TextView) view.findViewById(R.id.localidad);
        locality.setText(String.valueOf(event.getLocality()));
        return view;
    }

    public void setImage(Event event, final ImageView mImageView){


        final long ONE_MEGABYTE = 1024 * 1024;
        storageReference = storage.getReferenceFromUrl("gs://partymaps-51476.appspot.com").child("images/events/"+event.getId());

        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                mImageView.setImageBitmap(bitmap);
            }
        });
    }


}
