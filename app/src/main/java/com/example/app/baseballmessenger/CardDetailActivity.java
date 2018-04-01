package com.example.app.baseballmessenger;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Created by pr4h6n on 4/1/18.
 */

public class CardDetailActivity extends AppCompatActivity {

    private Card data;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        data = getIntent().getParcelableExtra("card"); //TODO This is null for some reason... don't know why bc i.putParcelableExtra() is passed a non-null value

        TextView cardName = findViewById(R.id.cardName);
        cardName.setText(data.name);

        TextView value = findViewById(R.id.value);
        value.setText(data.value + "");

        TextView owner = findViewById(R.id.owner);
        owner.setText(data.owner + "");

        TextView condition = findViewById(R.id.condition);
        condition.setText(data.condition + "");

        TextView number = findViewById(R.id.number);
        number.setText(data.number);

        TextView role = findViewById(R.id.role);
        role.setText(data.role + "");

        TextView team = findViewById(R.id.team);
        team.setText(data.team + "");

        TextView year = findViewById(R.id.year);
        year.setText(data.year + "");

        TextView date = findViewById(R.id.date);
        date.setText(data.dateAcquired + "");

        // Set up drawer
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        DrawerListener listen = new DrawerListener(this, drawer);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(listen);
    }
}
