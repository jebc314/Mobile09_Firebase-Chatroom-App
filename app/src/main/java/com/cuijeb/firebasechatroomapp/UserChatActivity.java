package com.cuijeb.firebasechatroomapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class UserChatActivity extends AppCompatActivity implements View.OnClickListener{
    // Info from database
    private User user;
    private Chat chat;

    // Views
    private TextView chatname;
    private ScrollView scrollView;
    private LinearLayout messagesLayout;
    private EditText messageBox;
    private Button sendMessage;

    // More databases
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference messagesRef;
    private DatabaseReference chatsRef;

    // Messages object
    private Messages messages;

    // id to name
    //private HashMap<String, String> idToName = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_chat);

        // Get information from intent
        Intent intent = getIntent();
        Gson gson = new Gson();
        String userJson = intent.getStringExtra("user");
        user = gson.fromJson(userJson, User.class);
        String chatJson = intent.getStringExtra("chat");
        chat = gson.fromJson(chatJson, Chat.class);

        // get views
        chatname = findViewById(R.id.chatnameTextview);
        scrollView = findViewById(R.id.scrollView);
        messagesLayout = findViewById(R.id.messagesLayout);
        messageBox = findViewById(R.id.editTextTextPersonName);
        sendMessage = findViewById(R.id.sendButton);
        sendMessage.setOnClickListener(this);

        // initial setup
        chatname.setText(chat.title);

        // get the messages recepective this chat
        firebaseDatabase = FirebaseDatabase.getInstance();
        messagesRef = firebaseDatabase.getReference("messages/" + chat.chatId);
        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, Message> hashMap = new HashMap<>();
                Iterable<DataSnapshot> dataSnapshots = snapshot.getChildren();
                for(DataSnapshot ds: dataSnapshots) {
                    Message temp = ds.getValue(Message.class);
                    hashMap.put(ds.getKey(), temp);
                    //idToName.put(temp.userId, "");
                    /*DatabaseReference usersRef = firebaseDatabase.getReference("users");
                    Query query = usersRef.orderByKey().equalTo(temp.userId);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            idToName.put(temp.userId, snapshot.child("userName").getValue(String.class));
                            updateMessages();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });*/
                }
                messages = new Messages(hashMap);

                updateMessages();
                scrollView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                },1000);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // database chats
        chatsRef = firebaseDatabase.getReference("chats/"+chat.chatId);

    }
    public void updateMessages(){
        messagesLayout.removeAllViews();
        //String[] messageIds = messages.messages.keySet().toArray(new String[1]);
        //Arrays.sort(messageIds);
        for (int i = 0; i<messages.messages.keySet().size(); i++) {
            String id = "m" + i;
            Message message = messages.messages.get(id);

            // User and time:
            TextView infoTextView = new TextView(this);
            infoTextView.setId(View.generateViewId());
            Date time = new Date((long)message.timeStamp*1000);
            DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
            String strTime = dateFormat.format(time);
            infoTextView.setText(message.userName + ": " + strTime);
            infoTextView.setTextSize(15);
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            messagesLayout.addView(infoTextView);

            // Actual message
            TextView messageTextView = new TextView(this);
            messageTextView.setId(View.generateViewId());
            messageTextView.setText(message.message);
            messageTextView.setTextSize(25);
            llp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            messagesLayout.addView(messageTextView);

            // Space
            TextView spaceTextView = new TextView(this);
            spaceTextView.setText(" ");
            spaceTextView.setTextSize(25);
            llp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            messagesLayout.addView(spaceTextView);

            // Align right if this user sent the message other wise to the right
            // FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            // String uId = firebaseUser.getUid();

            // Get user Id directly from user object
            String uId = user.userId;
            if (message.userId.equals(uId)){
                infoTextView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                messageTextView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                spaceTextView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            } else {
                infoTextView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                messageTextView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                spaceTextView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sendButton) {
            // put this message in messages in the right chat section
            String message = messageBox.getText().toString();
            messageBox.setText("");
            String tag = "m"+messages.messages.size();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("message", message);
            // // Change this using that users' read is now public
            // FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            // String uId = firebaseUser.getUid();
            String uId = user.userId;
            hashMap.put("userId", uId);
            hashMap.put("userName", user.userName);
            long unixSeconds = System.currentTimeMillis() / 1000L;
            hashMap.put("timeStamp", unixSeconds);
            messagesRef.child(tag).setValue(hashMap);

            // put this as the most recent message in chats section
            chatsRef.child("message").setValue(message);
            chatsRef.child("timeStamp").setValue(unixSeconds);
        }
    }
}