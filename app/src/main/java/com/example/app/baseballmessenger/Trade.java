package com.example.app.baseballmessenger;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by elli on 3/25/18.
 */

public class Trade implements Parcelable {
    public String uuid;
    public String requestingUser;
    public String receivingUser;
    public String cardSent;
    public String cardRequested;
    public int status;

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

    public Trade(){
        // Default constructor required for calls to DataSnapshot.getValue(Message.class)
    }

    public Trade(String u, String request, String recieve, String sent, String got, int stat){
        uuid = u;
        requestingUser = request;
        receivingUser = recieve;
        cardSent = sent;
        cardRequested = got;
        status = stat;
    }
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

    public void updateFirebase(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("trades").child(uuid).setValue(this);
    }

    @Override
    public String toString(){
        return uuid + ":" + statusText();
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

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
}
