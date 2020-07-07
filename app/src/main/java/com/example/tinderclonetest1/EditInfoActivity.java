package com.example.tinderclonetest1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tinderclonetest1.placeholder.CardStackViewPlaceHolder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class EditInfoActivity extends AppCompatActivity {

    ImageView pro;
    EditText username;
    EditText about;
    EditText city;
    EditText age;
    RadioButton male;
    RadioButton female;
    Toolbar toolbar;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);

        pro = findViewById(R.id.pic);
        username = findViewById(R.id.username);
        about = findViewById(R.id.about);
        city = findViewById(R.id.city);
        age = findViewById(R.id.age);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        setTitle("");

        //fill view whit this user details

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseAuth.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CardStackViewPlaceHolder placeHolder = dataSnapshot.getValue(CardStackViewPlaceHolder.class);

                if (placeHolder.getFirstImageUrl().equals("default")) {
                    pro.setImageResource(R.drawable.no_p);
                } else {
                    Glide.with(EditInfoActivity.this).load(placeHolder.getFirstImageUrl()).into(pro);
                }

                username.setText(placeHolder.getUsername());
                about.setText(placeHolder.getAbout());
                city.setText(placeHolder.getCity());
                age.setText(placeHolder.getAge());

                if (placeHolder.getGender().equals("male")) {
                    male.toggle();
                } else if (placeHolder.getGender().equals("female")) {
                    female.toggle();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void finish(View view) {
        finish();
    }

    public void update(View view) {

        String getUsername = username.getText().toString().toUpperCase();
        String getAbout = about.getText().toString();
        String getAge = age.getText().toString();
        String getCity = city.getText().toString();

        String getGender;

        if (male.isChecked()) {
            getGender = "male";
        } else if (female.isChecked()) {
            getGender = "female";
        } else {
            getGender = "none";
        }

        if (getUsername.length() > 20) {
            Toast.makeText(this, "username is too long at most 20 character", Toast.LENGTH_LONG).show();
            return;
        }

        if (getAbout.trim().isEmpty() || getAge.trim().isEmpty() || getCity.trim().isEmpty() || getUsername.trim().isEmpty()) {
            Toast.makeText(this, "all filed are required in other to update", Toast.LENGTH_LONG).show();
            return;
        }

        Map<String, Object> map = new HashMap<>();
        map.put("username", getUsername);
        map.put("about", getAbout);
        map.put("age", getAge);
        map.put("gender", getGender);
        map.put("city", getCity);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("updating user info");
        progressDialog.show();

        //update this user details
        databaseReference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(EditInfoActivity.this, "update successful", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(EditInfoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    //open upload activity
    public void addMedial(View view) {
        Intent intent = new Intent(this, UploadImageActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String fUser = firebaseUser.getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(fUser);

        Map<String, Object> map = new HashMap<>();
        map.put("isOnline", "online");

        databaseReference.updateChildren(map);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String fUser = firebaseUser.getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(fUser);

        Map<String, Object> map = new HashMap<>();
        map.put("isOnline", "offline");

        databaseReference.updateChildren(map);
    }
}
