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

public class SearchUsersActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private GridView userScroll;
    private UserAdapter adapter;
    private ArrayList<User> users = new ArrayList<User>();
    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // Set up toolbar and drawer
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_collection) {
            startActivity(new Intent(SearchUsersActivity.this, NewTrade.class));
        } else if (id == R.id.nav_wishlist) {
            startActivity(new Intent(SearchUsersActivity.this, WishlistActivity.class));
        } else if (id == R.id.nav_profile) {
            Intent i=new Intent(this,UserDetailActivity.class);
            i.putExtra("user", Handoff.currentUser);
            startActivity(i);
        } else if (id == R.id.nav_chat) {
            startActivity(new Intent(SearchUsersActivity.this, NewTrade.class));
        } else if (id == R.id.nav_users) {
            startActivity(new Intent(SearchUsersActivity.this, SearchUsersActivity.class));
        } else if (id == R.id.nav_trade) {
            startActivity(new Intent(SearchUsersActivity.this, NewTrade.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
