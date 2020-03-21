package com.example.hey;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private String receveruserid,current_state,senderUserID;
    CircleImageView dp;
    TextView userneme,userstatus;
    Button sendmsg ,cancle_request;
    private DatabaseReference userref,chatrequestref,contactref;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        receveruserid = getIntent().getStringExtra("user_id");
        userref = FirebaseDatabase.getInstance().getReference().child("users");
        chatrequestref = FirebaseDatabase.getInstance().getReference().child("chat request");
        contactref = FirebaseDatabase.getInstance().getReference().child("contacts");
        senderUserID = mAuth.getCurrentUser().getUid();
        cancle_request = findViewById(R.id.cancle_request);


        dp = findViewById(R.id.dp);
        userneme = findViewById(R.id.username);
        userstatus = findViewById(R.id.ustatus);
        sendmsg = findViewById(R.id.usendmessage);
        current_state = "new";


        Retriveuserinfo();




    }

    private void Retriveuserinfo() {
        userref.child(receveruserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if((dataSnapshot.exists())  && (dataSnapshot.hasChild("image"))){

                    String usrname = dataSnapshot.child("name").getValue().toString();
                    String userimage = dataSnapshot.child("image").getValue().toString();
                    String usrstatus = dataSnapshot.child("status").getValue().toString();

                    Picasso.get().load(userimage).placeholder(R.drawable.defaultprofile).into(dp);
                    userneme.setText(usrname);
                    userstatus.setText(usrstatus);
                    ManageChatRequest();

                }
                else {
                    String usrname = dataSnapshot.child("name").getValue().toString();
                    String usrstatus = dataSnapshot.child("status").getValue().toString();

                    userneme.setText(usrname);
                    userstatus.setText(usrstatus);

                    ManageChatRequest();


                }





            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void ManageChatRequest() {

        chatrequestref.child(senderUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {

                        if (dataSnapshot.hasChild(receveruserid))
                        {
                            String request_type = dataSnapshot.child(receveruserid).child("request_type").getValue().toString();

                            if (request_type.equals("sent"))
                            {
                                current_state = "request_sent";
                                sendmsg.setText("cancel chat request");
                            }
                            else if (request_type.equals("received"))
                            {
                                current_state = "request_received";
                                sendmsg.setText("Accept Chat Request");
                                cancle_request.setVisibility(View.VISIBLE);
                                cancle_request.setEnabled(true);
                                cancle_request.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        canclesendrequest();
                                    }
                                });


                            }
                        }
                        else {
                            contactref.child(senderUserID)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChild(receveruserid)){
                                                current_state = "friends";
                                                sendmsg.setText("Remove this contact");

                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }

                    }



                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        if (!senderUserID.equals(receveruserid)){


            sendmsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    sendmsg.setEnabled(false);
                    if (current_state.equals("new")){
                        sendchatrequest();

                    }
                    if (current_state.equals("request_sent"))
                    {
                        canclesendrequest();

                    }
                    if (current_state.equals("request_received"))
                    {
                        acceptchatrequest();

                    }
                    if (current_state.equals("friends"))
                    {
                        removecontact();

                    }

                }
            });

        }
        else {
            sendmsg.setVisibility(View.INVISIBLE);

        }


    }

    private void removecontact() {

        contactref.child(senderUserID).child(receveruserid)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            contactref.child(receveruserid).child(senderUserID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                            {
                                                sendmsg.setEnabled(true);
                                                current_state = "new";
                                                sendmsg.setText("send message");
                                                cancle_request.setVisibility(View.INVISIBLE);
                                                cancle_request.setEnabled(false);

                                            }


                                        }
                                    });

                        }


                    }
                });


    }

    private void acceptchatrequest() {

        contactref.child(senderUserID).child(receveruserid)
                .child("contacts").setValue("saved")
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            contactref.child(receveruserid).child(senderUserID)
                                    .child("contacts").setValue("saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>()
                                    {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful()){
                                                chatrequestref.child(senderUserID).child(receveruserid)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    chatrequestref.child(receveruserid).child(senderUserID)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    sendmsg.setEnabled(true);
                                                                                    current_state = "friends";
                                                                                    sendmsg.setText("Remove this contact");

                                                                                    cancle_request.setVisibility(View.INVISIBLE);
                                                                                    cancle_request.setEnabled(false);

                                                                                }
                                                                            });
                                                                }

                                                            }
                                                        });
                                            }

                                        }
                                    });

                        }

                    }
                });


    }

    private void canclesendrequest() {
        chatrequestref.child(senderUserID).child(receveruserid)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            chatrequestref.child(receveruserid).child(senderUserID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                            {
                                                sendmsg.setEnabled(true);
                                                current_state = "new";
                                                sendmsg.setText("send message");
                                                cancle_request.setVisibility(View.INVISIBLE);
                                                cancle_request.setEnabled(false);

                                            }


                                        }
                                    });

                        }


                    }
                });
    }


    private void sendchatrequest()
    {
        chatrequestref.child(senderUserID).child(receveruserid)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            chatrequestref.child(receveruserid).child(senderUserID)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                            {
                                                sendmsg.setEnabled(true);
                                                current_state ="request_sent";
                                                sendmsg.setText("cancel chat  request");

                                            }

                                        }
                                    });
                        }

                    }
                });


    }
}
