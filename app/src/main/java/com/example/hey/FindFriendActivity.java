package com.example.hey;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendActivity extends AppCompatActivity {
    Toolbar mtoolbar;
    RecyclerView findFriendRecycleView;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        usersRef = FirebaseDatabase.getInstance().getReference().child("users");

        setContentView(R.layout.activity_find_friend);
        mtoolbar = findViewById(R.id.find_friend_toolbar);
        findFriendRecycleView = findViewById(R.id.find_friend_recycle_view);
        findFriendRecycleView.setLayoutManager(new LinearLayoutManager(this));

        setSupportActionBar(mtoolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Friend");
    }



    @Override
    protected void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<contact>options = new FirebaseRecyclerOptions.Builder<contact>()
                .setQuery(usersRef,contact.class)
                .build();



        FirebaseRecyclerAdapter<contact,FindFriendViewHolder> adapter
                = new FirebaseRecyclerAdapter<contact, FindFriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, final int position, @NonNull contact model) {

                holder.username.setText(model.getName());
                        holder.userstatus.setText(model.getStatus());
                        Picasso.get().load(model.getImage()).placeholder(R.drawable.defaultprofile).into(holder.userimage);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String user_id = getRef(position).getKey();
                                Intent intent = new Intent(FindFriendActivity.this,ProfileActivity.class);
                                intent.putExtra("user_id",user_id);
                                startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_display_layout,viewGroup,false);
                FindFriendViewHolder viewHolder = new FindFriendViewHolder(view);
                return viewHolder;
            }
        };

        findFriendRecycleView.setAdapter(adapter);
        adapter.startListening();
    }




    public  static class FindFriendViewHolder extends RecyclerView.ViewHolder
    {
        TextView username,userstatus;
        CircleImageView userimage;

        public FindFriendViewHolder(@NonNull View itemView)
        {
            super(itemView);
            username = itemView.findViewById(R.id.user_profile_name);
            userstatus = itemView.findViewById(R.id.user_status);
            userimage = itemView.findViewById(R.id.user_profile_image);


        }
    }
}
