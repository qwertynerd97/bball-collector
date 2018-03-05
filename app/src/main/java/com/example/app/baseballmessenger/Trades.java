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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by pr4h6n on 2/27/18.
 */

public class Trades extends AppCompatActivity {
    ListView tradesList;
    TextView noTradesText;
    Button newTradeButton;
    ArrayList<String> al = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trades);

        tradesList = (ListView)findViewById(R.id.tradesList);
        noTradesText = (TextView)findViewById(R.id.noTradesText);
        newTradeButton = (Button)findViewById(R.id.newTradeButton);

        String url = "https://baseballmessenger-afdea.firebaseio.com/trades.json";

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

        RequestQueue rQueue = Volley.newRequestQueue(Trades.this);
        rQueue.add(request);

        tradesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Store trade information for future use
                UserDetails.tradeWith = UserDetails.hashMap.get(al.get(position).substring(11, al.get(position).indexOf(" #")));
                UserDetails.tradeNumber = al.get(position).substring(al.get(position).indexOf(" #")+2);
                startActivity(new Intent(Trades.this, Trade.class));
            }
        });

        newTradeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                //Clear all ArrayLists containing past selections from other trades
                NewTrade.receivedCardsAl.clear();
                NewTrade.receivedCards.clear();
                NewTrade.sentCardsAl.clear();
                NewTrade.sentCards.clear();
                startActivity(new Intent(Trades.this, NewTrade.class));
            }
        });

    }

    //Displays all trades that involve current user but were not initiated by current user (they shouldn't be allowed to accept/decline their own trade proposal)
    public void doOnSuccess(String s)
    {
        try{
            JSONObject obj = new JSONObject(s);
            Iterator i = obj.keys();

            while(i.hasNext())
            {
                String uid = i.next().toString();
                if(uid.contains(UserDetails.currentUser.getUid()))
                {
                    JSONObject childObj = obj.getJSONObject(uid);

                    Iterator i2 = childObj.keys();
                    while(i2.hasNext())
                    {
                        String tradeIdentifier = i2.next().toString();
                        JSONObject childOfChildObj = childObj.getJSONObject(tradeIdentifier);
                        if(!childOfChildObj.getString("user").equals(UserDetails.currentUser.getUid()))
                        {
                            String tradeNumber = childOfChildObj.getString("trade_id");
                            String[] users = uid.split("_");

                            String otherUserUID;
                            String otherUserEmail = "";

                            //Get the other user's UID
                            if(users[0].equals(UserDetails.currentUser.getUid()))
                            {
                                otherUserUID = users[1];
                            }
                            else
                            {
                                otherUserUID = users[0];
                            }

                            //Get user email from UID
                            for(String key: UserDetails.hashMap.keySet())
                            {
                                if(UserDetails.hashMap.get(key).equals(otherUserUID))
                                {
                                    otherUserEmail = key;
                                }
                            }

                            al.add("Trade with " + otherUserEmail + " #" + tradeNumber);
                        }
                    }
                }
            }

        }catch (JSONException e)
        {
            e.printStackTrace();
        }

        if(al.size() < 1)
        {
            noTradesText.setVisibility(View.VISIBLE);
            tradesList.setVisibility(View.GONE);
        }
        else
        {
            noTradesText.setVisibility(View.GONE);
            tradesList.setVisibility(View.VISIBLE);
            tradesList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, al));
        }
    }

}
