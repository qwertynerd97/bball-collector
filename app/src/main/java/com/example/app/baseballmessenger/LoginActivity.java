package com.example.app.baseballmessenger;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity {
    TextView registerUser;
    EditText emailAddress, password;
    Button loginButton;
    String email, pass;
    FirebaseAuth mAuth;
    RelativeLayout progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        registerUser = (TextView) findViewById(R.id.register);
        emailAddress = (EditText) findViewById(R.id.email_address);
        password = (EditText) findViewById(R.id.password);
        loginButton = (Button) findViewById(R.id.loginButton);
        progressBar = (RelativeLayout) findViewById(R.id.auto_login_dialog);

        progressBar.setVisibility(View.GONE);

        registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailAddress.getText().toString();
                pass = password.getText().toString();
                Log.d("Error Result",email + " " + pass);

                if (email.equals(""))
                {
                    emailAddress.setError("Email address cannot be blank");
                }
                else if (pass.equals(""))
                {
                    password.setError("Password cannot be blank");
                }
                else
                {
                    mAuth.signInWithEmailAndPassword(email, pass)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d("Error Result","Tried to auth");
                                    if (task.isSuccessful())
                                    {
                                        Log.d("Error Result","Auth successful");
                                        sharedPref.edit().putString("user_email",email).putString("user_password",pass).apply();
                                        FirebaseDatabase.getInstance().getReference("users").child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot snapshot) {
                                                Handoff.currentUser = snapshot.getValue(User.class);
                                            }
                                            @Override
                                            public void onCancelled(DatabaseError firebaseError) {

                                            }
                                        });
                                        //UserDetails.currentUser = mAuth.getCurrentUser(); //Store current user data (Uid, email address, etc.)
                                        startActivity(new Intent(LoginActivity.this, SearchUsersActivity.class));
                                    } else {
                                        Log.d("Error Result","Auth failed");
                                        Toast.makeText(LoginActivity.this, String.format("Login failed: %s", task.getException().getMessage()),
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }

            }
        });

        // Auto-LoginActivity
        String mail = sharedPref.getString("user_email", "dummyuser@gmail.com");
        String password = sharedPref.getString("user_password", "areallydummypassword");

        if(!mail.equals("dummyuser@gmail.com")) {
            progressBar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(mail, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("Error Result", "Tried to auth");
                            if (task.isSuccessful()) {
                                Log.d("Error Result", "Auth sucessful");
                                FirebaseDatabase.getInstance().getReference("users").child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        Handoff.currentUser = snapshot.getValue(User.class);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError firebaseError) {

                                    }
                                });
                                progressBar.setVisibility(View.GONE);
                                startActivity(new Intent(LoginActivity.this, SearchUsersActivity.class));
                            } else {
                                Log.d("Error Result", "Auth failed");
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }
}

