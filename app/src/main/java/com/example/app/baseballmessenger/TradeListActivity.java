package com.example.app.baseballmessenger;

import android.arch.persistence.room.Database;
import android.content.Intent;
import android.os.Bundle;
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

/**
 * Created by pr4h6n on 2/27/18.
 */

public class TradeListActivity extends AppCompatActivity {
    ListView tradesList;
    TextView noTradesText;
    Button newTradeButton;
    ArrayList<String> allTrades;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trades);

        tradesList = (ListView)findViewById(R.id.tradesList);
        noTradesText = (TextView)findViewById(R.id.noTradesText);
        newTradeButton = (Button)findViewById(R.id.newTradeButton);

        allTrades = new ArrayList<String>();

        //TODO Implement Navigation Drawer
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

        //Get list of all trades from Firebase and fill ListView
        DatabaseReference ref = Trade.databaseReference();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot tradeSnapshot: dataSnapshot.getChildren())
                {
                    Trade trade = tradeSnapshot.getValue(Trade.class);
                    if(trade.receivingUser.equals(Handoff.currentUser.uuid))
                    {
                        allTrades.add(trade.uuid);
                    }
                }

                tradesList.setVisibility(View.VISIBLE);
                tradesList.setAdapter(new ArrayAdapter<String>(TradeListActivity.this, android.R.layout.simple_list_item_1, allTrades));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        tradesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(TradeListActivity.this, SingleTradeActivity.class);
                i.putExtra("uuid", allTrades.get(position));
                startActivity(i);
            }
        });

        newTradeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(TradeListActivity.this, NewTrade.class));
            }
        });

    }
}
