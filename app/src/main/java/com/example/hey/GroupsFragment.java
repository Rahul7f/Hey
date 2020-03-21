package com.example.hey;


import android.content.Intent;
import android.icu.text.Edits;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment {

    private  View groupview;

    DatabaseReference groupref;

    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> listofgroup = new ArrayList<>();


    public GroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        groupview = inflater.inflate(R.layout.fragment_groups, container, false);

        Cast();

        RetriveAndDiaplayGroup();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String currentGroupName = (String) parent.getItemAtPosition(position);

                Intent groupChatintent = new Intent(getContext(),GroupChatActivity.class);
                groupChatintent.putExtra("GroupName",currentGroupName);
                startActivity(groupChatintent);
            }
        });

        return groupview;
    }



    private void Cast() {
        listView =  groupview.findViewById(R.id.list_View);
        arrayAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,listofgroup);
        listView.setAdapter(arrayAdapter);
        groupref = FirebaseDatabase.getInstance().getReference().child("groups");
    }


    private void RetriveAndDiaplayGroup() {

        groupref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Set<String> set = new HashSet<>();
                Iterator iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()){
                    set.add(((DataSnapshot)iterator.next()).getKey());
                }

                listofgroup.clear();
                listofgroup.addAll(set);
                arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

}
