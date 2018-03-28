package com.example.app.baseballmessenger;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by pr4h6n on 2/25/18.
 */

public class UserDetails {
    static String chatWith = ""; //Stores the other user's UID in chat conversation
    static String tradeWith = ""; //Stores the other user's UID in existing trade
    static String selectedUserTrade = ""; //Stores the other user's UID in new trade
    static String tradeNumber = ""; //Stores the trade number of existing trade
    static FirebaseUser currentUser; //Stores the current user's data
    static HashMap<String, String> hashMap = new HashMap<>(); //Stores user UID-email address lookup table

    static int card_mode = 2; //received = 1, sent = 0, startup = 2 | used to determine if cards selected are for received or sent cards list
}

