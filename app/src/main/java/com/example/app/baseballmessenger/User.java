package com.example.app.baseballmessenger;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

/**
 * User is an encapsulating class that provides access to User data from the Firebase Database.
 * User can be passed between Activities
 * Created by elli on 3/13/18.
 */
public class User implements Parcelable {
    /**
     * The Universally unique identifier for a user
     */
    public String uuid;
    /**
     * The name to display on the user's profile, currently not in use
     */
    public String displayName;
    /**
     * The login email address
     */
    public String email;
    /**
     * The combined value of all of the user's cards
     */
    public double value;
    /**
     * The number of cards in a user's collection
     */
    public int numCollection;
    /**
     * The number of cards in a user's wishlist
     */
    public int numWishlist;
    /**
     * The list of all chats that a user is in. The key is the uuid for the chat, and the
     * Boolean is a dummy value that is not used.  Chat uuids are store in this manner to comply
     * with the Firebase JSON format.
     */
    public Map<String, Boolean> chats;
    /**
     * The list of all trades that a user is in. The key is the uuid for the trade, and the
     * Boolean is a dummy value that is not used.  Trade uuids are store in this manner to comply
     * with the Firebase JSON format.
     */
    public Map<String, Boolean> trades;
    /**
     * The file name of the user's profile image. Profile images are stored in Firebase Storage
     * at /users/{imagename}
     */
    public String imageName;

    /**
     * The Parcelable Creator, for passing users between Activities
     */
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
     * After creation, Firebase fills out the attributes with the valuse from the database
     */
    public User(){
        uuid = "---";
        displayName = "blank faliure";
        email = "blank@terrible.com";
        value = 0.00;
        numCollection = 0;
        numWishlist = 0;
        chats = new HashMap<>();
        trades = new HashMap<>();
        imageName = "default";
    }

    /**
     * Creates a User from a Parcel.
     * This is used to pass users between Activities
     * @param in The Android Parcel to build the user
     */
    public User(Parcel in){
        String[] data= new String[9];
        in.readStringArray(data);

        uuid = data[0];
        displayName = data[1];
        email = data[2];
        value = Double.parseDouble(data[3]);
        numCollection = Integer.parseInt(data[4]);
        numWishlist = Integer.parseInt(data[5]);
        chats = parseMap(data[6]);
        trades = parseMap(data[7]);
        imageName = data[8];
    }

    /**
     * Creates a new user with the given data. This is used when a new user registers
     * @param u The uuid for the user
     * @param name The username
     * @param mail The user's email
     * @param val The combined value of all of the user's cards
     * @param coll The number of cards in the user's collection
     * @param wish The number of cards in the user's wishlist
     * @param pic The filename of the user's profile picture
     */
    public User(String u, String name, String mail, double val, int coll, int wish, String pic){
        uuid = u;
        displayName = name;
        email = mail;
        value = val;
        numCollection = coll;
        numWishlist = wish;
        chats = new HashMap<>();
        trades = new HashMap<>();
        imageName = pic;
    }

    /**
     * Updates the Firebase database with the values for this user
     */
    public void updateFirebase(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("users").child(uuid).setValue(this);
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
     * Writes the contents of this user to a Parcel, to pass between Activities
     * @param dest The Parcel that will be passed
     * @param flags Special information for the parcel
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        String[] data= new String[9];

        data[0]= uuid;
        data[1] = displayName;
        data[2] = email;
        data[3]= value + "";
        data[4] = numCollection + "";
        data[5] = numWishlist + "";
        data[6] = chats.toString();
        data[7] = trades.toString();
        data[8] = imageName;

        dest.writeStringArray(data);
    }

    /**
     * Returns a String representation of this User
     * @return
     */
    @Override
    public String toString(){
        return uuid + ":" + email;
    }

    /**
     * Parses a HashMap from a String.  Used when de-parceling a User
     * @param parse The String representation of the HashMap
     * @return The newly-created HashMap
     */
    private HashMap<String, Boolean> parseMap(String parse){
        HashMap<String, Boolean> temp = new HashMap<>();
        if(parse.equals("{}")){
            return temp;
        }

        // Clear curly braces from hashmap
        parse = parse.substring(1,parse.length()-1);

        // Extract each key-value pair
        String[] items = parse.split(", ");


        for(String item: items){
            String[] kv = item.split("=");
            temp.put(kv[0],Boolean.parseBoolean(kv[1]));
        }

        return temp;
    }
}
