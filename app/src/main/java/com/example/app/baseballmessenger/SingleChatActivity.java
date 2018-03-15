package com.example.app.baseballmessenger;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pr4h6n on 2/25/18.
 */

public class SingleChatActivity extends AppCompatActivity{
    LinearLayout layout;
    RelativeLayout layout_two;
    ImageView sendButton;
    EditText messageArea;
    ScrollView scrollView;
    Firebase reference_one;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        layout = (LinearLayout) findViewById(R.id.layout1);
        layout_two = (RelativeLayout) findViewById(R.id.layout2);
        sendButton = (ImageView) findViewById(R.id.sendButton);
        messageArea = (EditText) findViewById(R.id.messageArea);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        Firebase.setAndroidContext(this);

        //Determine correct order of UIDs
        if(UserDetails.chatWith.compareTo(UserDetails.currentUser.getUid()) > 0)
        {
            reference_one = new Firebase("https://baseballmessenger-afdea.firebaseio.com/messages/" + UserDetails.currentUser.getUid() + "_" + UserDetails.chatWith);
        }
        else
        {
            reference_one = new Firebase("https://baseballmessenger-afdea.firebaseio.com/messages/" + UserDetails.chatWith + "_" + UserDetails.currentUser.getUid());
        }

        sendButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                String messageText = messageArea.getText().toString();

                if(!messageText.equals(""))
                {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", messageText);
                    map.put("user", UserDetails.currentUser.getEmail());

                    reference_one.push().setValue(map); //push message data to Firebase database
                    messageArea.setText("");
                }
            }
        });

        //Update chat conversation to reflect new messages
        reference_one.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                String message = map.get("message").toString();
                String email = map.get("user").toString();

                if(email.equals(UserDetails.currentUser.getEmail()))
                {
                    //addMessageBox("You\n" + message, 1);
                }
                else
                {
                    //addMessageBox(email + "\n" + message, 2);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    //Adds message to the chat conversation
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void addMessageBox(String message, int type)
    {
        TextView textView = new TextView(SingleChatActivity.this);
        textView.setText(message);
        textView.setTextAppearance(R.style.fontForTextMessage);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;

        if(type == 1)
        {
            lp2.gravity = Gravity.RIGHT;
            textView.setBackgroundResource(R.drawable.bubble_in);
        }
        else
        {
            lp2.gravity = Gravity.LEFT;
            textView.setBackgroundResource(R.drawable.bubble_out);
        }

        textView.setLayoutParams(lp2);
        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }
}
