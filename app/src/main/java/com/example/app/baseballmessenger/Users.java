package com.example.app.baseballmessenger;

import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
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
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.example.app.baseballmessenger.UserDetails.db;

/**
 * Created by pr4h6n on 2/25/18.
 */

public class Users extends AppCompatActivity{
    ListView usersList;
    TextView noUsersText;
    Button trade;
    ArrayList<String> al = new ArrayList<String>();
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        mAuth = FirebaseAuth.getInstance();

        Firebase.setAndroidContext(this);

        usersList = (ListView)findViewById(R.id.usersList);
        noUsersText = (TextView)findViewById(R.id.noUsersText);
        trade = (Button)findViewById(R.id.tradeButton);

        String url = "https://baseballmessenger-afdea.firebaseio.com/users.json";

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

        RequestQueue rQueue = Volley.newRequestQueue(Users.this);
        rQueue.add(request);

        fillDatabaseFromFirebase(); //TODO Eliminate local database for Firebase cloud storage
        usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserDetails.chatWith = UserDetails.hashMap.get(al.get(position)); //Stores the other user's UID in chat conversation for future use
                startActivity(new Intent(Users.this, Chat.class));
            }
        });

        trade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Users.this, Trades.class));
            }
        });

    }

    //Retrieves all users from Firebase database and stores in ArrayList
    public void doOnSuccess(String s)
    {
        try{
            JSONObject obj = new JSONObject(s);
            Iterator i = obj.keys();

            while(i.hasNext())
            {
                String uid = i.next().toString();
                if(!uid.equals(UserDetails.currentUser.getUid()))
                {
                    JSONObject childObj = obj.getJSONObject(uid);
                    String emailAddr = childObj.get("email").toString();
                    al.add(emailAddr);
                    UserDetails.hashMap.put(emailAddr, uid);
                }
            }

        }catch (JSONException e)
        {
            e.printStackTrace();
        }

        if(al.size() <= 1)
        {
            noUsersText.setVisibility(View.VISIBLE);
            usersList.setVisibility(View.GONE);
        }
        else
        {
            noUsersText.setVisibility(View.GONE);
            usersList.setVisibility(View.VISIBLE);
            usersList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, al));
        }
    }

    //Pulls contents to local database from Firebase cloud storage - overwrites existing data in local database with contents of cloud
    //TODO Eliminate local database for Firebase cloud storage
    public void fillDatabaseFromFirebase()
    {

        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "cards-collection").allowMainThreadQueries().fallbackToDestructiveMigration().build();
        db.cardDAO().nukeTable();

        String url = "https://baseballmessenger-afdea.firebaseio.com/users/" + UserDetails.currentUser.getUid() + "/cards.json";

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
                        UserDetails.db.cardDAO().insertAll(temp);
                    }
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

        RequestQueue rQueue = Volley.newRequestQueue(Users.this);
        rQueue.add(request);
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
            newCard.put(Integer.toString((int)Math.round(Math.random()*100 + 1)), temp2);
        }
        reference.child("cards").setValue(newCard);

    }

}
