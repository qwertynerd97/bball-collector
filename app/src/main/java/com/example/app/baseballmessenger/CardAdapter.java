package com.example.app.baseballmessenger;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

/**
 * Created by elli on 3/15/18.
 */

public class CardAdapter extends ArrayAdapter {
    ArrayList<Card> cards;
    int resourceId;
    Context context;

    public CardAdapter(Context context, int textViewResourceId, ArrayList<Card> objects) {
        super(context, textViewResourceId, objects);
        cards = objects;
        this.context = context;
        resourceId = textViewResourceId;
    }

    public CardAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        cards = new ArrayList<Card>();
        cards.add(new Card("0", "Babe Ruth","tet", "New", 1, "Batter", "Yankees", 100.00, 1963, "2017", false, false, "cardDefault.jpg"));
        cards.add(new Card("01", "Elli Howard","test", "New", 1, "Batter", "Yankees", 100.00, 1963, "2017", false, false, "cardDefault.jpg"));
        cards.add(new Card("02", "Blah", "tett","New", 1, "Batter", "Yankees", 100.00, 1963, "2017", false, false, "cardDefault.jpg"));
        cards.add(new Card("03", "Dummy", "stts","New", 1, "Batter", "Yankees", 100.00, 1963, "2017", false, false, "cardDefault.jpg"));
        cards.add(new Card("04", "Troy Wildcat", "tse","New", 1, "Batter", "Yankees", 100.00, 1963, "2017", false, false, "cardDefault.jpg"));
        resourceId = textViewResourceId;
    }

    @Override
    public int getCount() {
        return cards.size();
    }

    @Override
    public Object getItem(int position) {
        return cards.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(resourceId, null);
        final ImageView pic = v.findViewById(R.id.cardImage);
        final long ONE_MEGABYTE = 1024 * 1024;
        cards.get(position).imageRef().getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                pic.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
        TextView userName = v.findViewById(R.id.cardName);
        userName.setText(cards.get(position).name);
        return v;
    }
}
