package com.example.app.baseballmessenger;

import android.media.Image;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.firebase.client.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;

/**
 * Created by elli on 3/13/18.
 */

public class User implements Parcelable {
    public String uuid;
    public String displayName;
    public String email;
    public double value;
    public int numCollection;
    public int numWishlist;
    private Image profileImage;
    private DatabaseReference reference;

    public static final Parcelable.Creator<User> CREATOR= new Parcelable.Creator<User>() {

        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);  //using parcelable constructor
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    /**
     * Creates an empty user
     * This is just a dummy method for DataSnapshot.getValue(User.class)
     */
    public User(){
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
        reference = FirebaseDatabase.getInstance().getReference();
    }

    /**
     * Creates a User from a Parcel
     * @param in The Android Parcel to build the user
     */
    public User(Parcel in){
        reference = FirebaseDatabase.getInstance().getReference();

        String[] data= new String[6];
        in.readStringArray(data);

        uuid = data[0];
        displayName = data[1];
        email = data[2];
        value = Double.parseDouble(data[3]);
        numCollection = Integer.parseInt(data[4]);
        numWishlist = Integer.parseInt(data[5]);
    }

    /**
     * Creates a new user with the given data
     * @param u The uuid for the user
     * @param name The username
     * @param mail The user's email
     * @param val The combined value of all of the user's cards
     * @param coll The number of cards in the user's collection
     * @param wish The number of cards in the user's wishlist
     */
    public User(String u, String name, String mail, double val, int coll, int wish){
        reference = FirebaseDatabase.getInstance().getReference();

        uuid = u;
        displayName = name;
        email = mail;
        value = val;
        numCollection = coll;
        numWishlist = wish;
    }

    /**
     * Updates the realtime database with the values for this user
     */
    public void updateFirebase(){
        reference.child("users").child(uuid).setValue(this);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        String[] data= new String[6];

        data[0]= uuid;
        data[1] = displayName;
        data[2] = email;
        data[3]= value + "";
        data[4] = numCollection + "";
        data[5] = numWishlist + "";

        dest.writeStringArray(data);
    }
}
