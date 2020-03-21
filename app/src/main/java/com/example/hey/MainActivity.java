package com.example.hey;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
//import android.widget.Toolbar;


public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ViewPager myviewPager;
    private TabLayout mytabLayout;
    TabAcessorAdapter mytabAcessorAdapter;
    FirebaseUser currentuser;
    DatabaseReference rootref;
    FirebaseAuth mAuth;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        mAuth = FirebaseAuth.getInstance();
       currentuser = mAuth.getCurrentUser();
       rootref = FirebaseDatabase.getInstance().getReference();

        toolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("hey! chats");

        myviewPager = findViewById(R.id.main_tabs_pager);
        mytabAcessorAdapter = new TabAcessorAdapter(getSupportFragmentManager());
        myviewPager.setAdapter(mytabAcessorAdapter);

        mytabLayout = findViewById(R.id.main_Tabs);
        mytabLayout.setupWithViewPager(myviewPager);



    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentuser == null){
            sendusertologin();
        }
        else {

            veryfyusexixtance();


        }
    }

    private void veryfyusexixtance() {

        String userid = mAuth.getCurrentUser().getUid();
        rootref.child("users").child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if ((dataSnapshot.child("name").exists())){
//                    Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                }
                else {
                    sendusertoSetting();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendusertologin() {
        Intent intent = new Intent(getApplicationContext(),Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }

    private void sendusertoSetting() {
        Intent settingintent = new Intent(getApplicationContext(),SettingActivity.class);
//        settingintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(settingintent);
//        finish();

    }


        private void sendusertoFindFriend() {
        Intent findfriendintent = new Intent(getApplicationContext(),FindFriendActivity.class);

        startActivity(findfriendintent);
//        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu,menu);
        return super.onCreateOptionsMenu(menu);


    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId()==R.id.main_logout_option){

            mAuth.signOut();
            sendusertologin();

        }

        if (item.getItemId()==R.id.main_setting_option){

            sendusertoSetting();

        }

        if (item.getItemId()==R.id.main_find_friends_option){
            sendusertoFindFriend();


        }

        if (item.getItemId() == R.id.main_create_group_option){
            RequestNewGroup();
        }

        return super.onOptionsItemSelected(item);





    }

    private void RequestNewGroup() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle("Enter Group Name..");

        final EditText groupnamefild = new EditText(MainActivity.this);
        groupnamefild.setHint("e.g Friends Forever");
        builder.setView(groupnamefild);

        builder.setPositiveButton("Creare", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupname = groupnamefild.getText().toString();

                if (TextUtils.isEmpty(groupname))
                {
                    Toast.makeText(MainActivity.this, "Enter group name ", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    CreateNewGroup(groupname);

                }

            }
        });

        builder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void CreateNewGroup(String groupname) {

        rootref.child("groups").child(groupname).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "group crrated", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            String message = task.getException().toString();
                            Toast.makeText(MainActivity.this, "error"+message, Toast.LENGTH_SHORT).show();
                        }

                    }
                });


    }
}
