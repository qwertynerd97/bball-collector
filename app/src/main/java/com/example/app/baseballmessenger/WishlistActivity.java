package com.example.app.baseballmessenger;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class WishlistActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private GridView cardScroll;
    private CardAdapter adapter;
    private ArrayList<Card> cards = new ArrayList<Card>();
    private FirebaseAuth mAuth;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

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
        cardScroll = (GridView) findViewById(R.id.user_scroll);
        cardScroll.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(WishlistActivity.this, "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(WishlistActivity.this,"Add a new card",Toast.LENGTH_SHORT).show();
            }
        });

        // Set up list of cards
        cards = new ArrayList<Card>();

        // Set up Firebase
        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("cards").child(Handoff.currentUser.uuid).child("wishlist");
        Query q = reference.orderByChild("name");

        ChildEventListener userListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d("Card added", "onChildAdded:" + dataSnapshot.getKey());
                cards.add(dataSnapshot.getValue(Card.class));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                // Cards cannot visibly change while searching
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // Users cannot ever be deleted
                cards.remove(dataSnapshot.getValue(Card.class));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                // Cards dont move
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Wishlist", "loadPost:onCancelled", databaseError.toException());
                Toast.makeText(WishlistActivity.this, "Failed to load cards.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        q.addChildEventListener(userListener);
        Log.d("Wishlist","there are " + cards.size());

        adapter = new CardAdapter(this, R.layout.card_thumbnail);
        cardScroll.setAdapter(adapter);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_collection) {
            startActivity(new Intent(WishlistActivity.this, NewTrade.class));
        } else if (id == R.id.nav_wishlist) {
            startActivity(new Intent(WishlistActivity.this, WishlistActivity.class));
        } else if (id == R.id.nav_profile) {
            Intent i=new Intent(WishlistActivity.this,UserDetailActivity.class);
            i.putExtra("user", Handoff.currentUser);
            startActivity(i);
        } else if (id == R.id.nav_chat) {
            startActivity(new Intent(WishlistActivity.this, NewTrade.class));
        } else if (id == R.id.nav_users) {
            startActivity(new Intent(WishlistActivity.this, SearchUsersActivity.class));
        } else if (id == R.id.nav_trade) {
            startActivity(new Intent(WishlistActivity.this, NewTrade.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
