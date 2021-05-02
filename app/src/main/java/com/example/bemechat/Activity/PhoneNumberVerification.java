package com.example.bemechat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.bemechat.databinding.ActivityPhoneNumberVerificationBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneNumberVerification extends AppCompatActivity {

    private ActivityPhoneNumberVerificationBinding binding;

    private PhoneAuthProvider.ForceResendingToken forceResendingToken;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private String mVerificationId;

    private FirebaseAuth firebaseAuth;

    private static final String TAG = "MAIN_TAG";

    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneNumberVerificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.PhoneNumberVerifyLl.setVisibility(View.VISIBLE);
        binding.OTPVerifyLl.setVisibility(View.GONE);

        firebaseAuth = FirebaseAuth.getInstance();

        pd = new ProgressDialog(this);
        pd.setTitle("Please wait...");
        pd.setCanceledOnTouchOutside(false);

        if (firebaseAuth.getCurrentUser() != null){
            Intent intent = new Intent(PhoneNumberVerification.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                pd.dismiss();
                Toast.makeText(PhoneNumberVerification.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verificationId, forceResendingToken);
                Log.d(TAG,"onCodeSent: "+verificationId);

                mVerificationId = verificationId;
                forceResendingToken = token;
                pd.dismiss();

                binding.PhoneNumberVerifyLl.setVisibility(View.GONE);
                binding.OTPVerifyLl.setVisibility(View.VISIBLE);

                Toast.makeText(PhoneNumberVerification.this, "Verification code sent...", Toast.LENGTH_SHORT).show();

                binding.MobileNoTv.setText(""+binding.inputMobileEt.getText().toString().trim());
            }
        };

        binding.GetOTPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = binding.inputMobileEt.getText().toString().trim();
                if (TextUtils.isEmpty(phone)){
                    Toast.makeText(PhoneNumberVerification.this, "Please enter phone number...", Toast.LENGTH_SHORT).show();
                }
                else{
                    startPhoneNumberVerification(phone);
                }
            }
        });

        binding.ResendOTPTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = binding.inputMobileEt.getText().toString().trim();
                if (TextUtils.isEmpty(phone)){
                    Toast.makeText(PhoneNumberVerification.this, "Please enter phone number...", Toast.LENGTH_SHORT).show();
                }
                else{
                    resendVerificationCode(phone, forceResendingToken);
                }
            }
        });

        binding.VerifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = binding.OTPCodeEt.getText().toString().trim();
                if (TextUtils.isEmpty(code)){
                    Toast.makeText(PhoneNumberVerification.this, "Please enter verification code...", Toast.LENGTH_SHORT).show();
                }
                else{
                    verifyPhoneNumberWithCode(mVerificationId, code);
                }
            }
        });

    }


    private void startPhoneNumberVerification(String phone) {
        pd.setMessage("Verify Phone Number");
        pd.show();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phone)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void resendVerificationCode(String phone, PhoneAuthProvider.ForceResendingToken token){
        pd.setMessage("Resending code...");
        pd.show();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phone)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .setForceResendingToken(token)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyPhoneNumberWithCode(String VerificationId, String code) {
        pd.setMessage("Verifying Code");
        pd.show();

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(VerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        pd.setMessage("Logging In");

        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        pd.dismiss();
                        String phone = firebaseAuth.getCurrentUser().getPhoneNumber();
                        Toast.makeText(PhoneNumberVerification.this, "Logged in as"+phone, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(PhoneNumberVerification.this,ProfileInfoActivity.class);
                        startActivity(intent);
                        finishAffinity();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(PhoneNumberVerification.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}