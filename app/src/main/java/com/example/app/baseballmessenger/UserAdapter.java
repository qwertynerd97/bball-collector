package com.example.app.baseballmessenger;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by elli on 3/13/18.
 */

public class UserAdapter extends ArrayAdapter {

    ArrayList<User> users;
    int resourceId;

    public UserAdapter(Context context, int textViewResourceId, ArrayList<User> objects) {
        super(context, textViewResourceId, objects);
        users = objects;
        resourceId = textViewResourceId;
    }
    public UserAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        resourceId = textViewResourceId;
        users = new ArrayList<User>();
        users.add(new User("ABC","dummy01","blah@gmail.com",200.0,3,3));
        users.add(new User("ABC","dummy02","blah@gmail.com",200.0,3,3));
        users.add(new User("ABC","dummy03","blah@gmail.com",200.0,3,3));
        users.add(new User("ABC","dummy04","blah@gmail.com",200.0,3,3));
        users.add(new User("ABC","dummy05","blah@gmail.com",200.0,3,3));
        users.add(new User("ABC","dummy06","blah@gmail.com",200.0,3,3));
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(resourceId, null);
        TextView userName = v.findViewById(R.id.user_name);
        userName.setText(users.get(position).email);
        return v;
    }
}
