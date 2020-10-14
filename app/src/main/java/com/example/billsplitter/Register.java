package com.example.billsplitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    public static final String TAG = "TAG";
    EditText name, email, password, phone;
    TextView loginLink;
    Button registerButton;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    FirebaseFirestore fStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = findViewById(R.id.profileName);
        email = findViewById(R.id.profileEmail);
        password = findViewById(R.id.Password);
        phone = findViewById(R.id.profilePhone);
        registerButton = findViewById(R.id.register);
        loginLink = findViewById(R.id.loginLink);
        progressBar = findViewById(R.id.progressBar);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String Email = email.getText().toString().trim();
                final String Password = password.getText().toString().trim();
                final String Phone = phone.getText().toString().trim();
                final String Name = name.getText().toString();


                if(TextUtils.isEmpty(Email)){
                    email.requestFocus();
                    email.setError("Email is required");
                    return;
                }
                if(TextUtils.isEmpty(Password)){
                    password.requestFocus();
                    password.setError("Password cannot be empty");
                    return;
                }
                if(Password.length()<6){
                    password.requestFocus();
                    password.setError("Password must be atleast 6 characters long");
                    return;
                }
                if(TextUtils.isEmpty(Phone)){
                    phone.requestFocus();
                    phone.setError("Phone Number cannot be empty");
                    return;
                }
                if(Phone.length()<10){
                    phone.requestFocus();
                    phone.setError("Invalid Phone number");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);

                //register user in Firebase
                fAuth.createUserWithEmailAndPassword(Email,Password). addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Register.this,"Account created",Toast.LENGTH_SHORT).show();
                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            Map<String,Object>user = new HashMap<>();
                            user.put("name",Name);
                            user.put("email",Email);
                            user.put("phone",Phone);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG,"on Success : User profile is created for"+userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "on Failure : "+e.toString());
                                }
                            });

                            startActivity(new Intent(getApplicationContext(),Login.class));
                        } else {
                            Toast.makeText(Register.this, "An error occurred!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });
    }
}