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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by pr4h6n on 3/3/18.
 */

// TODO Add functionality to CollectionActivity class and delete
public class SelectCards extends AppCompatActivity {

    TextView noCardsText;
    ListView cardsList;
    Button doneButton;
    ArrayList<String> al = new ArrayList<>();
    List<Card> allCards;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectcards);

        noCardsText = (TextView)findViewById(R.id.noCardsText);
        cardsList = (ListView)findViewById(R.id.cardsList);
        doneButton = (Button)findViewById(R.id.doneButton);

        if(UserDetails.card_mode == 0)
        {
            //Load current user's cards
            allCards = UserDetails.db.cardDAO().getAll(); //TODO Eliminate local database for Firebase cloud storage

            for(int i = 0; i < allCards.size(); i++)
            {
                al.add(allCards.get(i).getName());
            }

            cardsList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, al));
        }
        else
        {
            //Load other user's cards
            allCards = new ArrayList<>();

            String url = "https://baseballmessenger-afdea.firebaseio.com/cards/" + UserDetails.selectedUserTrade + "/cards.json";

            StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                @Override
                public void onResponse(String s)
                {
                    try{

                        JSONObject obj = new JSONObject(s);
                        Iterator i = obj.keys();

                        while(i.hasNext())
                        {
                            String key = i.next().toString();
                            JSONObject childObj = obj.getJSONObject(key);
                            Card temp = new Card();
                            temp.setCondition(childObj.getString("condition"));
                            temp.setDateAcquired(childObj.getString("date_acquired"));
                            temp.setName(childObj.getString("name"));
                            temp.setRole(childObj.getString("role"));
                            temp.setTeam(childObj.getString("team"));
                            temp.setNumber(childObj.getString("number"));
                            temp.setValue(Double.parseDouble(childObj.getString("value")));
                            temp.setYear(childObj.getString("year"));
                            allCards.add(temp);
                            al.add(temp.getName());
                        }

                        cardsList.setAdapter(new ArrayAdapter<String>(SelectCards.this, android.R.layout.simple_list_item_1, al));
                    }
                    catch(JSONException e)
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

            RequestQueue rQueue = Volley.newRequestQueue(SelectCards.this);
            rQueue.add(request);

        }

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SelectCards.this, NewTrade.class));
            }
        });

        //Adds selected card to ArrayList storing all selected cards
        cardsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String cardName = al.get(position);
                for(int i = 0; i < allCards.size(); i++)
                {
                    if(allCards.get(i).getName().equals(cardName))
                    {
                        if(UserDetails.card_mode == 0)
                        {
                            NewTrade.sentCards.add(allCards.get(i));
                            NewTrade.sentCardsAl.add(allCards.get(i).getName());
                        }
                        else
                        {
                            NewTrade.receivedCards.add(allCards.get(i));
                            NewTrade.receivedCardsAl.add(allCards.get(i).getName());
                        }
                    }
                }

            }
        });
    }

}
