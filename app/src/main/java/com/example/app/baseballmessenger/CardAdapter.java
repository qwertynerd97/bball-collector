package com.example.app.baseballmessenger;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by elli on 3/15/18.
 */

public class CardAdapter extends ArrayAdapter {
    ArrayList<Card> cards;
    int resourceId;

    public CardAdapter(Context context, int textViewResourceId, ArrayList<Card> objects) {
        super(context, textViewResourceId, objects);
        cards = objects;
        resourceId = textViewResourceId;
    }

    public CardAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        cards = new ArrayList<Card>();
        //cards.add(new Card("0", "Babe Ruth", "New", "01", "Batter", "Yankees", 100.00, "1963", "2017", "wishlist"));
        //cards.add(new Card("01", "Elli Howard", "New", "01", "Batter", "Yankees", 100.00, "1963", "2017", "wishlist"));
        //cards.add(new Card("02", "Blah", "New", "01", "Batter", "Yankees", 100.00, "1963", "2017", "wishlist"));
        //cards.add(new Card("03", "Dummy", "New", "01", "Batter", "Yankees", 100.00, "1963", "2017", "wishlist"));
        //cards.add(new Card("04", "Troy Wildcat", "New", "01", "Batter", "Yankees", 100.00, "1963", "2017", "wishlist"));
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
        TextView userName = v.findViewById(R.id.user_name);
        //userName.setText(cards.get(position).name);
        return v;
    }
}
