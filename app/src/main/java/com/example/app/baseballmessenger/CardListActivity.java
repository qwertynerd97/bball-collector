package com.example.app.baseballmessenger;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by pr4h6n on 4/1/18.
 * The CardListActivity is responsible for displaying a list of cards to the user.
 * These can either be from the wishlist, or from the card collection.
 * The following bundle data is supplied:
 *      "wishlist" - boolean - True if we're looking at the wishlist, False otherwise
 */

public class CardListActivity extends AppCompatActivity {
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private GridView cardScroll;
    private CardAdapter adapter;
    private TextView noCardsView;
    private ArrayList<Card> cards = new ArrayList<Card>();
    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private Context mContext = this;
    private String previousActivity = "";
    private Button addCardButton;

    private boolean isWishlist;
    private static final String noCardsInCollectionString = "There are no cards in your collection. Perhaps you would like to add one?";
    private static final String noCardsInWishlistString = "There are no cards in your wishlist. Perhaps you would like to add one?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);

        if(savedInstanceState == null)
        {
            isWishlist = getIntent().getBooleanExtra("wishlist", false);
        }
        else
        {
            isWishlist = savedInstanceState.getBoolean("wishlist");
        }

        previousActivity = getIntent().getStringExtra("previous_activity");

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

        // Set up the Add New Card button
        addCardButton = (Button)findViewById(R.id.addCardButton);
        addCardButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Intent i = new Intent(CardListActivity.this, AddCardActivity.class);
                i.putExtra("wishlist", isWishlist);
                startActivity(i);
            }
        });

        // Set up pretty card scroll
        cardScroll = (GridView) findViewById(R.id.card_scroll);
        cardScroll.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                // Executed when an item on the grid view is clicked
                Intent i=new Intent(CardListActivity.this, CardDetailActivity.class);
                i.putExtra("card", cards.get(position));
                i.putExtra("wishlist", isWishlist);
                startActivity(i);
            }
        });

        // Set the thing to show when the GridView is empty
        noCardsView = (TextView) findViewById(R.id.noCardsText);
        if(isWishlist)
        {   // use the wishlist text
            noCardsView.setText(noCardsInWishlistString);
        }
        else
        {   // use the collection text
            noCardsView.setText(noCardsInCollectionString);
        }
        cardScroll.setEmptyView(noCardsView);

        // Set up list of Cards
        cards = new ArrayList<Card>();

        // Set up Firebase
        mAuth = FirebaseAuth.getInstance();

        reference = Card.databaseReference(FirebaseAuth.getInstance().getCurrentUser().getUid(), !isWishlist);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot cardSnapshot:dataSnapshot.getChildren())
                {
                    cards.add(cardSnapshot.getValue(Card.class));
                    System.out.println(cardSnapshot.getValue());
                }
                Log.d("Search Cards","there are " + cards.size());

                adapter = new CardAdapter(CardListActivity.this, R.layout.card_thumbnail, cards);
                adapter.notifyDataSetChanged();
                cardScroll.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}