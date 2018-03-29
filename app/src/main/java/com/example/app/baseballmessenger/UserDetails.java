package com.example.app.baseballmessenger;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by pr4h6n on 2/25/18.
 */

//TODO Eliminate UserDetails class and use Intent extras instead
public class UserDetails {

    static String selectedUserTrade = ""; //Stores the other user's UID in new trade
    static HashMap<String, String> hashMap = new HashMap<>(); //Stores user UID-email address lookup table

    static int card_mode = 2; //received = 1, sent = 0, startup = 2 | used to determine if cards selected are for received or sent cards list
}

