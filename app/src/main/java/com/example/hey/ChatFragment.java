package com.example.hey;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {
   private     View chatview;
   private RecyclerView chatlist;
   DatabaseReference chatref,userref;
   FirebaseAuth mAuth;
   String currnetuserID;
   private String profile;



    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        chatview = inflater.inflate(R.layout.fragment_chat, container, false);

        chatlist = chatview.findViewById(R.id.chats_list);
        chatlist.setLayoutManager(new LinearLayoutManager(getContext()));
        mAuth = FirebaseAuth.getInstance();
        currnetuserID = mAuth.getCurrentUser().getUid();


        userref = FirebaseDatabase.getInstance().getReference().child("users");
        userref.keepSynced(true);
        chatref = FirebaseDatabase.getInstance().getReference().child("contacts").child(currnetuserID);
        userref.keepSynced(true);
        return chatview;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<contact> options =
                new FirebaseRecyclerOptions.Builder<contact>()
                .setQuery(chatref,contact.class)
                .build();

        FirebaseRecyclerAdapter<contact,ChatViewHolder>adapter =
                new FirebaseRecyclerAdapter<contact, ChatViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ChatViewHolder holder, int position, @NonNull contact model)
                    {
                        final String userID = getRef(position).getKey();
                        userref.child(userID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists())
                                {


                                    if (dataSnapshot.hasChild("image")){

                                         profile = dataSnapshot.child("image").getValue().toString();

                                        Picasso.get().load(profile).placeholder(R.drawable.defaultprofile).into(holder.userimage);

                                    }



                                    final String username = dataSnapshot.child("name").getValue().toString();

                                    holder.username.setText(username);
                                    holder.userstatus.setText("Last seen: "+"\n"+"Date: "+"Time: ");

                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(getContext(),ChattingActivity.class);
                                            intent.putExtra("visitid",userID);
                                            intent.putExtra("visitname",username);
                                            intent.putExtra("visitimage",profile);
                                            startActivity(intent);
                                        }
                                    });

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_display_layout,viewGroup,false);
                        ChatViewHolder chatViewHolder = new ChatViewHolder(view);
                        return chatViewHolder;
                    }
                };

        chatlist.setAdapter(adapter);
        adapter.startListening();
    }

    public  static class ChatViewHolder extends RecyclerView.ViewHolder
    {
        TextView username,userstatus;
        CircleImageView userimage;

        public ChatViewHolder(@NonNull View itemView)
        {
            super(itemView);
            username = itemView.findViewById(R.id.user_profile_name);
            userstatus = itemView.findViewById(R.id.user_status);
            userimage = itemView.findViewById(R.id.user_profile_image);



        }
    }
}
