package com.example.app.baseballmessenger;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by pr4h6n on 2/27/18.
 */

public class SingleTradeActivity extends AppCompatActivity {

    ListView cardsReceivedList;
    ListView cardsSentList;

    Button acceptButton;
    Button rejectButton;

    ArrayList<String> cards_received_al = new ArrayList<>();
    ArrayList<String> cards_sent_al = new ArrayList<>();

    //Stores the JSON objects for easy reuse
    HashMap<String, String> cards_received_al_json = new HashMap<>();
    HashMap<String, String> cards_sent_al_json = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade);

        Firebase.setAndroidContext(this);

        cardsReceivedList = (ListView)findViewById(R.id.cards_received);
        cardsSentList = (ListView)findViewById(R.id.cards_sent);

        acceptButton = (Button)findViewById(R.id.AcceptButton);
        rejectButton = (Button)findViewById(R.id.RejectButton);

        //Determines correct order of UIDs (i.e. UIDONE_UIDTWO vs. UIDTWO_UIDONE)
        String url;
        if(UserDetails.tradeWith.compareTo(UserDetails.currentUser.getUid()) > 0)
        {
            url = "https://baseballmessenger-afdea.firebaseio.com/trades/" + UserDetails.currentUser.getUid() + "_" + UserDetails.tradeWith + ".json";
        }
        else
        {
            url = "https://baseballmessenger-afdea.firebaseio.com/trades/" + UserDetails.tradeWith + "_" + UserDetails.currentUser.getUid() + ".json";
        }

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s)
            {
                doOnSuccess(s);
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                System.out.println("" + volleyError);
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(SingleTradeActivity.this);
        rQueue.add(request);

        acceptButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                //Add cards sent to current user's collection
                for(int i = 0; i < cards_sent_al.size(); i++) {
                    try {
                        JSONObject obj = new JSONObject(cards_sent_al_json.get(cards_sent_al.get(i)));

//                        Card tempCard = new Card();
//                        tempCard.setCondition(obj.getString("condition"));
//                        tempCard.setDateAcquired(obj.getString("date_acquired"));
//                        tempCard.setName(obj.getString("name"));
//                        tempCard.setNumber(obj.getString("number"));
//                        tempCard.setRole(obj.getString("role"));
//                        tempCard.setValue(Double.parseDouble(obj.getString("value")));
//                        tempCard.setYear(obj.getString("year"));
//                        tempCard.setTeam(obj.getString("team"));
//
//                        UserDetails.db.cardDAO().insertAll(tempCard); //TODO Eliminate local database for Firebase cloud storage

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                //Add cards received to other user's collection
                String url = "https://baseballmessenger-afdea.firebaseio.com/users/" + UserDetails.tradeWith + "/cards";

                Firebase reference = new Firebase(url);

                for(int i = 0; i < cards_received_al.size(); i++)
                {
                    try
                    {
                        JSONObject obj = new JSONObject(cards_received_al_json.get(cards_received_al.get(i)));
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("condition", obj.getString("condition"));
                        map.put("date_acquired", obj.getString("date_acquired"));
                        map.put("name", obj.getString("name"));
                        map.put("number", obj.getString("number"));
                        map.put("owner", UserDetails.tradeWith);
                        map.put("role", obj.getString("role"));
                        map.put("value", obj.getString("value"));
                        map.put("year", obj.getString("year"));
                        map.put("team", obj.getString("team"));
                        reference.push().setValue(map);

//                        Card tempCard = new Card();
//                        tempCard.setCondition(obj.getString("condition"));
//                        tempCard.setDateAcquired(obj.getString("date_acquired"));
//                        tempCard.setName(obj.getString("name"));
//                        tempCard.setNumber(obj.getString("number"));
//                        tempCard.setRole(obj.getString("role"));
//                        tempCard.setValue(Double.parseDouble(obj.getString("value")));
//                        tempCard.setYear(obj.getString("year"));
//                        tempCard.setTeam(obj.getString("team"));
//                        UserDetails.db.cardDAO().delete(tempCard.getName(), tempCard.getNumber()); //TODO Eliminate local database for Firebase cloud storage

                    }
                    catch(JSONException e)
                    {
                        e.printStackTrace();
                    }
                }

                //Determines correct order of UIDs (i.e. UIDONE_UIDTWO vs. UIDTWO_UIDONE)
                String url2;
                if(UserDetails.tradeWith.compareTo(UserDetails.currentUser.getUid()) > 0)
                {
                    url2 = "https://baseballmessenger-afdea.firebaseio.com/trades/" + UserDetails.currentUser.getUid() + "_" + UserDetails.tradeWith + ".json";
                }
                else
                {
                    url2 = "https://baseballmessenger-afdea.firebaseio.com/trades/" + UserDetails.tradeWith + "_" + UserDetails.currentUser.getUid() + ".json";
                }

                //Deletes trade object in Firebase database
                final String urlTwo = url2;
                StringRequest request = new StringRequest(Request.Method.GET, url2, new Response.Listener<String>(){
                    @Override
                    public void onResponse(String s)
                    {
                        try
                        {
                            JSONObject obj = new JSONObject(s);
                            Iterator i = obj.keys();

                            while(i.hasNext())
                            {
                                String trade = i.next().toString();
                                JSONObject childObj = obj.getJSONObject(trade);
                                if(childObj.getString("trade_id").equals(UserDetails.tradeNumber))
                                {
                                    Firebase referenceTwo = new Firebase(urlTwo.substring(0, urlTwo.indexOf(".json")));
                                    //Waits for the removeValue() to complete before starting activity
                                    referenceTwo.child(trade).removeValue(new Firebase.CompletionListener() {
                                        @Override
                                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                            syncFirebaseWithDatabase(); //TODO Eliminate local database for Firebase cloud storage
                                            startActivity(new Intent(SingleTradeActivity.this, TradeListActivity.class));
                                        }
                                    });
                                }

                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError volleyError)
                    {
                        System.out.println("" + volleyError);
                    }
                });

                RequestQueue rQueue = Volley.newRequestQueue(SingleTradeActivity.this);
                rQueue.add(request);
            }
        });

        rejectButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                //Add cards sent to user's collection

                String url = "https://baseballmessenger-afdea.firebaseio.com/cards/" + UserDetails.tradeWith + "/cards";

                Firebase reference = new Firebase(url);

                for(int i = 0; i < cards_sent_al.size(); i++)
                {
                    try
                    {
                        JSONObject obj = new JSONObject(cards_sent_al_json.get(cards_sent_al.get(i)));
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("condition", obj.getString("condition"));
                        map.put("date_acquired", obj.getString("date_acquired"));
                        map.put("name", obj.getString("name"));
                        map.put("number", obj.getString("number"));
                        map.put("owner", UserDetails.tradeWith);
                        map.put("role", obj.getString("role"));
                        map.put("value", obj.getString("value"));
                        map.put("year", obj.getString("year"));
                        map.put("team", obj.getString("team"));
                        reference.push().setValue(map);
                    }
                    catch(JSONException e)
                    {
                        e.printStackTrace();
                    }
                }

                //Determines correct order of UIDs (i.e. UIDONE_UIDTWO vs. UIDTWO_UIDONE)
                String url2;
                if(UserDetails.tradeWith.compareTo(UserDetails.currentUser.getUid()) > 0)
                {
                    url2 = "https://baseballmessenger-afdea.firebaseio.com/trades/" + UserDetails.currentUser.getUid() + "_" + UserDetails.tradeWith + ".json";
                }
                else
                {
                    url2 = "https://baseballmessenger-afdea.firebaseio.com/trades/" + UserDetails.tradeWith + "_" + UserDetails.currentUser.getUid() + ".json";
                }

                //Deletes trade object in Firebase database
                final String urlTwo = url2;
                StringRequest request = new StringRequest(Request.Method.GET, url2, new Response.Listener<String>(){
                    @Override
                    public void onResponse(String s)
                    {
                        try
                        {
                            JSONObject obj = new JSONObject(s);
                            Iterator i = obj.keys();

                            while(i.hasNext())
                            {
                                String trade = i.next().toString();
                                JSONObject childObj = obj.getJSONObject(trade);
                                if(childObj.getString("trade_id").equals(UserDetails.tradeNumber))
                                {
                                    Firebase referenceTwo = new Firebase(urlTwo.substring(0, urlTwo.indexOf(".json")));
                                    //Waits for removeValue() to complete to start activity
                                    referenceTwo.child(trade).removeValue(new Firebase.CompletionListener() {
                                        @Override
                                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                            syncFirebaseWithDatabase(); //TODO Eliminate local database for Firebase cloud storage
                                            startActivity(new Intent(SingleTradeActivity.this, TradeListActivity.class));
                                        }
                                    });

                                }

                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError volleyError)
                    {
                        System.out.println("" + volleyError);
                    }
                });

                RequestQueue rQueue = Volley.newRequestQueue(SingleTradeActivity.this);
                rQueue.add(request);

            }
        });

        // Set up drawer
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        DrawerListener listen = new DrawerListener(this, drawer);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(listen);
    }

    //Retrieves trade data from Firebase database
    public void doOnSuccess(String s)
    {
        try
        {
            JSONObject obj = new JSONObject(s);
            Iterator i = obj.keys();

            while(i.hasNext())
            {
                String trade = i.next().toString();
                JSONObject childObj = obj.getJSONObject(trade);
                if(childObj.getString("trade_id").equals(UserDetails.tradeNumber))
                {
                    JSONObject cardsReceived = childObj.getJSONObject("cards_received");
                    JSONObject cardsSent = childObj.getJSONObject("cards_sent");

                    Iterator k = cardsReceived.keys();

                    while(k.hasNext())
                    {
                        String key = k.next().toString();
                        JSONObject childChildObj = cardsReceived.getJSONObject(key);
                        String cardName = childChildObj.getString("name");

                        cards_received_al.add(cardName);
                        cards_received_al_json.put(cardName, childChildObj.toString());
                    }

                    Iterator j = cardsSent.keys();

                    while(j.hasNext())
                    {
                        String key = j.next().toString();
                        JSONObject childChildObj = cardsSent.getJSONObject(key);
                        String cardName = childChildObj.getString("name");

                        cards_sent_al.add(cardName);
                        cards_sent_al_json.put(cardName, childChildObj.toString());
                    }
                }

            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        cardsSentList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, cards_sent_al));
        cardsReceivedList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, cards_received_al));

    }

    //Pushes contents of local database to Firebase cloud storage - overwrites existing data in cloud with contents of local database
    //TODO Eliminate local database for Firebase cloud storage
    public void syncFirebaseWithDatabase()
    {
        Firebase reference = new Firebase("https://baseballmessenger-afdea.firebaseio.com/cards/" + UserDetails.currentUser.getUid());

        Map<String, Object> newCard = new HashMap<>();
//        List<Card> receivedCards = UserDetails.db.cardDAO().getAll();
//        System.out.println(receivedCards.size());
//        for(int i = 0; i < receivedCards.size(); i++)
//        {
//            Map<String, String> temp2 = new HashMap<>();
//            temp2.put("condition", receivedCards.get(i).getCondition());
//            temp2.put("date_acquired", receivedCards.get(i).getDateAcquired());
//            temp2.put("name", receivedCards.get(i).getName());
//            temp2.put("number", receivedCards.get(i).getNumber());
//            temp2.put("owner", UserDetails.currentUser.getUid());
//            temp2.put("role", receivedCards.get(i).getRole());
//            temp2.put("team", receivedCards.get(i).getTeam());
//            temp2.put("value", Integer.toString((int)(receivedCards.get(i).getValue())));
//            temp2.put("year", receivedCards.get(i).getYear());
//            newCard.put(Integer.toString((int)Math.round(Math.random()*100 + 1)), temp2);
//        }
//        reference.child("cards").setValue(newCard);

    }
}
