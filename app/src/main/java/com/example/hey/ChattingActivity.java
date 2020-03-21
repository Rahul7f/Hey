package com.example.hey;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChattingActivity extends AppCompatActivity {

    String visitid,visitname;
    TextView vname,vlastseen;
    String vdp,senderid;
    CircleImageView vprofile;
    ImageButton sendmsg;
    EditText entermsg;
    FirebaseAuth mAuth;
    private DatabaseReference rootref;

    private  final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdepter messageAdepter;
    private RecyclerView msglist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        visitid = getIntent().getExtras().get("visitid").toString();
        visitname = getIntent().getExtras().get("visitname").toString();
        vdp = getIntent().getExtras().get("visitimage").toString();

        mAuth = FirebaseAuth.getInstance();
        senderid = mAuth.getCurrentUser().getUid();

        rootref = FirebaseDatabase.getInstance().getReference();






        sendmsg = findViewById(R.id.chatsendbtn);
        entermsg = findViewById(R.id.chatsendmsg);
        vname = findViewById(R.id.userchatname);
        messageAdepter = new MessageAdepter(messagesList);
        msglist = findViewById(R.id.msg_list);
        linearLayoutManager = new LinearLayoutManager(this);
        msglist.setLayoutManager(linearLayoutManager);
        msglist.setAdapter(messageAdepter);


        vname.setText(visitname);
        vprofile = findViewById(R.id.userchatdp);
        Picasso.get().load(vdp).placeholder(R.drawable.defaultprofile).into(vprofile);

        sendmsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendmessage();

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        rootref.child("Messages").child(senderid).child(visitid)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Messages messages = dataSnapshot.getValue(Messages.class);
                        messagesList.add(messages);
                        messageAdepter.notifyDataSetChanged();

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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

    private void sendmessage() {

        String messagetext = entermsg.getText().toString();
        if (messagetext.isEmpty()){
            Toast.makeText(this, "enter message ", Toast.LENGTH_SHORT).show();
        }
        else {
            String messagesenderRef = "Messages/"+senderid+"/"+visitid;
            String messagereceiverRef = "Messages/"+visitid+"/"+senderid;

            DatabaseReference userMessageKeyRef =  rootref.child("Messages")
                    .child(senderid).child(visitid).push();

            String messagePushID = userMessageKeyRef.getKey();

            Map messageTextBody = new HashMap();
            messageTextBody.put("message",messagetext);
            messageTextBody.put("type","text");
            messageTextBody.put("from",senderid);

            Map messageBodyDetail = new HashMap();
            messageBodyDetail.put(messagesenderRef+"/"+messagePushID,messageTextBody);
            messageBodyDetail.put(messagereceiverRef+"/"+messagePushID,messageTextBody);

            rootref.updateChildren(messageBodyDetail).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        Toast.makeText(ChattingActivity.this, "message sent", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        String message = task.getException().toString();
                        Toast.makeText(ChattingActivity.this, "error"+message, Toast.LENGTH_SHORT).show();
                    }
                    entermsg.setText("");

                }
            });


        }
    }
}
