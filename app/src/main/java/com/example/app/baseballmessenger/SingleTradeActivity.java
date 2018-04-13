package com.example.app.baseballmessenger;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    private String uuid;
    private DatabaseReference ref;
    Card cardSent;
    Card cardRequested;

    ListView cardsReceivedList;
    ListView cardsSentList;

    Button acceptButton;
    Button rejectButton;

    ArrayList<String> al = new ArrayList<>();
    ArrayList<String> al2 = new ArrayList<>();
    Trade t;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade);

        Firebase.setAndroidContext(this);

        uuid = getIntent().getStringExtra("uuid");
        ref = Trade.databaseReference();

        cardsReceivedList = (ListView)findViewById(R.id.cards_received);
        cardsSentList = (ListView)findViewById(R.id.cards_sent);

        acceptButton = (Button)findViewById(R.id.AcceptButton);
        rejectButton = (Button)findViewById(R.id.RejectButton);

        t = new Trade(uuid, new MyCallback() {
            @Override
            public void onCallback(Trade t) {

                cardRequested = new Card(t.requestingUser, true, t.cardSent, new MyCallback() {
                    @Override
                    public void onCallback(Card c) {
                        al.clear();
                        al.add(cardRequested.name);
                        cardsSentList.setAdapter(new ArrayAdapter<String>(SingleTradeActivity.this, android.R.layout.simple_list_item_1, al));
                    }

                    @Override
                    public void onCallback(Trade t) {

                    }

                    @Override
                    public void onCallback(User u) {

                    }
                });


                cardSent = new Card(t.receivingUser, true, t.cardRequested, new MyCallback() {
                    @Override
                    public void onCallback(Card c) {
                        al2.clear();
                        al2.add(cardSent.name);
                        cardsReceivedList.setAdapter(new ArrayAdapter<String>(SingleTradeActivity.this, android.R.layout.simple_list_item_1, al2));
                    }

                    @Override
                    public void onCallback(Trade t) {

                    }

                    @Override
                    public void onCallback(User u) {

                    }
                });
            }

            @Override
            public void onCallback(Card c) {

            }

            @Override
            public void onCallback(User u) {

            }
        });

        acceptButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {

                if(cardRequested.value < cardSent.value)
                {
                    AlertDialog alertDialog = new AlertDialog.Builder(SingleTradeActivity.this).create();
                    alertDialog.setTitle("Unbalanced Trade Warning");
                    alertDialog.setMessage("Are you sure you want to accept this trade? This action cannot be undone.");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Accept",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Called when clicking the Accept button on the modal dialog
                                    // Dismiss the dialog
                                    dialog.dismiss();

                                    //Add cards sent to current user's collection
                                    Card.deleteCard(cardSent.owner, true, cardSent.uuid);
                                    cardSent.owner = t.requestingUser;
                                    cardSent.lockstatus = false;
                                    cardSent.updateFirebase();

                                    //Add cards received to other user's collection
                                    Card.deleteCard(cardRequested.owner, true, cardRequested.uuid);
                                    cardRequested.owner = t.receivingUser;
                                    cardRequested.lockstatus = false;
                                    cardRequested.updateFirebase();

                                    //Deletes trade object in Firebase database
                                    Trade.deleteTrade(t.uuid);

                                    //Update users' total collection value
                                    User u = new User(t.receivingUser, new MyCallback() {
                                        @Override
                                        public void onCallback(Card c) {

                                        }

                                        @Override
                                        public void onCallback(Trade t) {

                                        }

                                        @Override
                                        public void onCallback(User u) {
                                            u.calculateCollectionValue();
                                            u.updateFirebase();
                                        }
                                    });

                                    User u2 = new User(t.requestingUser, new MyCallback() {
                                        @Override
                                        public void onCallback(Card c) {

                                        }

                                        @Override
                                        public void onCallback(Trade t) {

                                        }

                                        @Override
                                        public void onCallback(User u) {
                                            u.calculateCollectionValue();
                                            u.updateFirebase();
                                        }
                                    });

                                    startActivity(new Intent(SingleTradeActivity.this, TradeListActivity.class));
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Called when clicking CANCEL on the modal dialog (just dismiss it)
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
                else
                {
                    //Add cards sent to current user's collection
                    Card.deleteCard(cardSent.owner, true, cardSent.uuid);
                    cardSent.owner = t.requestingUser;
                    cardSent.lockstatus = false;
                    cardSent.updateFirebase();

                    //Add cards received to other user's collection
                    Card.deleteCard(cardRequested.owner, true, cardRequested.uuid);
                    cardRequested.owner = t.receivingUser;
                    cardRequested.lockstatus = false;
                    cardRequested.updateFirebase();

                    //Deletes trade object in Firebase database
                    Trade.deleteTrade(t.uuid);

                    startActivity(new Intent(SingleTradeActivity.this, TradeListActivity.class));
                }
            }
        });

        rejectButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                //Add cards sent to user's collection
                cardRequested.lockstatus = false;
                cardRequested.updateFirebase();

                //Deletes trade object in Firebase database
                Trade.deleteTrade(t.uuid);

                startActivity(new Intent(SingleTradeActivity.this, TradeListActivity.class));
            }
        });

        cardsSentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {



                User u = new User(t.receivingUser, new MyCallback() {
                    @Override
                    public void onCallback(Card c) {

                    }

                    @Override
                    public void onCallback(Trade t) {

                    }

                    @Override
                    public void onCallback(User u) {
                        u.calculateCollectionValue();
                        u.updateFirebase();
                        //TODO NEED TO PUT USER IN EXTRAS IN HERE
                        Intent i = new Intent(SingleTradeActivity.this, CardDetailActivity.class);
                        i.putExtra("card", cardRequested);
                        i.putExtra("update_delete_access", false);
                        i.putExtra("trade_view", true);
                        i.putExtra("user", u);
                        startActivity(i);
                    }
                });
            }
        });

        cardsReceivedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                User u = new User(t.receivingUser, new MyCallback() {
                    @Override
                    public void onCallback(Card c) {

                    }

                    @Override
                    public void onCallback(Trade t) {

                    }

                    @Override
                    public void onCallback(User u) {
                        u.calculateCollectionValue();
                        u.updateFirebase();
                        //TODO NEED TO PUT USER IN EXTRAS IN HERE
                        Intent i = new Intent(SingleTradeActivity.this, CardDetailActivity.class);
                        i.putExtra("card", cardSent);
                        i.putExtra("update_delete_access", false);
                        i.putExtra("user", u);
                        i.putExtra("trade_view", true);
                        startActivity(i);
                    }
                });

            }
        });

        //TODO Implement Navigation Drawer
        // Set up drawer
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();
//
//        DrawerListener listen = new DrawerListener(this, drawer);
//        NavigationView navigationView = findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(listen);
    }
}
