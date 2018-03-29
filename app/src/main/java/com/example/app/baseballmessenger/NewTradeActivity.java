package com.example.app.baseballmessenger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by pr4h6n on 3/3/18.
 */


public class NewTradeActivity extends AppCompatActivity {

    Button selectUserButton;
    TextView selectedUser;
    Button selectCardsReceivedButton;
    Button selectCardsSentButton;
    ListView cardsSentList;
    ListView cardsReceivedList;
    Button proposeTradeButton;
    User selectedUserObj;
    static Card sentCard = new Card("", "", "", "", 0, "", "", 0.0, 0, "", true, false);
    static Card requestedCard = new Card("", "", "", "", 0, "", "", 0.0, 0, "", true, false);


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newtrade);

        Firebase.setAndroidContext(this);

        proposeTradeButton = (Button)findViewById(R.id.proposeTradeButton);

        selectUserButton = (Button)findViewById(R.id.selectUserButton);
        selectedUser = (TextView)findViewById(R.id.userText);

        selectCardsReceivedButton = (Button)findViewById(R.id.selectCardsTwo);
        selectCardsSentButton = (Button)findViewById(R.id.selectCardsOne);

        cardsSentList = (ListView)findViewById(R.id.cards_sent);
        cardsReceivedList = (ListView)findViewById(R.id.cards_received);

        selectedUserObj = getIntent().getParcelableExtra("user");

        if(selectedUserObj != null) {
            selectedUser.setText(selectedUserObj.email);
        }
        else
        {
            selectedUser.setText("Select user...");
            selectedUserObj = new User();
        }

        ArrayList<String> al = new ArrayList<>();
        al.add(sentCard.name);
        cardsSentList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, al));

        ArrayList<String> al2 = new ArrayList<>();
        al2.add(requestedCard.name);
        cardsReceivedList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, al2));

        selectUserButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(NewTradeActivity.this, SearchUsersActivity.class);
                i.putExtra("previous_activity", "NewTradeActivity");
                startActivity(i);
                sentCard = new Card("", "", "", "", 0, "", "", 0.0, 0, "", true, false);
                requestedCard = new Card("", "", "", "", 0, "", "", 0.0, 0, "", true, false);
            }
        });

        selectCardsSentButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(NewTradeActivity.this, SelectCardsActivity.class);
                i.putExtra("selection_mode", "sent");
                i.putExtra("user", selectedUserObj);
                startActivity(i);
            }
        });

        selectCardsReceivedButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(NewTradeActivity.this, SelectCardsActivity.class);
                i.putExtra("selection_mode", "requested");
                i.putExtra("user", selectedUserObj);
                startActivity(i);
            }
        });

        proposeTradeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                if(selectedUserObj.uuid.equals(Handoff.currentUser.uuid))
                {
                    Toast.makeText(NewTradeActivity.this, "Cannot trade with yourself. Please choose another user",
                            Toast.LENGTH_LONG).show();
                }
                else if(sentCard.uuid.equals("") || requestedCard.uuid.equals(""))
                {
                    Toast.makeText(NewTradeActivity.this, "Trades cannot be one-sided. Please choose a card to send and receive",
                            Toast.LENGTH_LONG).show();
                }
                else
                {
                    RandomString random = new RandomString();
                    Trade trade = new Trade(random.nextString(), Handoff.currentUser.uuid, selectedUserObj.uuid, sentCard.uuid, requestedCard.uuid, 0);
                    trade.updateFirebase();
                    startActivity(new Intent(NewTradeActivity.this, TradeListActivity.class));
                }
            }
        });

    }
}
