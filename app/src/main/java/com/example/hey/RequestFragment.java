package com.example.hey;


import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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


/**
 * A simple {@link Fragment} subclass.
 */

public class RequestFragment extends Fragment {
    private  View requestview;
    private RecyclerView requestlist;
    private DatabaseReference requestref,userref,contactRef;
    private FirebaseAuth mAuth;
    private String currentuserID;


    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        requestview = inflater.inflate(R.layout.fragment_request, container, false);

        requestlist = requestview.findViewById(R.id.requestlist);
        requestlist.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth = FirebaseAuth.getInstance();
        currentuserID = mAuth.getCurrentUser().getUid();
        requestref = FirebaseDatabase.getInstance().getReference().child("chat request");
        contactRef = FirebaseDatabase.getInstance().getReference().child("contacts");
        userref = FirebaseDatabase.getInstance().getReference().child("users");


        return requestview;
    }

    @Override
    public void onStart() {
        super.onStart();


        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<contact>()
                        .setQuery(requestref.child(currentuserID).orderByChild("request_type").equalTo("received"),contact.class)
                        .build();

        FirebaseRecyclerAdapter<contact,RequestViewHolder> adapter =
                new FirebaseRecyclerAdapter<contact, RequestViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final RequestViewHolder holder, int position, @NonNull contact model)
                    {
//                        holder.itemView.findViewById(R.id.request_accept_btn).setVisibility(View.VISIBLE);
//                        holder.itemView.findViewById(R.id.request_cancle_btn).setVisibility(View.VISIBLE);

                        final  String list_user_id = getRef(position).getKey();

                        DatabaseReference getTypeRef = getRef(position).child("request_type").getRef();

                        getTypeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.exists()){

                                    String type = dataSnapshot.getValue().toString();

                                    if (type.equals("received"))
                                    {
                                        userref.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                if (dataSnapshot.hasChild("image")){

                                                   final String profile = dataSnapshot.child("image").getValue().toString();

                                                    Picasso.get().load(profile).placeholder(R.drawable.defaultprofile).into(holder.userimage);

                                                }


                                                final String username = dataSnapshot.child("name").getValue().toString();

                                                holder.username.setText(username);
                                                holder.userstatus.setText("Wants to connect with you");




                                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v)
                                                    {
                                                        CharSequence option[] = new CharSequence[]
                                                                {
                                                                        "Accept",
                                                                        "Cancel"

                                                                };
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                        builder.setTitle(username+"chat request");
                                                        builder.setItems(option, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which)
                                                            {
                                                                if (which == 0)
                                                                {
                                                                    contactRef.child(currentuserID).child(list_user_id).child("contacts")
                                                                            .setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task)
                                                                        {
                                                                            if (task.isSuccessful())
                                                                            {

                                                                                contactRef.child(list_user_id).child(currentuserID).child("contacts")
                                                                                        .setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task)
                                                                                    {
                                                                                        if (task.isSuccessful())
                                                                                        {
                                                                                            requestref.child(currentuserID).child(list_user_id)
                                                                                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<Void> task)
                                                                                                {
                                                                                                    if (task.isSuccessful()){
                                                                                                        requestref.child(list_user_id).child(currentuserID)
                                                                                                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                                if(task.isSuccessful()){
                                                                                                                    Toast.makeText(getContext(), "New Contact Added", Toast.LENGTH_SHORT).show();
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


                                                                        }
                                                                    });

                                                                }

                                                                if (which == 1)
                                                                {
                                                                    requestref.child(currentuserID).child(list_user_id)
                                                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task)
                                                                        {
                                                                            if (task.isSuccessful()){
                                                                                requestref.child(list_user_id).child(currentuserID)
                                                                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if(task.isSuccessful()){
                                                                                            Toast.makeText(getContext(), "Canceled", Toast.LENGTH_SHORT).show();
                                                                                        }

                                                                                    }
                                                                                });
                                                                            }

                                                                        }
                                                                    });

                                                                }

                                                            }
                                                        });
                                                        builder.show();

                                                    }
                                                });

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });


                                    }



                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                    @NonNull
                    @Override
                    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_display_layout,viewGroup,false);
                        RequestViewHolder requestViewHolder = new RequestViewHolder(view);
                        return requestViewHolder;
                    }
                };
        requestlist.setAdapter(adapter);
        adapter.startListening();




    }


    public  static class RequestViewHolder extends RecyclerView.ViewHolder
    {
        TextView username,userstatus;
        CircleImageView userimage;
        Button accept,cancle;

        public RequestViewHolder(@NonNull View itemView)
        {
            super(itemView);
            username = itemView.findViewById(R.id.user_profile_name);
            userstatus = itemView.findViewById(R.id.user_status);
            userimage = itemView.findViewById(R.id.user_profile_image);
            accept = itemView.findViewById(R.id.request_accept_btn);
            cancle = itemView.findViewById(R.id.request_cancle_btn);



        }
    }


}
