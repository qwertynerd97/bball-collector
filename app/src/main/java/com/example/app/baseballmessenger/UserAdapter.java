package com.example.app.baseballmessenger;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

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
        users.add(new User("ABC","dummy01","blah@gmail.com",200.0,3,3, "dummy.jpg"));
        users.add(new User("ABC","dummy02","blah@gmail.com",200.0,3,3, "dummy.jpg"));
        users.add(new User("ABC","dummy03","blah@gmail.com",200.0,3,3, "dummy.jpg"));
        users.add(new User("ABC","dummy04","blah@gmail.com",200.0,3,3, "dummy.jpg"));
        users.add(new User("ABC","dummy05","blah@gmail.com",200.0,3,3, "dummy.jpg"));
        users.add(new User("ABC","dummy06","blah@gmail.com",200.0,3,3, "dummy.jpg"));
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
        final ImageView pic = v.findViewById(R.id.user_pic);
        final long ONE_MEGABYTE = 1024 * 1024;
        users.get(position).imageRef().getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                pic.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
        TextView userName = v.findViewById(R.id.user_name);
        userName.setText(users.get(position).email);
        return v;
    }
}
