package com.example.app.baseballmessenger;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.example.app.baseballmessenger.UserDetails.db;

/**
 * Created by pr4h6n on 2/25/18.
 */

public class SearchUsersActivity extends AppCompatActivity{
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private GridView userScroll;
    private UserAdapter adapter;
    private ArrayList<User> users = new ArrayList<User>();
    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // Set up toolbar and drawer
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up drawer
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        DrawerListener listen = new DrawerListener(this, drawer);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(listen);

        // Set up pretty user scroll
        userScroll = (GridView) findViewById(R.id.user_scroll);
        userScroll.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Intent i=new Intent(SearchUsersActivity.this,UserDetailActivity.class);
                i.putExtra("user", users.get(position));
                startActivity(i);
            }
        });

        // Set up list of Users
        users = new ArrayList<User>();

        // Set up Firebase
        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("users");
        Query q = reference.orderByChild("email");

        ChildEventListener userListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d("User added", "onChildAdded:" + dataSnapshot.getKey());
                users.add(dataSnapshot.getValue(User.class));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                // Users cannot visibly change while searching
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // Users cannot ever be deleted
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                // Users dont move
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("User Search", "loadPost:onCancelled", databaseError.toException());
                Toast.makeText(mContext, "Failed to load users.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        q.addChildEventListener(userListener);
        Log.d("Search Users","there are " + users.size());

        adapter = new UserAdapter(this, R.layout.user_thumbnail, users);
        userScroll.setAdapter(adapter);
    }


}
