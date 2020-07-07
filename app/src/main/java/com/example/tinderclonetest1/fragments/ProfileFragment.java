package com.example.tinderclonetest1.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tinderclonetest1.EditInfoActivity;
import com.example.tinderclonetest1.LoginSignUpActivity;
import com.example.tinderclonetest1.R;
import com.example.tinderclonetest1.UploadImageActivity;
import com.example.tinderclonetest1.placeholder.CardStackViewPlaceHolder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    Button logoutBnt;
    CircleImageView proPics;
    TextView username;
    RelativeLayout edit;
    RelativeLayout medai;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        logoutBnt = view.findViewById(R.id.logout_bnt);
        proPics = view.findViewById(R.id.pro_pic);
        username = view.findViewById(R.id.user_name_age);
        edit = view.findViewById(R.id.edit);
        medai = view.findViewById(R.id.media);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditInfoActivity.class);
                startActivity(intent);
            }
        });

        medai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UploadImageActivity.class);
                startActivity(intent);
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseAuth.getUid());

        //fill view whit this user details
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CardStackViewPlaceHolder placeHolder = dataSnapshot.getValue(CardStackViewPlaceHolder.class);

                    if (placeHolder.getFirstImageUrl().equals("default")) {
                        proPics.setImageResource(R.drawable.no_p);
                    } else {
                        Glide.with(getContext()).load(placeHolder.getFirstImageUrl()).into(proPics);

                    }
                    username.setText(placeHolder.getUsername() + "," + placeHolder.getAge());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //do logout
        logoutBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                        .setTitle("logout")
                        .setMessage("are you sure you want to logout ")
                        .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                firebaseAuth.signOut();
                                Intent intent = new Intent(getContext(), LoginSignUpActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);

                            }
                        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        return view;
    }
}
