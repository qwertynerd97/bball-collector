package com.example.app.baseballmessenger;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
    private EditText roleEntry;
    private EditText conditionEntry;
    private EditText numberEntry;
    private EditText yearEntry;
    private EditText teamEntry;
    private EditText valueEntry;
    private EditText dateEntry;

    private TextView nameView;
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

    private final int PICK_IMAGE = 127;

    protected void showTextViews(int visibility)
    {
        nameView.setVisibility(visibility);
        roleView.setVisibility(visibility);
        conditionView.setVisibility(visibility);
        numberView.setVisibility(visibility);
        yearView.setVisibility(visibility);
        teamView.setVisibility(visibility);
        valueView.setVisibility(visibility);
        dateView.setVisibility(visibility);
    }

    protected void fillCardData(Card data)
    {
        data.name = nameEntry.getText().toString();
        data.owner = Handoff.currentUser.uuid;
        data.role = roleEntry.getText().toString();
        data.condition = conditionEntry.getText().toString();
        data.number = Integer.parseInt(numberEntry.getText().toString());
        data.year = Integer.parseInt(yearEntry.getText().toString());
        data.team = teamEntry.getText().toString();
        data.value = Double.parseDouble(valueEntry.getText().toString());
        data.dateAcquired = dateEntry.getText().toString();


        Handoff.currentUser.value += data.value;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_card);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Bind all of the text elements
        nameView = findViewById(R.id.cardNameView);
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
                Handoff.currentUser.numWishlist++;
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
                Handoff.currentUser.numCollection++;
            }
            else
            {
                setTitle(editCardCollectionTitle);
            }
        }

//        // This block deals with the date field in the card
//        dateEntry.addTextChangedListener(new TextWatcher() {
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//            }
//        });
        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");

                // Do this if you need to be able to open the returned URI as a stream
                // (for example here to read the image data).
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                Intent finalIntent = Intent.createChooser(intent, "Select card image");

                startActivityForResult(finalIntent, PICK_IMAGE);
            }
        });

        // If we're editing a card, map the data in the card to the entered fields
        if(data != null) {
            final long ONE_MEGABYTE = 1024 * 1024;
            data.imageRef().getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    image.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
            saveCardButton.setText(saveChanges);

            nameEntry.setText(data.name);
            roleEntry.setText(data.role);
            conditionEntry.setText(data.condition);
            numberEntry.setText(Integer.toString(data.number));
            yearEntry.setText(Integer.toString(data.year));
            teamEntry.setText(data.team);
            valueEntry.setText(Double.toString(data.value));
            dateEntry.setText(data.dateAcquired);

            Handoff.currentUser.value -= data.value;

                    // Show the labels if we're in Edit view
            showTextViews(View.VISIBLE);

            // The Save Card button will be triggering an Edit action, not a New Card action.
            saveCardButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View parent) {
                    // Gather data from the fields and stuff them into the card data
                    fillCardData(data);

                    // Make the necessary changes in Firebase
                    data.updateFirebase();

                    // Go to card detail
                    Intent i=new Intent(AddEditCardActivity.this, CardDetailActivity.class);
                    i.putExtra("card", data);
                    i.putExtra("update_delete_access", true);
                    i.putExtra("wishlist", isWishlist);
                    startActivity(i);
                }
            });
        }
        else {
            saveCardButton.setText(saveCard);

            // Don't show the labels if we're in Create view
            showTextViews(View.GONE);

            // The Save Card button will be triggering a New Card action, not an Edit Card action.
            saveCardButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (conditionEntry.getText().toString().trim().equals(""))
                    {
                        conditionEntry.setError("Condition cannot be blank");
                    }
                    else if (roleEntry.getText().toString().trim().equals(""))
                    {
                        roleEntry.setError("Role cannot be blank");
                    }
                    else if (numberEntry.getText().toString().trim().equals(""))
                    {
                        numberEntry.setError("Number cannot be blank");
                    }
                    else if (dateEntry.getText().toString().trim().equals(""))
                    {
                        dateEntry.setError("Date cannot be blank");
                    }
                    else if (yearEntry.getText().toString().trim().equals(""))
                    {
                        yearEntry.setError("Year cannot be blank");
                    }
                    else if(teamEntry.getText().toString().trim().equals(""))
                    {
                        teamEntry.setError("Team cannot be blank");
                    }
                    else if (nameEntry.getText().toString().trim().equals(""))
                    {
                        nameEntry.setError("Name cannot be blank");
                    }
                    else if (valueEntry.getText().toString().trim().equals("")) {
                        valueEntry.setError("Value cannot be blank");
                    }
                    else {
                        // Create Card
                        Card c = new Card("", Handoff.currentUser.uuid, "", "", 0, "", "", 0.0, 0, "", true, false, "cardDefault.jpg");
                        c.uuid = c.generateUUID();
                        c.owner = Handoff.currentUser.uuid;
                        c.condition = conditionEntry.getText().toString();
                        c.role = roleEntry.getText().toString();
                        c.number = Integer.parseInt(numberEntry.getText().toString());
                        c.dateAcquired = dateEntry.getText().toString();
                        c.year = Integer.parseInt(yearEntry.getText().toString());
                        c.team = teamEntry.getText().toString();
                        c.name = nameEntry.getText().toString();
                        c.value = Double.parseDouble(valueEntry.getText().toString());
                        c.lockstatus = false;
                        c.inCollection = !isWishlist;
                        c.updateFirebase();


                        Handoff.currentUser.value += c.value;

                                // Go to card list
                        Intent i = new Intent(AddEditCardActivity.this, CardListActivity.class);
                        i.putExtra("update_delete_access", true);
                        i.putExtra("wishlist", isWishlist);
                        startActivity(i);
                    }
                }
            });
        }
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case PICK_IMAGE: {
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        ByteArrayOutputStream buffer = new ByteArrayOutputStream(bitmap.getWidth() * bitmap.getHeight());
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, buffer);
                        byte[] bytes = buffer.toByteArray();

                        final String fileName = selectedImage.getLastPathSegment();
                        data.fileName = fileName;

                        UploadTask uploadTask = data.imageRef().putBytes(bytes);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(AddEditCardActivity.this, "Sucessfully uploaded image to Firebase!",Toast.LENGTH_LONG);
                            }
                        });

                        image.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
