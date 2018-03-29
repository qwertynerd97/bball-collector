package com.example.app.baseballmessenger;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by elli on 3/25/18.
 */

public class Trade implements Parcelable {
    /**
     * The universally unique identifier for a Trade
     */
    public String uuid;
    /**
     * The user that requested the trade
     */
    public String requestingUser;
    /**
     * The user that is reciving the trade
     */
    public String receivingUser;
    /**
     * The card sent in trade
     */
    public String cardSent;
    /**
     * The card requested in the trade
     */
    public String cardRequested;
    /**
     * The current status of the trade
     */
    public int status;

    /**
     * The Parcelable Creator, for passing Trades between Activities
     */
    public static final Parcelable.Creator<Trade> CREATOR= new Parcelable.Creator<Trade>() {

        @Override
        public Trade createFromParcel(Parcel source) {
            return new Trade(source);  //using parcelable constructor
        }

        @Override
        public Trade[] newArray(int size) {
            return new Trade[size];
        }
    };

    /**
     * Creates an empty trade
     * This is just a dummy method for DataSnapshot.getValue(Trade.class)
     * After creation, Firebase fills out the attributes with the values from the database
     */
    public Trade(){
        uuid = "---";
        requestingUser = "---";
        receivingUser = "---";
        cardSent = "---";
        cardRequested = "---";
        status = -1;
    }

    /**
     * Creates a new trade with the given data. This is used when a user requests a new trade
     * @param u The uuid of the trade
     * @param request The requesting user
     * @param recieve The receiving user
     * @param sent The sent card
     * @param got The received card
     * @param stat The current status of the trade
     */
    public Trade(String u, String request, String recieve, String sent, String got, int stat){
        uuid = u;
        requestingUser = request;
        receivingUser = recieve;
        cardSent = sent;
        cardRequested = got;
        status = stat;
    }

    /**
     * Creates a Trade from a Parcel.
     * This is used to pass trades between Activities
     * @param in The Android Parcel to build the trade
     */
    public Trade(Parcel in){
        String[] data= new String[6];
        in.readStringArray(data);

        uuid = data[0];
        requestingUser = data[1];
        receivingUser = data[2];
        cardSent = data[3];
        cardRequested = data[4];
        status = Integer.parseInt(data[5]);
    }

    /**
     * Get trade from database from uuid
     * @param uuid The uuid of the trade to retrieve
     */
    public Trade(String uuid){
        DatabaseReference reference = Trade.databaseReference().child(uuid);
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Trade.this.setValues(dataSnapshot.getValue(Trade.class));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        reference.addListenerForSingleValueEvent(userListener);
    }

    /**
     * Sets trade values based on another trade
     * @param other Values to use for this trade
     */
    private void setValues(Trade other){
        this.uuid = other.uuid;
        this.requestingUser = other.requestingUser;
        this.receivingUser = other.receivingUser;
        this.cardSent = other.cardSent;
        this.cardRequested = other.cardRequested;
        this.status = other.status;
    }

    /**
     * Updates the Firebase database with the values for this trade
     */
    public void updateFirebase(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("trades").child(uuid).setValue(this);
    }

    /**
     * Returns a String representation of this trade
     * @return The uuid + status of the trade
     */
    @Override
    public String toString(){
        return uuid + ":" + statusText();
    }

    /**
     * Returns a String representation of this card's status
     * @return The human-readable status
     */
    public String statusText(){
        switch(status){
            case 0:
                return "Initiated";
            case 1:
                return "Accepted";
            case 2:
                return "Rejected";
            default:
                return "Unidentified Status";
        }
    }

    /**
     * Describes the parcelable contents as an integer
     * @return 0
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Writes the contents of this trade to a Parcel, to pass between Activities
     * @param dest The Parcel that will be passed
     * @param flags Special information for the parcel
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        String[] data= new String[6];

        data[0] = uuid;
        data[1] = requestingUser;
        data[2] = receivingUser;
        data[3] = cardSent;
        data[4] = cardRequested;
        data[5] = status + "";

        dest.writeStringArray(data);
    }

    /**
     * Gets the database location for a particular trade
     * @return The Firebase location for the particular trade
     */
    public DatabaseReference dbReference(){
        return FirebaseDatabase.getInstance().getReference("trades").child(uuid);
    }

    /**
     * Gets the database query for all trades. Because of the way that trades are currently
     * structured, This returns all trades regardless of user, and will then need to be filtered
     * by user, by checking the requestingUser and receivingUser properties
     * @return The Firebase location for all trades
     */
    public static DatabaseReference databaseReference(){
        return FirebaseDatabase.getInstance().getReference("trades");
    }
}
