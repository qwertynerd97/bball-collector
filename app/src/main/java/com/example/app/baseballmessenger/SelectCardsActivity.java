package com.example.app.baseballmessenger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by pr4h6n on 3/3/18.
 */

// TODO Add functionality to CollectionActivity class and delete
public class SelectCardsActivity extends AppCompatActivity {

    ListView cardsList;
    ArrayList<String> al;
    DatabaseReference ref;
    User user;
    HashMap<String, Card> cards;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectcards);

        cardsList = (ListView)findViewById(R.id.cardsList);
        al = new ArrayList<>();
        cards = new HashMap<>();
        user = getIntent().getParcelableExtra("user");

        if(getIntent().getStringExtra("selection_mode").equals("sent"))
        {
            //Load current user's card collection
            ref = Card.databaseReference(Handoff.currentUser.uuid, true);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot cardSnapshot: dataSnapshot.getChildren())
                    {
                        Card c = cardSnapshot.getValue(Card.class);
                        if(!c.lockstatus)
                        {
                            al.add(c.name);
                            cards.put(c.name, c);
                        }
                        cardsList.setAdapter(new ArrayAdapter<String>(SelectCardsActivity.this, android.R.layout.simple_list_item_1, al));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else
        {
            //Load receiving user's card collection
            ref = Card.databaseReference(user.uuid, true);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot cardSnapshot: dataSnapshot.getChildren())
                    {
                        Card c = cardSnapshot.getValue(Card.class);
                        if(!c.lockstatus)
                        {
                            al.add(c.name);
                            cards.put(c.name, c);
                        }
                        cardsList.setAdapter(new ArrayAdapter<String>(SelectCardsActivity.this, android.R.layout.simple_list_item_1, al));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        //Adds selected card to ArrayList storing all selected cards
        cardsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(getIntent().getStringExtra("selection_mode").equals("sent"))
                {
                    Intent i = new Intent(SelectCardsActivity.this, NewTradeActivity.class);
                    i.putExtra("user", user);
                    NewTradeActivity.sentCard = cards.get(al.get(position));
                    startActivity(i);
                }
                else
                {
                    Intent i = new Intent(SelectCardsActivity.this, NewTradeActivity.class);
                    i.putExtra("user", user);
                    NewTradeActivity.requestedCard = cards.get(al.get(position));
                    startActivity(i);
                }
            }
        });
    }

}
