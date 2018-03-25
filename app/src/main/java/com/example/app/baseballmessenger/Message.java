package com.example.app.baseballmessenger;

import android.media.Image;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by elli on 3/15/18.
 */

public class Message {
    public String uuid;
    public String chat;
    public String sender;
    public String reciever;
    public String text;

    public Message(){
        // Default constructor required for calls to DataSnapshot.getValue(Message.class)
    }

    public Message(String u, String cha, String send, String recieve, String data){
        uuid = u;
        chat = cha;
        sender = send;
        reciever = recieve;
        text = data;
    }

    public void updateFirebase(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("messages").child(chat).child(uuid).setValue(this);
    }

    @Override
    public String toString(){
        return uuid + ":" + text;
    }
}
