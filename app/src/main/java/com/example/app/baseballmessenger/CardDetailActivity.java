package com.example.app.baseballmessenger;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * Created by pr4h6n on 4/1/18.
 */

public class CardDetailActivity extends AppCompatActivity {

    private Card data;
    private DrawerLayout drawer;
    private Button editbutton;
    private Button deletebutton;
    private boolean isWishlist;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        data = getIntent().getExtras().getParcelable("card");

        final ImageView pic = findViewById(R.id.cardImage);
        final long ONE_MEGABYTE = 1024 * 1024;
        data.imageRef().getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                pic.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        TextView cardName = findViewById(R.id.cardName);
        cardName.setText(data.name);

        TextView value = findViewById(R.id.value);
        value.setText(data.value + "");

        TextView owner = findViewById(R.id.owner);
        TextView ownerHeader = findViewById(R.id.ownerView);
        owner.setText(data.owner + "");

        if(data.owner.equals(Handoff.currentUser.uuid))
        {
            String location = data.inCollection ? "your collection.":"your wishlist.";
            owner.setVisibility(View.GONE);
            ownerHeader.setText("This card is currently in " + location);

        }

        TextView condition = findViewById(R.id.condition);
        condition.setText(data.condition + "");

        TextView number = findViewById(R.id.number);
        number.setText(data.number + "");

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

        // Determine whether we are in the wishlist or not
        if(savedInstanceState == null)
        {
            isWishlist = getIntent().getBooleanExtra("wishlist", false);
        }
        else
        {
            isWishlist = savedInstanceState.getBoolean("wishlist");
        }

        // Set up the edit and delete buttons
        editbutton = findViewById(R.id.editCard);
        deletebutton = findViewById(R.id.deleteCard);
        if(data.lockstatus)
        {   // If card is in a pending trade, don't allow clicking on the delete or edit buttons
            editbutton.setEnabled(false);
            deletebutton.setEnabled(false);
        }
        else
        {   // We are allowed to touch things, all is fine
            editbutton.setEnabled(true);
            deletebutton.setEnabled(true);

            // Add a click listener also
            editbutton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Code here executes on main thread after user presses button
                    Intent i = new Intent(CardDetailActivity.this, AddEditCardActivity.class);
                    i.putExtra("wishlist", isWishlist);
                    i.putExtra("add", false);
                    i.putExtra("card", data);

                    startActivity(i);
                }
            });

            deletebutton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    AlertDialog alertDialog = new AlertDialog.Builder(CardDetailActivity.this).create();
                    alertDialog.setTitle("Delete Card?");
                    alertDialog.setMessage("Are you sure you want to delete this card? This action cannot be undone.");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Delete",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Called when clicking the DELETE button on the modal dialog
                                    // Dismiss the dialog
                                    dialog.dismiss();

                                    // Tell the card to go away
                                    Card.deleteCard(data.owner, !isWishlist, data.uuid);

                                    // Go to the card list activity
                                    Intent i = new Intent(CardDetailActivity.this, CardListActivity.class);
                                    i.putExtra("wishlist", isWishlist);
                                    startActivity(i);
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Called when clicking CANCEL on the modal dialog (just dismiss it)
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            });
        }
    }
}