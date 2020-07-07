package com.example.tinderclonetest1.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tinderclonetest1.OtherUserDetailsActivity;
import com.example.tinderclonetest1.R;
import com.example.tinderclonetest1.placeholder.CardStackViewPlaceHolder;

import java.util.List;

public class CardStackViewAdapter extends RecyclerView.Adapter<CardStackViewAdapter.ViewHolder> {

    List<CardStackViewPlaceHolder> placeHolderList;
    Context context;

    public CardStackViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // inflated view for each cards
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_stack_view_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
// binging each view with each user details

        final CardStackViewPlaceHolder placeHolder = placeHolderList.get(position);

        holder.username.setText(placeHolder.getUsername());
        holder.city.setText(placeHolder.getCity());
        holder.age.setText(placeHolder.getAge());
        holder.about.setText(placeHolder.getAbout());

        if (placeHolder.getFirstImageUrl().equals("default")) {
            holder.imageView.setImageResource(R.drawable.no_p);
        } else {
            Glide.with(context).load(placeHolder.getFirstImageUrl()).into(holder.imageView);
        }


    }

    @Override
    public int getItemCount() {
        return placeHolderList.size();
    }

    public void setPlaceHolderList(List<CardStackViewPlaceHolder> placeHolderList) {
        this.placeHolderList = placeHolderList;
        notifyDataSetChanged();
        //reset list

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView username;
        TextView age;
        TextView about;
        TextView city;

        ImageView imageView;

        RelativeLayout details;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            age = itemView.findViewById(R.id.age);
            about = itemView.findViewById(R.id.about);
            city = itemView.findViewById(R.id.city);

            imageView = itemView.findViewById(R.id.userImage);

            details = itemView.findViewById(R.id.details);

            //opens details activity
            details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final CardStackViewPlaceHolder placeHolder = placeHolderList.get(getAdapterPosition());
                    Intent intent = new Intent(context, OtherUserDetailsActivity.class);
                    intent.putExtra("id", placeHolder.getUserId());
                    context.startActivity(intent);
                }
            });


        }
    }
}
