package com.example.tinderclonetest1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class LoginNumActivity extends AppCompatActivity {

    EditText email;
    EditText password;
    Button loginBnt;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    ProgressBar progressBar;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_num);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginBnt = findViewById(R.id.log_bnt);
        firebaseAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progBar);
        intent = getIntent();

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String num = s.toString();
                if (num.isEmpty()) {
                    loginBnt.setBackgroundResource(R.drawable.login_btn_background2);
                    loginBnt.setTextColor(Color.parseColor("#BDBCBC"));
                } else {
                    loginBnt.setBackgroundResource(R.drawable.login_btn_background);
                    loginBnt.setTextColor(Color.parseColor("#FFFFFF"));

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String num = s.toString();
                if (num.isEmpty()) {
                    loginBnt.setBackgroundResource(R.drawable.login_btn_background2);
                    loginBnt.setTextColor(Color.parseColor("#BDBCBC"));
                } else {
                    loginBnt.setBackgroundResource(R.drawable.login_btn_background);
                    loginBnt.setTextColor(Color.parseColor("#FFFFFF"));

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void finish(View view) {
        finish();
    }

    public void continueLogin(View view) {

        String email = this.email.getText().toString();
        String password = this.password.getText().toString();
        progressBar.setVisibility(View.VISIBLE);

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "email or password is empty", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "password most be at lest 6 character", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        //check if user clicked log in
        if (intent.hasExtra("login")) {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(LoginNumActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    }
                }


            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginNumActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            //else sign up user
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        //get this user authentication id
                        String user = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(user);
                        Map<String, String> map = new HashMap<>();
                        map.put("userId", user);
                        map.put("username", "NO USERNAME YET");
                        map.put("firstImageUrl", "default");
                        map.put("secondImageUrl", "default");
                        map.put("gender", "none");
                        map.put("age", "none");
                        map.put("city","none");
                        map.put("about","no about");
                        map.put("isOnline", "offline");

                        databaseReference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(LoginNumActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);

                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(LoginNumActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginNumActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}
