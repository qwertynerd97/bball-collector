package com.example.app.baseballmessenger;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/*
 *  The Add/Edit Card Activity is responsible both for adding a new card and editing an existing card.
 *  It accepts the following Intent extras:
 *      "wishlist" (boolean): true if this is a card from the Wishlist
 *      "add" (boolean): true if we are adding a card. false otherwise
 *      "card" (optional, Card): the card that we are modifying, if add is true
 */
public class AddEditCardActivity extends AppCompatActivity {

    private boolean addingCard;
    private boolean isWishlist;
    private Card data;
    private DrawerLayout drawer;

    private ImageView image;
    private Button chooseImageButton;
    private Button saveCardButton;
    private EditText nameEntry;
    private EditText ownerEntry;
    private EditText playerEntry;
    private EditText roleEntry;
    private EditText conditionEntry;
    private EditText numberEntry;
    private EditText yearEntry;
    private EditText teamEntry;
    private EditText valueEntry;
    private EditText dateEntry;

    private TextView nameView;
    private TextView ownerView;
    private TextView playerView;
    private TextView roleView;
    private TextView conditionView;
    private TextView numberView;
    private TextView yearView;
    private TextView teamView;
    private TextView valueView;
    private TextView dateView;

    private static final String saveChanges = "Save Changes";
    private static final String saveCard = "Save Card";
    private static final String addCardToCollectionTitle = "Add to Collection";
    private static final String addCardToWishlistTitle = "Add to Wishlist";
    private static final String editCardCollectionTitle = "Edit Collection";
    private static final String editCardWishlistTitle = "Edit Wishlist";

    protected void showTextViews(int visibility)
    {
        nameView.setVisibility(visibility);
        ownerView.setVisibility(visibility);
        playerView.setVisibility(visibility);
        roleView.setVisibility(visibility);
        conditionView.setVisibility(visibility);
        numberView.setVisibility(visibility);
        yearView.setVisibility(visibility);
        teamView.setVisibility(visibility);
        valueView.setVisibility(visibility);
        dateView.setVisibility(visibility);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_card);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Bind all of the text elements
        nameView = findViewById(R.id.cardNameView);
        ownerView = findViewById(R.id.ownerView);
        playerView = findViewById(R.id.playerView);
        roleView = findViewById(R.id.roleView);
        conditionView = findViewById(R.id.conditionView);
        numberView = findViewById(R.id.numberView);
        yearView = findViewById(R.id.yearView);
        teamView = findViewById(R.id.teamView);
        valueView = findViewById(R.id.valueView);
        dateView = findViewById(R.id.dateView);

        // Bind all of the interactable elements
        image = findViewById(R.id.cardImage);
        chooseImageButton = findViewById(R.id.chooseImageButton);
        saveCardButton = findViewById(R.id.saveButton);
        nameEntry = findViewById(R.id.nameEntry);
        ownerEntry = findViewById(R.id.ownerEntry);
        playerEntry = findViewById(R.id.playerEntry);
        roleEntry = findViewById(R.id.roleEntry);
        conditionEntry = findViewById(R.id.conditionEntry);
        numberEntry = findViewById(R.id.numberEntry);
        yearEntry = findViewById(R.id.yearEntry);
        teamEntry = findViewById(R.id.teamEntry);
        valueEntry = findViewById(R.id.valueEntry);
        dateEntry = findViewById(R.id.dateEntry);

        // Set up drawer
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        DrawerListener listen = new DrawerListener(this, drawer);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(listen);

        // Set up intent data
        if(savedInstanceState == null)
        {
            isWishlist = getIntent().getBooleanExtra("wishlist", false);
            addingCard = getIntent().getBooleanExtra("add", false);
            if(!addingCard)
            {
                data = getIntent().getExtras().getParcelable("card");
            }
            else
            {
                data = null;
            }
        }
        else
        {
            isWishlist = savedInstanceState.getBoolean("wishlist");
            addingCard = savedInstanceState.getBoolean("add");
            if(!addingCard)
            {
                data = getIntent().getExtras().getParcelable("card");
            }
            else
            {
                data = null;
            }
        }

        // Set the title
        if(isWishlist)
        {
            if(addingCard)
            {
                setTitle(addCardToWishlistTitle);
            }
            else
            {
                setTitle(editCardWishlistTitle);
            }
        }
        else
        {
            if(addingCard)
            {
                setTitle(addCardToCollectionTitle);
            }
            else
            {
                setTitle(editCardCollectionTitle);
            }
        }

        // If we're editing a card, map the data in the card to the entered fields
        if(data != null)
        {
            // TODO: fix the image
            saveCardButton.setText(saveChanges);

            nameEntry.setText(data.name);
            ownerEntry.setText(data.owner);
            playerEntry.setText(data.name); // FIXME: ??
            roleEntry.setText(data.role);
            conditionEntry.setText(data.condition);
            numberEntry.setText(data.number);
            yearEntry.setText(Integer.toString(data.year));
            teamEntry.setText(data.team);
            valueEntry.setText(Double.toString(data.value));
            dateEntry.setText(data.dateAcquired);

            // Show the labels if we're in Edit view
            showTextViews(View.VISIBLE);

            // TODO: add listener for the save button
        }
        else
        {
            saveCardButton.setText(saveCard);

            // Don't show the labels if we're in Create view
            showTextViews(View.GONE);

            // TODO: add listener for the save button
        }
    }

}