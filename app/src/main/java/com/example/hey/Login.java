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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {


    EditText email,password;
    Button loginbtn,phone;
    TextView needaccount,forgetpassword;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        cast();

        mAuth = FirebaseAuth.getInstance();


        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userlogin();
            }
        });



        needaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendusertoregister();

            }
        });

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phonelogintent = new Intent(Login.this,PhoneLogin.class);
                startActivity(phonelogintent);
            }
        });

    }

    private void userlogin() {

        String Email = email.getText().toString();
        String Password = password.getText().toString();

        if (TextUtils.isEmpty(Email));{
            Toast.makeText(this, "Enter email address", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(Password)){
            Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show();

        }
        else
        {
            progressDialog.setTitle("Sign in ");
            progressDialog.setMessage("Please wait...");
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(Email,Password)
                    .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful())
                    {
                        sendusertomain();
                        Toast.makeText(Login.this, "Login Sucessfull", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                    else
                    {
                        String message = task.getException().toString();
                        Toast.makeText(Login.this, "Error:-"+message, Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }

                }
            });
        }

    }

    private void cast() {

        progressDialog = new ProgressDialog(this);
        email = findViewById(R.id.login_userid);
        password = findViewById(R.id.login_password);
        loginbtn =  findViewById(R.id.loginbutton);
        phone = findViewById(R.id.phoneLogin);
        needaccount = findViewById(R.id.need_account);
        forgetpassword = findViewById(R.id.forget_password);

    }



    private void sendusertomain() {
        Intent mainintent = new Intent(getApplicationContext(),MainActivity.class);
        mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainintent);
        finish();
    }

    private void sendusertoregister() {
        Intent intent = new Intent(getApplicationContext(),Register.class);
        startActivity(intent);
    }
}
