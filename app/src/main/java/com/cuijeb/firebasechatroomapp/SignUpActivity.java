package com.cuijeb.firebasechatroomapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    // private DatabaseReference usersRef;

    // different views
    private EditText emailText;
    private EditText passwordText;
    private Button signupButton;

    //Tags
    private final String AUTH = "Authentication Sign Up";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize the FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        // initialize the different views
        emailText = findViewById(R.id.editTextTextEmailAddress);
        passwordText = findViewById(R.id.editTextTextPassword);
        signupButton = findViewById(R.id.makeAccountButton);

        // Add on click listeners
        signupButton.setOnClickListener(this);

        // get the data base
        // usersRef = database.getReference("users");
    }

    // sign up method
    private void signUp(String email, String password) {
        Log.d(AUTH, "SIGN UP");
        // Sign up

        if (!validateForm()) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // if successful
                        if (task.isSuccessful()) {
                            Log.d(AUTH, "signUp success");
                            // Tell user sign up success
                            Toast.makeText(getApplicationContext(),
                                    "Sign Up Successful!",
                                    Toast.LENGTH_SHORT).show();
                            // Make the user and put in data base
                            FirebaseUser newUser = mAuth.getCurrentUser();
                            User user = new User(newUser.getUid(), email.substring(0, email.indexOf("@")), null);
                            DatabaseReference databaseReference = database.getReference();
                            databaseReference.child("users/" + user.userId + "/userId").setValue(user.userId);
                            databaseReference.child("users/" + user.userId + "/userName").setValue(user.userName);
                            // Should i log user out to let them sign in? yes
                            mAuth.signOut();
                            // Go back to sign in activity
                            finish();
                        } else {
                            // if sign in fails
                            // Log debug
                            // show toast
                            FirebaseAuthException e = (FirebaseAuthException )task.getException();
                            Log.d(AUTH, "signUp failed", e);
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Sign Up failed! Email might already be in use",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
                });
    }

    // Check if user typed in something for email and password
    private boolean validateForm(){
        if (TextUtils.isEmpty(emailText.getText().toString())) {
            emailText.setError("Required!");
            return false;
        } else {
            emailText.setError(null);
        }

        if (TextUtils.isEmpty(passwordText.getText().toString())) {
            passwordText.setError("Required!");
            return false;
        }
        // If password too short
        else if (passwordText.getText().toString().length() < 6) {
            passwordText.setError("Password too short!");
            return false;
        } else {
            emailText.setError(null);
        }

        return true;

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.makeAccountButton) {
            signUp(
                    emailText.getText().toString(),
                    passwordText.getText().toString()
            );
        }
    }
}