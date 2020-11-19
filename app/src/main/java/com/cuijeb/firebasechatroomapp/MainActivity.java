package com.cuijeb.firebasechatroomapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.google.firebase.auth.GetTokenResult;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;

    // different views
    private EditText emailText;
    private EditText passwordText;
    private Button signinButton;
    private Button signupButton;

    //Tags
    private final String AUTH = "Authentication";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        // initialize the different views
        emailText = findViewById(R.id.editTextTextEmailAddress);
        passwordText = findViewById(R.id.editTextTextPassword);
        signinButton = findViewById(R.id.signinButton);
        signupButton = findViewById(R.id.signupButton);

        // Add on click listeners
        signinButton.setOnClickListener(this);
        signupButton.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // if signed in open Logged in activity
        // for now just open chat
        if (currentUser != null) {
            // Firebase Auth ID token
            /*
            currentUser.getIdToken(true)
                    .addOnCompleteListener(this, new OnCompleteListener<GetTokenResult>() {
                        @Override
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(
                                        getApplicationContext(),
                                        "Sign in successful!",
                                        Toast.LENGTH_SHORT
                                ).show();
                                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("userEmail", currentUser.getEmail());
                                String idToken = task.getResult().getToken();
                                bundle.putString("userId", idToken);
                                intent.putExtra("bundle", bundle);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(
                                        getApplicationContext(),
                                        "Please log in!",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                    });
            */
            Toast.makeText(
                    getApplicationContext(),
                    "Sign in successful!",
                    Toast.LENGTH_SHORT
            ).show();
            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("userEmail", currentUser.getEmail());
            String uId = currentUser.getUid();
            bundle.putString("userId", uId);
            intent.putExtra("bundle", bundle);
            startActivity(intent);
            finish();
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
                            // Show Toast
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Sign in successful!",
                                    Toast.LENGTH_SHORT
                            ).show();
                            // enter chat activity
                            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("userEmail", user.getEmail());
                            String uId = user.getUid();
                            bundle.putString("userId", uId);
                            intent.putExtra("bundle", bundle);
                            startActivity(intent);
                            finish();
                        } else {
                            // if sign in fails
                            Log.d(AUTH, "signIn failed");
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Sign in failed!",
                                    Toast.LENGTH_SHORT
                            ).show();
                            // show toast
                        }
                    }
                });
    }

    // sign up method
    private void signUp() {
        Log.d(AUTH, "SIGN UP");
        // Sign up
        // Open sign up activity
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.signupButton) {
            signUp();
        } else if (id == R.id.signinButton) {
            signIn(
                    emailText.getText().toString(),
                    passwordText.getText().toString()
            );
        }
    }
}