package com.example.tinderclonetest1.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tinderclonetest1.R;
import com.example.tinderclonetest1.placeholder.ChatsList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagingAdapter extends RecyclerView.Adapter<MessagingAdapter.ViewHolder> {

    List<ChatsList> chatListList;
    Context context;
    String imageUrl;

    public static final int Me = 1;
    public static final int You = 2;

    public MessagingAdapter(List<ChatsList> chatListList, Context context, String imageUrl) {
        this.chatListList = chatListList;
        this.context = context;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == Me) {
            View view = LayoutInflater.from(context).inflate(R.layout.me_message_layout, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.you_message_layout, parent, false);
            return new ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatsList chatList = chatListList.get(position);

        if (chatList.getMessage().equals("image")){
            holder.messageText.setVisibility(View.GONE);
        }else {
            holder.messageText.setText(chatList.getMessage());
        }

        holder.messageText.setText(chatList.getMessage());

        if (imageUrl.equals("default")) {
            holder.proPics.setImageResource(R.drawable.no_p);
        } else {
            Glide.with(context).load(imageUrl).into(holder.proPics);
        }

        if (chatList.getImageUrl().equals("default")) {
            holder.sendImage.setVisibility(View.GONE);
        } else {
            holder.sendImage.setVisibility(View.VISIBLE);
            Glide.with(context).load(chatList.getImageUrl()).into(holder.sendImage);
        }

        //check if recycle view is in the last position
        if (position == chatListList.size() - 1) {
            holder.ifIsSeen.setVisibility(View.VISIBLE);
            if (chatList.isIssen()) {
                holder.ifIsSeen.setText("seen");
            } else {
                holder.ifIsSeen.setText("sent");
            }
        }else{
            holder.ifIsSeen.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return chatListList.size();
    }


    @Override
    public int getItemViewType(int position) {
        ChatsList chatList = chatListList.get(position);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String id = firebaseUser.getUid();

        //check if i am the sender
        if (chatList.getSenderId().equals(id)) {
            return Me;
        } else {
            return You;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView proPics;
        ImageView sendImage;
        TextView messageText;
        TextView ifIsSeen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            proPics = itemView.findViewById(R.id.chat_pro_pics);
            sendImage = itemView.findViewById(R.id.sent_image);
            messageText = itemView.findViewById(R.id.message_text);
            ifIsSeen = itemView.findViewById(R.id.if_is_seen);

        }
    }
}
