package com.example.hey;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class ContactsFragment extends Fragment {
    private View contactview;
    private RecyclerView contactlist;
    private DatabaseReference contactref,userref;
    private FirebaseAuth mAuth;
    private String currentUserID;

    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        contactview = inflater.inflate(R.layout.fragment_contacts, container, false);

        contactlist = contactview.findViewById(R.id.contactlist);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        contactref = FirebaseDatabase.getInstance().getReference().child("contacts").child(currentUserID);
        userref = FirebaseDatabase.getInstance().getReference().child("users");

        contactlist.setLayoutManager(new LinearLayoutManager(getContext()));

        return contactview;

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<contact>()
                .setQuery(contactref,contact.class)
                .build();


        FirebaseRecyclerAdapter<contact,ContactViewHolder> adapter =
                new FirebaseRecyclerAdapter<contact, ContactViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ContactViewHolder holder, int position, @NonNull contact model)
                    {
                        String userID = getRef(position).getKey();
                        userref.child(userID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild("image")){

                                    String profile = dataSnapshot.child("image").getValue().toString();
                                    String userstatus = dataSnapshot.child("status").getValue().toString();
                                    String username = dataSnapshot.child("name").getValue().toString();

                                    holder.username.setText(username);
                                    holder.userstatus.setText(userstatus);
                                    Picasso.get().load(profile).placeholder(R.drawable.defaultprofile).into(holder.userimage);

                                }
                                else {

                                    String userstatus = dataSnapshot.child("status").getValue().toString();
                                    String username = dataSnapshot.child("name").getValue().toString();

                                    holder.username.setText(username);
                                    holder.userstatus.setText(userstatus);


                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType)
                    {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_display_layout,viewGroup,false);
                        ContactViewHolder viewHolder = new ContactViewHolder(view);
                        return viewHolder;

                    }
                };

        contactlist.setAdapter(adapter);
        adapter.startListening();
    }



    public  static class ContactViewHolder extends RecyclerView.ViewHolder
    {
        TextView username,userstatus;
        CircleImageView userimage;

        public ContactViewHolder(@NonNull View itemView)
        {
            super(itemView);
            username = itemView.findViewById(R.id.user_profile_name);
            userstatus = itemView.findViewById(R.id.user_status);
            userimage = itemView.findViewById(R.id.user_profile_image);


        }
    }
}
