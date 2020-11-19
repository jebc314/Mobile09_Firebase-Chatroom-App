package com.cuijeb.firebasechatroomapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener{

    // Firebase
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference userRef;

    // User info
    private Intent intent;
    private Bundle bundle;
    private String email;
    private String userId;
    private User user;

    // Views
    private TextView titleTextView;
    private Button signoutButton;

    // Chat Tag
    private final String TAG = "CHATROOMS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Firebase
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        // get intent and info
        intent = getIntent();
        bundle = intent.getBundleExtra("bundle");
        email = bundle.getString("userEmail");
        userId = bundle.getString("userId");

        // get views
        titleTextView = findViewById(R.id.titleTextView);
        signoutButton = findViewById(R.id.signoutButton);

        // intial setup

        // get the database
        userRef = database.getReference("users/" + userId);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Get user from database
                user = snapshot.getValue(User.class);
                Log.d(TAG, "READ USER");
                // Set chat name to user
                titleTextView.setText(user.userName + "\'s Chat");
                signoutButton.setEnabled(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "loadPost:onCancelled", error.toException());
            }
        });

        signoutButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.signoutButton) {
            auth.signOut();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}