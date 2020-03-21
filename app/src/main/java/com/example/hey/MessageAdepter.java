package com.example.hey;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MessageAdepter extends RecyclerView.Adapter<MessageAdepter.MessageViewHolder> {

    private List<Messages> usermessagelist;
    private FirebaseAuth mAuth;
    private DatabaseReference userref;

    public MessageAdepter (List<Messages> usermessagelist){
        this.usermessagelist = usermessagelist;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView senderMessageText,receiverMessageText;
        public MessageViewHolder(@NonNull View itemView)
        {
            super(itemView);
            senderMessageText = itemView.findViewById(R.id.sender_message_text);
            receiverMessageText = itemView.findViewById(R.id.receiver_message_text);

        }



    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.costom_message_layout,parent,false);

        mAuth = FirebaseAuth.getInstance();
        return new MessageViewHolder(view);
    }





    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {

        String messageSenderID  = mAuth.getCurrentUser().getUid();
        Messages messages = usermessagelist.get(position);

        String fromUserID = messages.getFrom();
        String fromMessageType = messages.getType();
        userref = FirebaseDatabase.getInstance().getReference().child("users").child(fromUserID);
        userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (fromMessageType.equals("text"))
        {
            holder.receiverMessageText.setVisibility(View.INVISIBLE);

            if (fromUserID.equals(messageSenderID)){
                holder.senderMessageText.setBackgroundResource(R.drawable.sender_message_layout);
               holder.senderMessageText.setText(messages.getMessage());
            }
            else {

                holder.senderMessageText.setVisibility(View.INVISIBLE);
                holder.receiverMessageText.setVisibility(View.VISIBLE);


                holder.receiverMessageText.setBackgroundResource(R.drawable.receiver_message_layout);
                holder.receiverMessageText.setText(messages.getMessage());


            }
        }


    }




    @Override
    public int getItemCount() {
       return usermessagelist.size();
    }



}
