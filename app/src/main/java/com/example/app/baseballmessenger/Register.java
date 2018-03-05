package com.example.app.baseballmessenger;

import android.app.ProgressDialog;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by pr4h6n on 2/25/18.
 */

public class Register extends AppCompatActivity {
    EditText emailAddress, password;
    Button registerButton;
    String email, pass;
    TextView login;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_register);

        emailAddress = (EditText) findViewById(R.id.email_address);
        password = (EditText) findViewById(R.id.password);
        registerButton = (Button) findViewById(R.id.registerButton);
        login = (TextView) findViewById(R.id.login);

        Firebase.setAndroidContext(this);

        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this, Login.class));
            }
        });

        //Creates registration for new user and signs them in automatically
        registerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                email = emailAddress.getText().toString();
                pass = password.getText().toString();

                if(email.equals(""))
                {
                    emailAddress.setError("Email address cannot be blank");
                }
                else if(pass.equals("")) {
                    password.setError("Password cannot be blank");
                }
                else if(pass.length()<5)
                {
                    password.setError("Password must be at least 5 characters long");
                }
                else
                {
                    mAuth.createUserWithEmailAndPassword(email, pass)
                            .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful())
                                    {
                                        // Sign in success, update UI with the signed-in user's information
                                        UserDetails.currentUser = mAuth.getCurrentUser();

                                    String url = "https://baseballmessenger-afdea.firebaseio.com/users.json";

                                    //Stores the new user's data in /users directory
                                    StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                                        @Override
                                        public void onResponse(String s)
                                        {
                                            Firebase reference = new Firebase("https://baseballmessenger-afdea.firebaseio.com/users/" + UserDetails.currentUser.getUid());
                                            reference.child("cards").setValue("");
                                            reference.child("wishlist").setValue("");
                                            reference.child("email").setValue(email);

                                            Toast.makeText(Register.this, "Registration successful", Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(Register.this, Users.class));
                                        }
                                    }, new Response.ErrorListener(){
                                        @Override
                                        public void onErrorResponse(VolleyError volleyError)
                                        {
                                            System.out.println("" + volleyError);
                                        }
                                    });

                                    RequestQueue rQueue = Volley.newRequestQueue(Register.this);
                                    rQueue.add(request);

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(Register.this, "Authentication failed.",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                }
            }
        });
    }
}
