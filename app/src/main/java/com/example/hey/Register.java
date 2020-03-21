package com.example.hey;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
    EditText user,password;
    Button register;
    TextView haveaccount;

    private DatabaseReference rootref;

    FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        rootref = FirebaseDatabase.getInstance().getReference();

        cast();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createaccount();
            }
        });

        haveaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendusertologin();

            }
        });
    }

    private void createaccount() {

        String email = user.getText().toString();
        String Password = password.getText().toString();

        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "enter email", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(Password))
        {
            Toast.makeText(this, "enter password", Toast.LENGTH_SHORT).show();
        }
        else{
            progressDialog.setTitle("Creating Account");
            progressDialog.setMessage("please Wait while we are creating account.....");
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(email,Password)
                    .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful())
                    {
                        String currentuserid = mAuth.getCurrentUser().getUid();
                        rootref.child("users").child(currentuserid).setValue("");
                        sendusertomainActivity();
                        Toast.makeText(Register.this, "Signup Sucessfully", Toast.LENGTH_SHORT).show();
                         progressDialog.dismiss();
                    }else {
                        String message = task.getException().toString();
                        Toast.makeText(Register.this, "Error:- "+message, Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }

                }
            });
        }




    }

    private void sendusertomainActivity() {
        Intent mainintent = new Intent(getApplicationContext(),MainActivity.class);
        mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainintent);
        finish();

    }

    private void sendusertologin() {
        Intent intent = new Intent(getApplicationContext(),Login.class);
        startActivity(intent);

    }

    private void cast() {
        progressDialog = new ProgressDialog(this);
        user = findViewById(R.id.register_userid);
        password = findViewById(R.id.register_password);
        register = findViewById(R.id.registerButton);
        haveaccount = findViewById(R.id.have_account);

    }
}
