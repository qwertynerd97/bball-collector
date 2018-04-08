package com.example.app.baseballmessenger;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by pr4h6n on 2/25/18.
 */

public class RegisterActivity extends AppCompatActivity {
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
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

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
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful())
                                    {
                                        // Sign in success, update UI with the signed-in user's information
                                        FirebaseUser currentUser = mAuth.getCurrentUser();
                                        User newUser = new User(currentUser.getUid(),"",currentUser.getEmail(),0,0,0, "icon1.png");
                                        newUser.updateFirebase();
                                        Handoff.currentUser = newUser;
                                        sharedPref.edit().putString("user_email",email).putString("user_password",pass).apply();
                                        Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(RegisterActivity.this, SearchUsersActivity.class));

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(RegisterActivity.this, String.format("Registration failed: %s", task.getException().getMessage()),
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                }
            }
        });
    }
}
