package com.example.tinderclonetest1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tinderclonetest1.placeholder.CardStackViewPlaceHolder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class OtherUserDetailsActivity extends AppCompatActivity {

    ImageView proPic;
    TextView username;
    TextView city;
    TextView about;
    TextView age;

    Intent intent;
    String getId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_details);

        intent = getIntent();
        getId = intent.getStringExtra("id");

        proPic = findViewById(R.id.profile_pic);
        username = findViewById(R.id.username);
        city = findViewById(R.id.city);
        about = findViewById(R.id.about);
        age = findViewById(R.id.age);

        //fill view whit other user details
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(getId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                CardStackViewPlaceHolder placeHolder = dataSnapshot.getValue(CardStackViewPlaceHolder.class);
                assert placeHolder != null;

                if (placeHolder.getFirstImageUrl().equals("default")){
                    proPic.setImageResource(R.drawable.no_p);
                }else {
                    Glide.with(OtherUserDetailsActivity.this).load(placeHolder.getFirstImageUrl()).into(proPic);
                }

                username.setText(placeHolder.getUsername()+",");
                age.setText(placeHolder.getAge());
                city.setText(placeHolder.getCity());
                about.setText(placeHolder.getAbout());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void finish(View view) {
        finish();
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
