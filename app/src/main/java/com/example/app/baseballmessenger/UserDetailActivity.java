package com.example.app.baseballmessenger;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.Button;
import android.widget.TextView;

public class UserDetailActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private User data;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button collectionButton = findViewById(R.id.collectionButton);
        collectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        Button wishlistButton = findViewById(R.id.wishlistButton);
        wishlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        Button chatButton = findViewById(R.id.chatButton);
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Erase saved login
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(UserDetailActivity.this);
                sharedPref.edit().putString("user_email","dummyuser@gmail.com").putString("user_password","").apply();

                // Go to login page
                startActivity(new Intent(UserDetailActivity.this, Login.class));
            }
        });

        data = getIntent().getParcelableExtra("user");

        Log.d("User Detail",data.toString());
        Log.d("User Detail",Handoff.currentUser.toString());

        // Set up visibility for current user vs other cards
        if(data.uuid.equals(Handoff.currentUser.uuid)){
            collectionButton.setVisibility(View.GONE);
            wishlistButton.setVisibility(View.GONE);
            chatButton.setVisibility(View.GONE);
            logoutButton.setVisibility(View.VISIBLE);
        }else{
            collectionButton.setVisibility(View.VISIBLE);
            wishlistButton.setVisibility(View.VISIBLE);
            chatButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.GONE);
        }

        TextView userName = findViewById(R.id.userName);
        userName.setText(data.email);
        TextView value = findViewById(R.id.value);
        value.setText(data.value + "");
        TextView collectionCards = findViewById(R.id.collection);
        collectionCards.setText(data.numCollection + "");
        TextView wishlistCards = findViewById(R.id.wishlist);
        wishlistCards.setText(data.numWishlist + "");

        // Set up drawer
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_collection) {
            startActivity(new Intent(this, NewTrade.class));
        } else if (id == R.id.nav_wishlist) {
            startActivity(new Intent(this, WishlistActivity.class));
        } else if (id == R.id.nav_profile) {
            Intent i=new Intent(this,UserDetailActivity.class);
            i.putExtra("user", Handoff.currentUser);
            startActivity(i);
        } else if (id == R.id.nav_chat) {
            startActivity(new Intent(this, NewTrade.class));
        } else if (id == R.id.nav_users) {
            startActivity(new Intent(this, SearchUsersActivity.class));
        } else if (id == R.id.nav_trade) {
            startActivity(new Intent(this, NewTrade.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
