package com.example.app.baseballmessenger;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class UserDetailActivity extends AppCompatActivity {
    private User data;
    private DrawerLayout drawer;

    private final int PICK_IMAGE = 127;
    private ImageView pic;

    private final String MyProfileString = "My Profile";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        data = getIntent().getParcelableExtra("user");

        Button collectionButton = findViewById(R.id.collectionButton);
        collectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(UserDetailActivity.this, CardListActivity.class);
                i.putExtra("user", data);
                i.putExtra("previous_activity", "UserDetailActivity");
                i.putExtra("isWishlist", false);
                startActivity(i);
            }
        });
        Button wishlistButton = findViewById(R.id.wishlistButton);
        wishlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(UserDetailActivity.this,CardListActivity.class);
                i.putExtra("previous_activity", "UserDetailActivity");
                i.putExtra("isWishlist", true);
                i.putExtra("user", data);
                startActivity(i);
            }
        });
        Button chatButton = findViewById(R.id.chatButton);
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(UserDetailActivity.this,SingleChatActivity.class);
                i.putExtra("chattingWith", data);
                startActivity(i);
            }
        });
        Button tradeButton = findViewById(R.id.tradeButton);
        tradeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(UserDetailActivity.this, NewTradeActivity.class);
                i.putExtra("user", data);
                startActivity(i);
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
                startActivity(new Intent(UserDetailActivity.this, LoginActivity.class));
            }
        });

        Button profileImage = findViewById(R.id.changeImage);

        Log.d("User Detail",Handoff.currentUser + "");
        Log.d("User Detail",data.toString());
        Log.d("User Detail", Handoff.currentUser.toString());

        // Set up visibility for current user vs other cards
        if(data.uuid.equals(Handoff.currentUser.uuid)){
            collectionButton.setVisibility(View.GONE);
            wishlistButton.setVisibility(View.GONE);
            chatButton.setVisibility(View.GONE);
            tradeButton.setVisibility(View.GONE);
            logoutButton.setVisibility(View.VISIBLE);
            profileImage.setVisibility(View.VISIBLE);

            setTitle(MyProfileString);
        }else{
            collectionButton.setVisibility(View.VISIBLE);
            wishlistButton.setVisibility(View.VISIBLE);
            chatButton.setVisibility(View.VISIBLE);
            tradeButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.GONE);
            profileImage.setVisibility(View.GONE);

            if(data.displayName.length() == 0) {   // use email instead of display name
                setTitle(data.email);
            }
            else {
                setTitle(data.displayName);
            }
        }

        pic = findViewById(R.id.userImage);
        final long ONE_MEGABYTE = 1024 * 1024;
        Log.d("UserDetail","image is " + data.fileName);
        data.imageRef().getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                pic.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("User detail","Failed");
            }
        });

        TextView userName = findViewById(R.id.userName);
        userName.setText(data.email);
        TextView value = findViewById(R.id.value);
        value.setText(data.value + "");
        TextView collectionCards = findViewById(R.id.collection);
        collectionCards.setText(data.numCollection + "");
        TextView wishlistCards = findViewById(R.id.wishlist);
        wishlistCards.setText(data.numWishlist + "");

        profileImage.setOnClickListener(new View.OnClickListener() {
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
                        data.updateFirebase();

                        UploadTask uploadTask = data.imageRef().putBytes(bytes);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(UserDetailActivity.this, "Sucessfully uploaded image to Firebase!",Toast.LENGTH_LONG);
                            }
                        });

                        pic.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
