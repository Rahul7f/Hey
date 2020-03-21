package com.example.hey;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageActivity;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    Button updatauserprofile;
    EditText updatestatus,updateuser;
    FirebaseAuth mAuth;
    DatabaseReference rootref;
    ProgressDialog lodingbar;
    CircleImageView profileImage;
    private   static  final int galerypic = 1;
    private StorageReference userprofileimageref;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


        userprofileimageref =  FirebaseStorage.getInstance().getReference().child("user images");

        updatestatus = findViewById(R.id.set_status);
        updateuser = findViewById(R.id.set_user_name);
        updatauserprofile = findViewById(R.id.update_profile);
        profileImage = findViewById(R.id.profile_image);
        lodingbar = new ProgressDialog(this);

        updateuser.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();


        rootref = FirebaseDatabase.getInstance().getReference();

        retriveuserinfo();

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galeryIntent  = new Intent();
                galeryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galeryIntent.setType("image/*");
                startActivityForResult(galeryIntent,galerypic);

            }
        });






        updatauserprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSetting();
            }
        });
    }

    private void retriveuserinfo() {

        String UserCurrrentId = mAuth.getCurrentUser().getUid();

        rootref.child("users").child(UserCurrrentId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))  && (dataSnapshot.hasChild("image"))){

                            String retriveusername = dataSnapshot.child("name").getValue().toString();
                            String retriveuserStatus = dataSnapshot.child("status").getValue().toString();
                            String retriveprofileimage = dataSnapshot.child("image").getValue().toString();
                            updateuser.setText(retriveusername);
                            updatestatus.setText(retriveuserStatus);
                            Picasso.get().load(retriveprofileimage).into(profileImage);



                        }

                        else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))){

                            String retriveusername = dataSnapshot.child("name").getValue().toString();
                            String retriveuserStatus = dataSnapshot.child("status").getValue().toString();
                            updateuser.setText(retriveusername);
                            updatestatus.setText(retriveuserStatus);

                        }
                        else {
                            updateuser.setVisibility(View.VISIBLE);
                            Toast.makeText(SettingActivity.this, "please set and update your profile infromation", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

    private void updateSetting() {

       final String setusername = updateuser.getText().toString();
       String status = updatestatus.getText().toString();
       if(TextUtils.isEmpty(setusername)){
           Toast.makeText(this, "enter user name ", Toast.LENGTH_SHORT).show();

       }

        if(TextUtils.isEmpty(status)){

            Toast.makeText(this, "enter status ", Toast.LENGTH_SHORT).show();

        }
        else {

            String currentuserid = mAuth.getCurrentUser().getUid();

            HashMap<String,Object>  profilemap   = new HashMap<>();
            profilemap.put("UID",currentuserid);
            profilemap.put("name",setusername);
            profilemap.put("status",status);
            rootref.child("users").child(currentuserid).updateChildren(profilemap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                sendusertomain();
                                Toast.makeText(SettingActivity.this, "Profile update sucessfull", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                String meassage = task.getException().toString();
                                Toast.makeText(SettingActivity.this, "Error:- "+meassage, Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }




    }

    private void sendusertomain() {
        Intent mainintent = new Intent(getApplicationContext(),MainActivity.class);
        mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainintent);
        finish();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==galerypic && requestCode==RESULT_FIRST_USER && data!=null){
            Uri imageuri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode==RESULT_OK){

                lodingbar.setTitle("set profile image ");
                lodingbar.setMessage("please wait your profile image is uploding");
                lodingbar.setCanceledOnTouchOutside(false);
                lodingbar.show();

                Uri resulturi = result.getUri();

                final String currentuserID = mAuth.getCurrentUser().getUid();

                final StorageReference filepath = userprofileimageref.child(currentuserID+".jpg");

                filepath.putFile(resulturi)

                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {


                                if (task.isSuccessful()){
                                    Toast.makeText(SettingActivity.this, "image upload sucessfuly", Toast.LENGTH_SHORT).show();

                                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                           final String downloadurl = uri.toString();


                                            rootref.child("users").child(currentuserID).child("image")
                                                    .setValue(downloadurl)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if (task.isSuccessful()){
                                                                Toast.makeText(SettingActivity.this, "image save in database sucessfully", Toast.LENGTH_SHORT).show();

                                                                lodingbar.dismiss();
                                                            }
                                                            else {

                                                                String meassage = task.getException().toString();
                                                                Toast.makeText(SettingActivity.this, "Error:- "+meassage, Toast.LENGTH_SHORT).show();

                                                                lodingbar.dismiss();
                                                            }

                                                        }
                                                    });

                                        }
                                    });






                                }
                                else {
                                    String message = task.getException().toString();
                                    Toast.makeText(SettingActivity.this, "error:-"+message, Toast.LENGTH_SHORT).show();
                                    lodingbar.dismiss();
                                }

                            }
                        });


            }

        }
    }
}
