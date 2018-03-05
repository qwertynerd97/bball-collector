package com.example.app.baseballmessenger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pr4h6n on 3/3/18.
 */

public class NewTrade extends AppCompatActivity {

    Button selectUserButton;
    TextView selectedUser;
    Button selectCardsReceivedButton;
    Button selectCardsSentButton;
    ListView cardsSentList;
    ListView cardsReceivedList;
    Button proposeTradeButton;
    Firebase reference_one;

    static ArrayList<Card> receivedCards = new ArrayList<>();
    static ArrayList<Card> sentCards = new ArrayList<>();
    static ArrayList<String> receivedCardsAl = new ArrayList<>();
    static ArrayList<String> sentCardsAl = new ArrayList<>();

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

        //Make sure that user has selected the receiver of trade proposal
        if(!UserDetails.selectedUserTrade.equals(""))
        {
            for(String key: UserDetails.hashMap.keySet())
            {
                if(UserDetails.hashMap.get(key).equals(UserDetails.selectedUserTrade))
                {
                    selectedUser.setText(key); //Display the email address instead of the UID
                }
            }
        }
        else
        {
            selectedUser.setText("Select user..."); //Default text
        }

        cardsSentList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sentCardsAl));
        cardsReceivedList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, receivedCardsAl));

        selectUserButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(NewTrade.this, SelectUser.class));
            }
        });

        selectCardsSentButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                //Clear ArrayList containing previously selected cards for new selection
                sentCardsAl.clear();
                sentCards.clear();
                startActivity(new Intent(NewTrade.this, SelectCards.class));
                UserDetails.card_mode = 0; //Set flag - choosing the sent cards
            }
        });

        selectCardsReceivedButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                //Clear ArrayList containing previously selected cards for new selection
                receivedCardsAl.clear();
                receivedCards.clear();
                startActivity(new Intent(NewTrade.this, SelectCards.class));
                UserDetails.card_mode = 1; //Set flag - choosing the received cards
            }
        });

        proposeTradeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {

                //Determine correct order of UIDs in URL (i.e. UIDONE_UIDTWO vs. UIDTWO_UIDONE)
                if(UserDetails.selectedUserTrade.compareTo(UserDetails.currentUser.getUid()) > 0)
                {
                    reference_one = new Firebase("https://baseballmessenger-afdea.firebaseio.com/trades/" + UserDetails.currentUser.getUid() + "_" + UserDetails.selectedUserTrade);
                }
                else
                {
                    reference_one = new Firebase("https://baseballmessenger-afdea.firebaseio.com/trades/" + UserDetails.selectedUserTrade + "_" + UserDetails.currentUser.getUid());
                }

                Map<String, String> temp = new HashMap<>();
                temp.put("user", UserDetails.currentUser.getUid());
                temp.put("trade_id", Integer.toString((int)Math.random()*100 + 1));

                Firebase returnReference = reference_one.push();
                returnReference.setValue(temp);
                String tradeIdentifier = returnReference.getKey();

                Map<String, Object> tradedCard = new HashMap<>();

                for(int i = 0; i < receivedCards.size(); i++)
                {
                    Map<String, String> temp2 = new HashMap<>();
                    temp2.put("condition", receivedCards.get(i).getCondition());
                    temp2.put("date_acquired", receivedCards.get(i).getDateAcquired());
                    temp2.put("name", receivedCards.get(i).getName());
                    temp2.put("number", receivedCards.get(i).getNumber());
                    temp2.put("owner", UserDetails.selectedUserTrade);
                    temp2.put("role", receivedCards.get(i).getRole());
                    temp2.put("team", receivedCards.get(i).getTeam());
                    temp2.put("value", Integer.toString((int)(receivedCards.get(i).getValue())));
                    temp2.put("year", receivedCards.get(i).getYear());
                    tradedCard.put(Integer.toString((int)Math.round(Math.random()*100 + 1)), temp2); //Card identifier, data object
                }
                reference_one.child(tradeIdentifier).child("cards_received").setValue(tradedCard); //Push all cards to Firebase

                tradedCard = new HashMap<>();

                for(int i = 0; i < sentCards.size(); i++)
                {
                    Map<String, Object> temp2 = new HashMap<>();
                    temp2.put("condition", sentCards.get(i).getCondition());
                    temp2.put("date_acquired", sentCards.get(i).getDateAcquired());
                    temp2.put("name", sentCards.get(i).getName());
                    temp2.put("number", sentCards.get(i).getNumber());
                    temp2.put("owner", UserDetails.currentUser.getUid());
                    temp2.put("role", sentCards.get(i).getRole());
                    temp2.put("team", sentCards.get(i).getTeam());
                    temp2.put("value", Integer.toString((int)(sentCards.get(i).getValue())));
                    temp2.put("year", sentCards.get(i).getYear());
                    tradedCard.put(Integer.toString((int)Math.round(Math.random()*100 +1)), temp2); //Card identifier, data object

                    UserDetails.db.cardDAO().delete(sentCards.get(i).getName(), sentCards.get(i).getNumber()); //TODO Eliminate local database for Firebase cloud storage
                }

                //Push all cards to Firebase - waits for push() to complete before starting activity
                reference_one.child(tradeIdentifier).child("cards_sent").setValue(tradedCard, new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        syncFirebaseWithDatabase(); //TODO Eliminate local database for Firebase cloud storage
                        startActivity(new Intent(NewTrade.this, Trades.class));
                    }
                });
            }
        });

    }


    //Pushes contents of local database to Firebase cloud storage - overwrites existing data in cloud with contents of local database
    //TODO Eliminate local database for Firebase cloud storage
    public void syncFirebaseWithDatabase()
    {
        Firebase reference = new Firebase("https://baseballmessenger-afdea.firebaseio.com/users/" + UserDetails.currentUser.getUid());

        Map<String, Object> newCard = new HashMap<>();
        List<Card> receivedCards = UserDetails.db.cardDAO().getAll();
        for(int i = 0; i < receivedCards.size(); i++)
        {
            Map<String, String> temp2 = new HashMap<>();
            temp2.put("condition", receivedCards.get(i).getCondition());
            temp2.put("date_acquired", receivedCards.get(i).getDateAcquired());
            temp2.put("name", receivedCards.get(i).getName());
            temp2.put("number", receivedCards.get(i).getNumber());
            temp2.put("owner", UserDetails.currentUser.getUid());
            temp2.put("role", receivedCards.get(i).getRole());
            temp2.put("team", receivedCards.get(i).getTeam());
            temp2.put("value", Integer.toString((int)(receivedCards.get(i).getValue())));
            temp2.put("year", receivedCards.get(i).getYear());
            newCard.put(Integer.toString((int)Math.round(Math.random()*100 + 1)), temp2); //Card identifier, data object
        }
        reference.child("cards").setValue(newCard);
    }
}
