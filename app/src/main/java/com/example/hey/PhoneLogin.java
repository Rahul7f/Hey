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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneLogin extends AppCompatActivity {

    EditText inputnumber ,inputOTP;
    Button sendcode,verify;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    ProgressDialog lodingbar;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        inputnumber = findViewById(R.id.phone_number);
        inputOTP = findViewById(R.id.OTP);
        sendcode = findViewById(R.id.sendcode);
        verify = findViewById(R.id.verifycode);
        mAuth = FirebaseAuth.getInstance();
        lodingbar = new ProgressDialog(this);


        sendcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String phoneNumber = inputnumber.getText().toString();
                if (TextUtils.isEmpty(phoneNumber))
                {
                    Toast.makeText(PhoneLogin.this, " Please Enter Phone Number first..", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    lodingbar.setTitle("Phone Login");
                    lodingbar.setMessage("please wait we are authenticating your Phone.. ");
                    lodingbar.setCanceledOnTouchOutside(false);
                    lodingbar.show();

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            PhoneLogin.this,               // Activity (for callback binding)
                            callbacks);        // OnVerificationStateChangedCallbacks


                }
            }
        });
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendcode.setVisibility(View.INVISIBLE);
                inputnumber.setVisibility(View.INVISIBLE);
                String verificationcode = inputOTP.getText().toString();
                if (TextUtils.isEmpty(verificationcode)){
                    Toast.makeText(PhoneLogin.this, "Please Enter Verification Code", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    lodingbar.setTitle("Verification code");
                    lodingbar.setMessage("please wait we are Verify Verification code...");
                    lodingbar.setCanceledOnTouchOutside(false);
                    lodingbar.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationcode);
                    signInWithPhoneAuthCredential(credential);
                }

            }
        });



        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                lodingbar.dismiss();
                Toast.makeText(PhoneLogin.this, "Invalid phone Number,enter correct countery code", Toast.LENGTH_SHORT).show();

                sendcode.setVisibility(View.VISIBLE);
                inputnumber.setVisibility(View.VISIBLE);

                inputOTP.setVisibility(View.INVISIBLE);
                verify.setVisibility(View.INVISIBLE);
            }

            public void onCodeSent( String verificationId,
                                    PhoneAuthProvider.ForceResendingToken token) {


                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                lodingbar.dismiss();

                Toast.makeText(PhoneLogin.this, "Code has been Sent", Toast.LENGTH_SHORT).show();

                sendcode.setVisibility(View.INVISIBLE);
                inputnumber.setVisibility(View.INVISIBLE);

                inputOTP.setVisibility(View.VISIBLE);
                verify.setVisibility(View.VISIBLE);


            }
        };


    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            lodingbar.dismiss();
                            Toast.makeText(PhoneLogin.this, "Congratulation you are sucessfully login", Toast.LENGTH_SHORT).show();

                            sendusertomain();
                        }
                        else
                        {
                            String message = task.getException().toString();
                            Toast.makeText(PhoneLogin.this, "Error:-"+message, Toast.LENGTH_SHORT).show();


                        }
                    }
                });
    }

    private void sendusertomain() {
        Intent mainintent = new Intent(getApplicationContext(),MainActivity.class);
        mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainintent);
        finish();
    }
}
