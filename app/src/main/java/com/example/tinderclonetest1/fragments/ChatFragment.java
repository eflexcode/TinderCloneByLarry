package com.example.tinderclonetest1.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.tinderclonetest1.R;
import com.example.tinderclonetest1.adapter.ChatListAdapter;
import com.example.tinderclonetest1.placeholder.CardStackViewPlaceHolder;
import com.example.tinderclonetest1.placeholder.ChatsIds;
import com.example.tinderclonetest1.placeholder.ChatsList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ChatFragment extends Fragment {

    RecyclerView recyclerView;
    ChatListAdapter chatListAdapter;
    List<String> allChats = new ArrayList<>();
    List<CardStackViewPlaceHolder> thisUsers = new ArrayList<>();
    EditText search;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = view.findViewById(R.id.chat_list_recycler);
        search = view.findViewById(R.id.search);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //get all user i have liked or super like ids
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allChats.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatsIds chatsList = snapshot.getValue(ChatsIds.class);
                    allChats.add(chatsList.getId());
                }
                //get all user whit this ids
                getAllUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String changedText = s.toString();
                searchNow(changedText);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    //filter the chat list
    private void searchNow(String changedText) {

        List<CardStackViewPlaceHolder> cloneThisUser = new ArrayList<>(thisUsers);
        List<CardStackViewPlaceHolder> filteredThisUser = new ArrayList<>();

        for (CardStackViewPlaceHolder placeHolder : cloneThisUser) {
            if (placeHolder.getUsername().toLowerCase().contains(changedText.toLowerCase())) {
                filteredThisUser.add(placeHolder);
            }

        }
        //send the filter list to adapter
        chatListAdapter = new ChatListAdapter(filteredThisUser, getContext());
        recyclerView.setAdapter(chatListAdapter);


        //may be when i changed my mind
//        Query query = FirebaseDatabase.getInstance().getReference("Users").startAt(changedText).orderByChild("username").endAt(changedText + "\uf8ff");
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                thisUsers.clear();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    CardStackViewPlaceHolder placeHolder = snapshot.getValue(CardStackViewPlaceHolder.class);
//                    thisUsers.add(placeHolder);
//                }
//                chatListAdapter = new ChatListAdapter(thisUsers, getContext());
//                recyclerView.setAdapter(chatListAdapter);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }

    private void getAllUsers() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                thisUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CardStackViewPlaceHolder placeHolder = snapshot.getValue(CardStackViewPlaceHolder.class);
                    //loop throw all the id in id list
                    for (String id : allChats) {
                        //if id is in all user add to list
                        if (placeHolder.getUserId().equals(id)) {
                            thisUsers.add(placeHolder);
                        }
                    }
                }

                //send list to adapter
                chatListAdapter = new ChatListAdapter(thisUsers, getContext());
                recyclerView.setAdapter(chatListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
