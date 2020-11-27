package com.cuijeb.firebasechatroomapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class AddChatActivity extends AppCompatActivity {

    // Users information
    private User user;
    private HashMap<String, String> idToName = new HashMap<>();

    // Views
    private EditText groupNameEditText;
    private LinearLayout groupMembersLayout;
    private Button makeGroupButton;

    // Firebase objects
    private FirebaseAuth auth;
    private FirebaseDatabase database;

    // Firebase database ref
    private DatabaseReference usersDatabase;

    // Used chat names
    private HashSet<String> usedChatNames = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chat);

        // Get user from intent
        Intent intent = getIntent();
        String userJson = intent.getStringExtra("user");
        Gson gson = new Gson();
        user = gson.fromJson(userJson, User.class);

        // Views
        groupNameEditText = findViewById(R.id.editTextTextPersonName3);
        groupMembersLayout = findViewById(R.id.membersLayout);
        makeGroupButton = findViewById(R.id.makeGroupButton);
        makeGroupButton.setEnabled(false);
        makeGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeGroup();
                finish();
            }
        });

        // Firebase objects
        // Firebase
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        // Get all other users
        usersDatabase = database.getReference("users");
        usersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // users
                for (DataSnapshot child: snapshot.getChildren()) {
                    String userId = (String)child.child("userId").getValue();
                    String userName = (String)child.child("userName").getValue();
                    idToName.put(userId, userName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // quick reminder: make sure to get chat names so that you make sure they put a unique one.
        DatabaseReference chatNamesDatabase = database.getReference("chats/chats");
        chatNamesDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // chat names
                for(DataSnapshot child: snapshot.getChildren()) {
                    usedChatNames.add(child.getKey());
                }
                addMemberChoices();
                makeGroupButton.setEnabled(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void addMemberChoices() {
        // Clear everything before working with it
        groupMembersLayout.removeAllViews();

        for (String id: idToName.keySet()) {
             if (!user.userId.equals(id)) {
                 CheckBox checkBox = new CheckBox(this);
                 checkBox.setId(View.generateViewId());
                 checkBox.setTag(id);
                 checkBox.setText(idToName.get(id));
                 checkBox.setTextSize(25);
                 LinearLayout.LayoutParams llpCheckBox = new LinearLayout.LayoutParams(
                         LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
                 );
                 checkBox.setLayoutParams(llpCheckBox);

                 groupMembersLayout.addView(checkBox);
             }
        }
    }

    private void makeGroup() {
        String groupName = groupNameEditText.getText().toString();
        if (TextUtils.isEmpty(groupName)) {
            groupNameEditText.setError("Please Type A Group Name");
            return;
        } else {
            groupNameEditText.setError(null);
        }

        String groupId = groupName.replace(" ", "").toLowerCase();

        if (usedChatNames.contains(groupId)) {
            groupNameEditText.setError("Group Name already in use");
            return;
        } else {
            groupNameEditText.setError(null);
        }

        ArrayList<String> members = new ArrayList<>();
        for (int pos = 0; pos < groupMembersLayout.getChildCount(); pos++) {
            CheckBox checkBox = (CheckBox)groupMembersLayout.getChildAt(pos);
            if (checkBox.isChecked()) {
                members.add((String)checkBox.getTag());
            }
        }
        members.add(user.userId);

        // database root
        DatabaseReference databaseReference = database.getReference();

        // Add chat to chats/chats
        databaseReference.child("chats/chats/" + groupId).setValue(true);

        // Add chat to chats
        HashMap<String, Object> chatInfo = new HashMap<>();
        chatInfo.put("title", groupName);
        chatInfo.put("message", "");
        chatInfo.put("timeStamp", System.currentTimeMillis() / 1000L);
        databaseReference.child("chats/"+groupId).setValue(chatInfo);

        // add chat to members
        HashMap<String, Boolean> chatMembers = new HashMap<>();
        for (String member: members) {
            chatMembers.put(member, true);
        }
        databaseReference.child("members/"+groupId).setValue(chatMembers);

        // add chatId to each user
        for (String member: members) {
            databaseReference.child("users/" + member + "/chats/" + groupId).setValue(true);
        }
    }
}