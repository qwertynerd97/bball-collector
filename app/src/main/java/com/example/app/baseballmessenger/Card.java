package com.example.app.baseballmessenger;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/**
 * Card is an encapsulating class that provides access to Card data from the Firebase Database.
 * Card can be passed between Activities
 * All the cards in a user's collection are stored at /cards/:owner/collection/
 * All the cards in a user's wishlist are stored at /cards/:owner/wishlist/
 * Created by pr4h6n on 3/2/18.
 */
public class Card implements Parcelable{
    /**
     * The universally unique identifier for a card
     */
    public String uuid;
    /**
     * The uuid of the user who currently owns or wants this card
     */
    public String owner;
    /**
     * The name of the player, team, or item represented on the card
     */
    public String name;
    /**
     * The condition the card is in
     */
    public String condition;
    /**
     * The card's print number
     */
    public int number;
    /**
     * The role of the player or item on the card
     */
    public String role;
    /**
     * The team the card is affiliated with
     */
    public String team;
    /**
     * The monetary value of the card, in USD
     */
    public double value;
    /**
     * The year the card was released
     */
    public int year;
    /**
     * The date that the card was acquired by its current owner
     */
    public String dateAcquired;
    /**
     * Indicates whether the card is in a collection (true) or a wishlist (false)
     */
    public boolean inCollection;

    /**
     * The Parcelable Creator, for passing Cards between Activities
     */
    public static final Parcelable.Creator<Card> CREATOR= new Parcelable.Creator<Card>() {

        @Override
        public Card createFromParcel(Parcel source) {
            return new Card(source);  //using parcelable constructor
        }

        @Override
        public Card[] newArray(int size) {
            return new Card[size];
        }
    };

    /**
     * Creates an empty card
     * This is just a dummy method for DataSnapshot.getValue(Card.class)
     * After creation, Firebase fills out the attributes with the values from the database
     */
    public Card(){
        uuid = "---";
        owner = "---";
        name = "NO NAME";
        condition = "unknown";
        number = -1;
        role = "none";
        team = "none";
        value = 0.00;
        year = -1;
        dateAcquired = "0/0/00";
        inCollection = false;
    }

    /**
     * Creates a Card from a Parcel.
     * This is used to pass cards between Activities
     * @param in The Android Parcel to build the card
     */
    public Card(Parcel in){
        String[] data= new String[11];
        in.readStringArray(data);

        uuid = data[0];
        owner = data[1];
        name = data[2];
        condition = data[3];
        number = Integer.parseInt(data[4]);
        role = data[5];
        team = data[6];
        value = Double.parseDouble(data[7]);
        year = Integer.parseInt(data[8]);
        dateAcquired = data[9];
        inCollection = Boolean.parseBoolean(data[10]);
    }

    /**
     * Creates a new card with the given data. This is used when a user makes a new card
     * @param uuid The uuid for the card
     * @param owner The uuid of the user that made the card
     * @param name The name of the card
     * @param cond The condition the card is in
     * @param num The print number of the card
     * @param role The role of the person on the card
     * @param team The team affiliated with the card
     * @param val The monetary value of the card
     * @param year The year the card was released
     * @param date The date the card was acquired
     */
    public Card(String uuid, String owner, String name, String cond, int num, String role, String team, double val, int year, String date, boolean coll){
        this.uuid = uuid;
        this.owner = owner;
        this.name = name;
        this.condition = cond;
        this.number = num;
        this.role = role;
        this.team = team;
        this.value = val;
        this.year = year;
        this.dateAcquired = date;
        this.inCollection = coll;
    }

    /**
     * Get card from database from uuid
     * @param user The uuid of the user who owns the card
     * @param inCollection Where the card is located
     * @param uuid The uuid of the card to retrieve
     */
    public Card(String user, boolean inCollection, String uuid){
        DatabaseReference reference = Card.databaseReference(user, inCollection).child(uuid);
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Card.this.setValues(dataSnapshot.getValue(Card.class));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        reference.addListenerForSingleValueEvent(userListener);
    }

    /**
     * Sets card values based on another card
     * @param other Values to use for this card
     */
    private void setValues(Card other){
        this.uuid = other.uuid;
        this.owner = other.owner;
        this.name = other.name;
        this.condition = other.condition;
        this.number = other.number;
        this.role = other.role;
        this.team = other.team;
        this.value = other.value;
        this.year = other.year;
        this.dateAcquired = other.dateAcquired;
        this.inCollection = other.inCollection;
    }

    /**
     * Updates the Firebase database with the values for this user
     */
    public void updateFirebase(){
        String location = (inCollection ? "collection" : "wishlist");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("cards").child(owner).child(location).child(uuid).setValue(this);
    }

    /**
     * Describes the parcelable contents as an  integer
     * @return 0
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Writes the contents of this card to a Parcel, to pass between Activities
     * @param dest The Parcel that will be passed
     * @param flags Special information for the parcel
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        String[] data = new String[11];

        data[0] = uuid;
        data[0] = owner;
        data[0] = name;
        data[0] = condition;
        data[0] = number + "";
        data[0] = role;
        data[0] = team;
        data[0] = value + "";
        data[0] = year + "";
        data[0] = dateAcquired;
        data[0] = inCollection + "";

        dest.writeStringArray(data);
    }

    /**
     * Returns a String representation of this card
     * @return The uuid + name of the card
     */
    @Override
    public String toString(){
        return uuid + ":" + name;
    }

    /**
     * Gets the database location for a particular card
     * @return The Firebase location for the particular card
     */
    public DatabaseReference dbReference(){
        String location = (inCollection ? "collection" : "wishlist");
        return FirebaseDatabase.getInstance().getReference("cards").child(owner).child(location).child(uuid);
    }

    /**
     * Gets the database location for a group of cards
     * @param user The users that the cards belong to
     * @param collection true if retrieving collection, false if retrieving wishlist
     * @return The Firebase location for the group of cards
     */
    public static DatabaseReference databaseReference(String user, boolean collection){
        String location = (collection ? "collection" : "wishlist");
        return FirebaseDatabase.getInstance().getReference("cards").child(user).child(location);
    }
}