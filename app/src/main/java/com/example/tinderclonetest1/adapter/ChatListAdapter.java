package com.example.tinderclonetest1.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tinderclonetest1.MessageActivity;
import com.example.tinderclonetest1.R;
import com.example.tinderclonetest1.placeholder.CardStackViewPlaceHolder;
import com.example.tinderclonetest1.placeholder.ChatsList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    List<CardStackViewPlaceHolder> placeHolderList;
    Context context;

    public ChatListAdapter(List<CardStackViewPlaceHolder> placeHolderList, Context context) {
        this.placeHolderList = placeHolderList;
        this.context = context;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        CardStackViewPlaceHolder placeHolder = placeHolderList.get(position);

        holder.username.setText(placeHolder.getUsername());
        if (placeHolder.getIsOnline().equals("online")) {
            holder.isonline.setVisibility(View.VISIBLE);
        } else {
            holder.isonline.setVisibility(View.GONE);
        }

        if (placeHolder.getFirstImageUrl().equals("default")) {
            holder.proPic.setImageResource(R.drawable.no_p);
        } else {
            Glide.with(context).load(placeHolder.getFirstImageUrl()).into(holder.proPic);
        }

        getLastMessage(holder.lastMessage, placeHolder.getUserId());

    }
    //get all message which i have received or sent and sent the tex to textview
    private void getLastMessage(final TextView lastMessage, final String userId) {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ChatsList chatsList = snapshot.getValue(ChatsList.class);

                    assert chatsList != null;
                    assert firebaseUser != null;
                    if (chatsList.getSenderId().equals(firebaseUser.getUid()) && chatsList.getReceiverId().equals(userId) ||
                            chatsList.getReceiverId().equals(firebaseUser.getUid()) && chatsList.getSenderId().equals(userId)){
                        lastMessage.setText(chatsList.getMessage());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return placeHolderList.size();
    }

    public void setPlaceHolderList(List<CardStackViewPlaceHolder> placeHolderList) {
        this.placeHolderList = placeHolderList;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView proPic;
        ImageView isonline;
        TextView username;
        TextView lastMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            proPic = itemView.findViewById(R.id.profile_pic);
            isonline = itemView.findViewById(R.id.is_online);
            username = itemView.findViewById(R.id.username);
            lastMessage = itemView.findViewById(R.id.last_sent);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    CardStackViewPlaceHolder placeHolder = placeHolderList.get(getAdapterPosition());
                    Intent intent = new Intent(context, MessageActivity.class);
                    intent.putExtra("id",placeHolder.getUserId());
                    context.startActivity(intent);

                }
            });
        }
    }
}
