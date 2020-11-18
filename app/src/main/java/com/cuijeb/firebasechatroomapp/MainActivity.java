package com.cuijeb.firebasechatroomapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;

    // different views
    private TextView statusText;
    private EditText emailText;
    private EditText passwordText;
    private Button signinButton;
    private Button signupButton;
    private Button signoutButton;

    //Tags
    private final String AUTH = "Authentication";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        // initialize the different views
        statusText = findViewById(R.id.signinstatusTextView);
        emailText = findViewById(R.id.editTextTextEmailAddress);
        passwordText = findViewById(R.id.editTextTextPassword);
        signinButton = findViewById(R.id.signinButton);
        signupButton = findViewById(R.id.signupButton);
        signoutButton = findViewById(R.id.signoutButton);

        // Add on click listeners
        signinButton.setOnClickListener(this);
        signupButton.setOnClickListener(this);
        signoutButton.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser, false);
    }


    @Override
    protected void onStop() {
        super.onStop();
        signOut();
    }

    private void updateUI(FirebaseUser user, boolean newUser) {
        if (user != null) {
            // Do something based on logged in
            if(newUser) {
                Toast.makeText(
                        getApplicationContext(),
                        "Succesfully signed up!",
                        Toast.LENGTH_SHORT)
                        .show();
            }
            Toast.makeText(
                    getApplicationContext(),
                    "Logged in",
                    Toast.LENGTH_SHORT)
                    .show();
            statusText.setText("Logged in");
        } else {
            // Do something based on not logged in
            if(newUser) {
                Toast.makeText(
                        getApplicationContext(),
                        "Email already in use!",
                        Toast.LENGTH_SHORT)
                        .show();
            }
            Toast.makeText(
                    getApplicationContext(),
                    "Please sign in",
                    Toast.LENGTH_SHORT)
                    .show();
            statusText.setText("Not logged in");
        }
    }

    // sign in method
    private void signIn(String email, String password) {
        Log.d(AUTH, "SIGN IN");
        if (!validateForm()) {
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // if successful
                        if (task.isSuccessful()) {
                            Log.d(AUTH, "signIn success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user, false);
                        } else {
                            // if sign in fails
                            Log.d(AUTH, "signIn failed");
                            Toast.makeText(getApplicationContext(), "Sign in failed!", Toast.LENGTH_SHORT);
                            updateUI(null, false);
                        }
                    }
                });
    }

    // sign up method
    private void signUp(String email, String password) {
        Log.d(AUTH, "SIGN UP");
        if (!validateForm()) {
            return;
        }

        // Sign up
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // if successful
                        if (task.isSuccessful()) {
                            Log.d(AUTH, "signUp success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user, true);
                        } else {
                            // if sign in fails
                            Log.d(AUTH, "signUp failed");
                            Toast.makeText(getApplicationContext(), "Sign up failed!", Toast.LENGTH_SHORT);
                            updateUI(null, true);
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
        } else {
            emailText.setError(null);
        }

        return true;

    }

    private void signOut() {
        mAuth.signOut();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.signupButton) {
            signUp(
                    emailText.getText().toString(),
                    passwordText.getText().toString()
            );
        } else if (id == R.id.signinButton) {
            signIn(
                    emailText.getText().toString(),
                    passwordText.getText().toString()
            );
        } else if (id == R.id.signoutButton) {
            signOut();
        }
    }
}