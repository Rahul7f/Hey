package com.example.hey;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.icu.text.Edits;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageButton sendmessagebtn;
    private TextView displaymsg;
    private EditText inputMsg;
    private ScrollView scrollView;
    DatabaseReference userref,GroupNameRef,GroupMessageKeyRef;
    FirebaseAuth mAuth;
    private  String currentgroupname,currentuserid,currentusername,currentDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        userref = FirebaseDatabase.getInstance().getReference().child("users");

        mAuth = FirebaseAuth.getInstance();
        currentuserid = mAuth.getCurrentUser().getUid();

        currentgroupname = getIntent().getStringExtra("GroupName");
        Toast.makeText(GroupChatActivity.this, currentgroupname, Toast.LENGTH_SHORT).show();
        GroupNameRef = FirebaseDatabase.getInstance().getReference().child("groups").child(currentgroupname);
        cast();
        getuserInfo();
        sendmessagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SavaMessageInfoToDataBase();
                inputMsg.setText("");
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);

            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        GroupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if (dataSnapshot.exists()){
                    Displaymessage(dataSnapshot);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


                if (dataSnapshot.exists()){
                    Displaymessage(dataSnapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void cast() {

        toolbar = findViewById(R.id.group_chat_bar_layout);
        toolbar.setTitle(currentgroupname);

        sendmessagebtn = findViewById(R.id.sendmessage);
        inputMsg = findViewById(R.id.input_text_msg);
        displaymsg = findViewById(R.id.group_chat_text_display);
        scrollView = findViewById(R.id.scrollviewchat);
    }

    private void getuserInfo() {

        userref.child(currentuserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    currentusername = dataSnapshot.child("name").getValue().toString();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }


    private void SavaMessageInfoToDataBase() {

        String message = inputMsg.getText().toString();
        String messagekey = GroupNameRef.push().getKey();

        if (TextUtils.isEmpty(message))
        {
            Toast.makeText(this, "enter message", Toast.LENGTH_SHORT).show();

        }
        else {

            Calendar calfordate  = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd,YYYY");
            currentDate = currentDateFormat.format(calfordate.getTime());


            Calendar calforTime  = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            String currentTime = currentTimeFormat.format(calforTime.getTime());

            HashMap<String,Object> groupMessageKey = new HashMap<>();
            GroupNameRef.updateChildren(groupMessageKey);

            GroupMessageKeyRef = GroupNameRef.child(messagekey);

            HashMap<String,Object> messageInfoMap = new HashMap<>();
            messageInfoMap.put("name",currentusername);
            messageInfoMap.put("message",message);
            messageInfoMap.put("date",currentDate);
            messageInfoMap.put("time",currentTime);

            GroupMessageKeyRef.updateChildren(messageInfoMap);

        }
    }

    private void Displaymessage(DataSnapshot dataSnapshot) {

        Iterator iterator = dataSnapshot.getChildren().iterator();

        String chatDate = (String)  ((DataSnapshot)iterator.next()).getValue();
        String chatMessage = (String)  ((DataSnapshot)iterator.next()).getValue();
        String chatName = (String)  ((DataSnapshot)iterator.next()).getValue();
        String chatTime = (String)  ((DataSnapshot)iterator.next()).getValue();

        displaymsg.append(chatName + ":\n"+chatMessage + ":\n" + chatTime+"    "+chatDate+"\n\n\n");
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);

    }
}
