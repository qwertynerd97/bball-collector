package com.example.app.baseballmessenger;

import android.media.Image;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by elli on 3/15/18.
 */

public class Message {
    /**
     * The universally unique identifier for a message
     */
    public String uuid;
    /**
     * The chat that the message is in
     */
    public String chat;
    /**
     * The user that sent the message
     */
    public String sender;
    /**
     * The user that received the message
     */
    public String reciever;
    /**
     * The text of the message
     */
    public String text;

    /**
     * Creates an empty message
     * This is just a dummy method for DataSnapshot.getValue(Message.class)
     * After creation, Firebase fills out the attributes with the values from the database
     */
    public Message(){
        uuid = "---";
        chat = "---";
        sender = "---";
        reciever = "---";
        text = "NO TEXT";
    }

    /**
     * Creates a new card with the given data. This is used when a user sends a new message
     * @param u The uuid for the message
     * @param cha The chat uuid that the message is in
     * @param send The uuid of the sending user
     * @param recieve The uuid of the receiving user
     * @param data The text of the message
     */
    public Message(String u, String cha, String send, String recieve, String data){
        uuid = u;
        chat = cha;
        sender = send;
        reciever = recieve;
        text = data;
    }

    /**
     * Get message from database from uuid
     * @param chat The uuid of the chat that holds the message
     * @param uuid The uuid of the message to retrieve
     */
    public Message(String chat, String uuid){
        DatabaseReference reference = Message.databaseReference(chat).child(uuid);
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Message.this.setValues(dataSnapshot.getValue(Message.class));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        reference.addListenerForSingleValueEvent(userListener);
    }

    /**
     * Sets message values based on another message
     * @param other Values to use for this message
     */
    private void setValues(Message other){
        this.uuid = other.uuid;
        this.chat = other.chat;
        this.sender = other.sender;
        this.reciever = other.reciever;
        this.text = other.text;
    }

    /**
     * Updates the Firebase database with the values for this message
     */
    public void updateFirebase(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("messages").child(chat).child(uuid).setValue(this);
    }

    /**
     * Returns a String representation of this message
     * @return The uuid + text of the message
     */
    @Override
    public String toString(){
        return uuid + ":" + text;
    }

    /**
     * Gets the database location for a particular message
     * @return The Firebase location for the particular message
     */
    public DatabaseReference getdatabaseReference(){
        return FirebaseDatabase.getInstance().getReference("messages").child(chat).child(uuid);
    }

    /**
     * Gets the database location for a group of messages
     * @param chat The uuid of the chat that the messages are in
     * @return The Firebase location for a group of messages
     */
    public static DatabaseReference databaseReference(String chat){
        return FirebaseDatabase.getInstance().getReference("messages").child(chat);
    }
}
