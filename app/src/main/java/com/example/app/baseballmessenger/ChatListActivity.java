package com.example.app.baseballmessenger;

import android.arch.persistence.room.Database;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by pr4h6n on 3/29/18.
 */

public class ChatListActivity extends AppCompatActivity {

    ArrayList<User> users;
    ListView conversations;
    ArrayList<String> al;
    HashMap<String, User> allUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        conversations = (ListView) findViewById(R.id.conversations_list);

        allUsers = new HashMap<>();
        users = new ArrayList<>();
        DatabaseReference ref = User.databaseReference();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot userSnapshot:dataSnapshot.getChildren())
                {
                    User u = userSnapshot.getValue(User.class);
                    if(!u.uuid.equals(Handoff.currentUser.uuid))
                    {
                        users.add(userSnapshot.getValue(User.class));
                    }
                }

                al = new ArrayList<>();
                for(int i = 0; i < users.size(); i++)
                {
                    al.add(users.get(i).email);
                    allUsers.put(users.get(i).email, users.get(i));
                }

                conversations.setAdapter(new ArrayAdapter<String>(ChatListActivity.this, android.R.layout.simple_list_item_1, al));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        conversations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(ChatListActivity.this, SingleChatActivity.class);
                i.putExtra("chattingWith", allUsers.get(al.get(position)));
                startActivity(i);
            }
        });

    }

}
