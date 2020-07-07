package com.example.tinderclonetest1.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.Toast;

import com.example.tinderclonetest1.R;
import com.example.tinderclonetest1.adapter.CardStackViewAdapter;
import com.example.tinderclonetest1.placeholder.CardStackViewPlaceHolder;
import com.example.tinderclonetest1.placeholder.ChatsIds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.Duration;
import com.yuyakaido.android.cardstackview.RewindAnimationSetting;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MatchFragment extends Fragment {

    CardStackView cardStackView;
    CardStackViewAdapter adapter;
    CardStackLayoutManager layoutManager;
    DatabaseReference databaseReference;
    DatabaseReference databaseReferenceGender;
    DatabaseReference martchReference;

    List<CardStackViewPlaceHolder> placeHolderList = new ArrayList<>();
    List<ChatsIds> allChats = new ArrayList<>();
    String myGender;
    FirebaseUser firebaseUser;
    FloatingActionButton like;
    FloatingActionButton nope;
    FloatingActionButton reload;
    FloatingActionButton superL;
    FloatingActionButton superNope;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_match, container, false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReferenceGender = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        databaseReferenceGender.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                CardStackViewPlaceHolder placeHolder = dataSnapshot.getValue(CardStackViewPlaceHolder.class);
                assert placeHolder != null;
                myGender = placeHolder.getGender();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference idReference = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid());
        idReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allChats.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatsIds chatsIds = snapshot.getValue(ChatsIds.class);
                    assert chatsIds != null;
                    allChats.add(chatsIds);
                }
                getAllUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        like = view.findViewById(R.id.like);
        nope = view.findViewById(R.id.nope);
        reload = view.findViewById(R.id.reload);
        superL = view.findViewById(R.id.superL);
        superNope = view.findViewById(R.id.super_nope);
        cardStackView = view.findViewById(R.id.card_sack_view);

        adapter = new CardStackViewAdapter(getContext());

        martchReference = FirebaseDatabase.getInstance().getReference("Chats");

        layoutManager = new CardStackLayoutManager(getContext(), new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {

            }

            @Override
            public void onCardSwiped(Direction direction) {

                if (direction == Direction.Right || direction == Direction.Top) {

                    //get swipe position
                    int position = layoutManager.getTopPosition() - 1;

                    // get my id
                    String senderId = firebaseUser.getUid();
                    final CardStackViewPlaceHolder viewPlaceHolder = placeHolderList.get(position);
                    //get other user id from position

                    String receiverId = viewPlaceHolder.getUserId();

                    Map<String, Object> chatMap = new HashMap<>();
                    chatMap.put("senderId", senderId);
                    chatMap.put("receiverId", receiverId);
                    chatMap.put("imageUrl","default");
                    chatMap.put("message", "hi i like you");
                    chatMap.put("isseen",false);

                    //send a message
                    martchReference.push().setValue(chatMap);

                    Toast.makeText(getContext(), viewPlaceHolder.getUsername(), Toast.LENGTH_SHORT).show();
                    final DatabaseReference chatList = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid()).child(viewPlaceHolder.getUserId());
                    final DatabaseReference chatList2 = FirebaseDatabase.getInstance().getReference("ChatList").child(viewPlaceHolder.getUserId()).child(firebaseUser.getUid());


                    final Map<String, String> chatListMap1 = new HashMap<>();
                    chatListMap1.put("id", viewPlaceHolder.getUserId());

                    final Map<String, String> chatListMap2 = new HashMap<>();
                    chatListMap2.put("id",firebaseUser.getUid());

                    // add to my chatlist ids and that of other user
                    chatList.setValue(chatListMap1);
                    chatList2.setValue(chatListMap2);

                }
            }

            @Override
            public void onCardRewound() {

            }

            @Override
            public void onCardCanceled() {

            }

            @Override
            public void onCardAppeared(View view, int position) {

            }

            @Override
            public void onCardDisappeared(View view, int position) {

            }
        });


        //just add this
        layoutManager.setStackFrom(StackFrom.None);
        layoutManager.setMaxDegree(30);
        layoutManager.setSwipeThreshold(0.3f);
        layoutManager.setDirections(Direction.FREEDOM);
        layoutManager.setOverlayInterpolator(new AccelerateInterpolator());
        cardStackView.setLayoutManager(layoutManager);



        // like nope super like rewind  super nope button whit there functions
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwipeAnimationSetting swipeAnimationSetting = new SwipeAnimationSetting.Builder()
                        .setDuration(Duration.Normal.duration)
                        .setDirection(Direction.Right)
                        .setInterpolator(new AccelerateInterpolator())
                        .build();

                layoutManager.setSwipeAnimationSetting(swipeAnimationSetting);
                cardStackView.swipe();

            }
        });

        nope.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwipeAnimationSetting swipeAnimationSetting = new SwipeAnimationSetting.Builder()
                        .setDuration(Duration.Normal.duration)
                        .setDirection(Direction.Left)
                        .setInterpolator(new AccelerateInterpolator())
                        .build();

                layoutManager.setSwipeAnimationSetting(swipeAnimationSetting);
                cardStackView.swipe();

            }
        });

        superNope.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwipeAnimationSetting swipeAnimationSetting = new SwipeAnimationSetting.Builder()
                        .setDuration(Duration.Normal.duration)
                        .setDirection(Direction.Bottom)
                        .setInterpolator(new AccelerateInterpolator())
                        .build();

                layoutManager.setSwipeAnimationSetting(swipeAnimationSetting);
                cardStackView.swipe();

            }
        });

        superL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwipeAnimationSetting swipeAnimationSetting = new SwipeAnimationSetting.Builder()
                        .setDuration(Duration.Normal.duration)
                        .setDirection(Direction.Top)
                        .setInterpolator(new AccelerateInterpolator())
                        .build();

                layoutManager.setSwipeAnimationSetting(swipeAnimationSetting);
                cardStackView.swipe();

            }
        });


        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RewindAnimationSetting rewindAnimationSetting = new RewindAnimationSetting.Builder()
                        .setDirection(Direction.Bottom)
                        .setDuration(Duration.Normal.duration)
                        .setInterpolator(new AccelerateInterpolator())
                        .build();
                layoutManager.setRewindAnimationSetting(rewindAnimationSetting);
                cardStackView.rewind();
            }
        });

        return view;
    }

    private void getAllUsers() {

       DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                placeHolderList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    CardStackViewPlaceHolder cardStackViewPlaceHolder = snapshot.getValue(CardStackViewPlaceHolder.class);

                    assert cardStackViewPlaceHolder != null;

                    //if other user gender is not equal to my gender add user to list
                    if (!myGender.equals(cardStackViewPlaceHolder.getGender())) {
                        placeHolderList.add(cardStackViewPlaceHolder);

                        //if i have liked or super like other user remove from list
                        for (ChatsIds id : allChats){
                            if (id.getId().equals(cardStackViewPlaceHolder.getUserId())){
                                placeHolderList.remove(cardStackViewPlaceHolder);
                            }
                        }

                    }
                }

                //send list to adapter
                adapter.setPlaceHolderList(placeHolderList);
                cardStackView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
