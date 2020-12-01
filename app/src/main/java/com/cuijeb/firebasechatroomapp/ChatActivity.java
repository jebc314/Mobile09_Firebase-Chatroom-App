package com.cuijeb.firebasechatroomapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

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
    // Also ayouts
    private LinearLayout chatsLayout;

    // Array list of chats
    private ArrayList<Chat> chats = new ArrayList<Chat>();

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
        chatsLayout = findViewById(R.id.chats_layout);

        // intial setup

        // get the database
        userRef = database.getReference("users/" + userId);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Get user from database
                // if first time
                if (user == null) {
                    user = snapshot.getValue(User.class);
                    updateChats(user);
                } else {
                    User updated = snapshot.getValue(User.class);
                    updateChats(updated);
                    user = updated;
                }
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

    public void updateChats(User user) {
        // if chats is empty then just clear the array
        // Also display to add the add chat button
        if (user.chats == null) {
            chats.clear();
            displayChats();
        // Otherwise
        } else {
            // Go through and remove the ones in ArrayList
            // that aren't in the user's chat HashMap
            Iterator<Chat> it = chats.iterator();
            while (it.hasNext()){
                Chat next = it.next();
                if (!user.chats.containsKey(next)) {
                    it.remove();
                }
            }

            // Add the ones in the user chat to Arraylist
            // and also update the chat objects by replacing it
            for (String chatId: user.chats.keySet()) {
                DatabaseReference databaseRef = database.getReference("chats/" + chatId);
                databaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Chat obj = snapshot.getValue(Chat.class);
                        obj.chatId = chatId;
                        if (chats.contains(obj)) {
                            chats.remove(obj);
                        }
                        chats.add(obj);
                        Collections.sort(chats,  Collections.reverseOrder());
                        displayChats();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }

        // displayChats();
    }

    public void displayChats() {
        chatsLayout.removeAllViews();
        for (Chat chat: chats) {
            // Box
            ConstraintLayout constraintLayout = new ConstraintLayout(this);
            constraintLayout.setTag(chat);
            constraintLayout.setId(View.generateViewId());
            LinearLayout.LayoutParams cLParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            constraintLayout.setLayoutParams(cLParams);

            // Title
            TextView chatTitle = new TextView(this);
            chatTitle.setId(View.generateViewId());
            chatTitle.setText(chat.title);
            chatTitle.setTextSize(25);

            ConstraintLayout.LayoutParams clpChatTitle = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
            chatTitle.setLayoutParams(clpChatTitle);

            constraintLayout.addView(chatTitle);

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);

            constraintSet.connect(chatTitle.getId(), ConstraintSet.LEFT, constraintLayout.getId(), ConstraintSet.LEFT, 8);
            constraintSet.connect(chatTitle.getId(), ConstraintSet.TOP, constraintLayout.getId(), ConstraintSet.TOP, 8);

            constraintSet.applyTo(constraintLayout);

            // Message
            TextView chatMessage = new TextView(this);
            chatMessage.setId(View.generateViewId());
            chatMessage.setText(chat.message.substring(0, Math.min(chat.message.length(), 40)));
            chatMessage.setTextSize(15);

            ConstraintLayout.LayoutParams clpChatMessage = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
            chatTitle.setLayoutParams(clpChatMessage);

            constraintLayout.addView(chatMessage);

            constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);

            constraintSet.connect(chatMessage.getId(), ConstraintSet.LEFT, constraintLayout.getId(), ConstraintSet.LEFT, 8);
            constraintSet.connect(chatMessage.getId(), ConstraintSet.BOTTOM, constraintLayout.getId(), ConstraintSet.BOTTOM, 8);
            constraintSet.connect(chatMessage.getId(), ConstraintSet.TOP, chatTitle.getId(), ConstraintSet.BOTTOM, 8);

            constraintSet.applyTo(constraintLayout);

            // Time
            TextView chatText = new TextView(this);
            chatText.setId(View.generateViewId());
            Date time = new Date((long)chat.timeStamp*1000);
            DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
            String strTime = dateFormat.format(time);
            chatText.setText(strTime);
            chatText.setTextSize(15);

            ConstraintLayout.LayoutParams clpChatText = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
            chatTitle.setLayoutParams(clpChatText);

            constraintLayout.addView(chatText);

            constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);

            constraintSet.connect(chatText.getId(), ConstraintSet.RIGHT, constraintLayout.getId(), ConstraintSet.RIGHT, 8);
            constraintSet.connect(chatText.getId(), ConstraintSet.BOTTOM, constraintLayout.getId(), ConstraintSet.BOTTOM, 8);

            constraintSet.applyTo(constraintLayout);

            // Add constraintlayout to chatLayout
            chatsLayout.addView(constraintLayout);

            // constraint layout's onclicklistener
            constraintLayout.setOnClickListener(this);
        }
        // Add the addChat button
        Button addChat = new Button(this);
        addChat.setId(View.generateViewId());
        addChat.setText("Add Chat");
        addChat.setTextSize(25);
        LinearLayout.LayoutParams llpAddChat = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        addChat.setLayoutParams(llpAddChat);
        addChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddChatActivity.class);
                Gson gson = new Gson();
                String userJson = gson.toJson(user);
                intent.putExtra("user", userJson);
                startActivity(intent);
            }
        });

        chatsLayout.addView(addChat);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.signoutButton) {
            auth.signOut();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else if(v instanceof ConstraintLayout) {
            Gson gson = new Gson();
            Intent intent = new Intent(this, UserChatActivity.class);
            Chat chat = (Chat)v.getTag();
            String userJson = gson.toJson(user);
            String chatJson = gson.toJson(chat);
            intent.putExtra("user", userJson);
            intent.putExtra("chat", chatJson);
            startActivity(intent);
        }
    }
}