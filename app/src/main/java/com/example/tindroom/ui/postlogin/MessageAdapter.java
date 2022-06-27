package com.example.tindroom.ui.postlogin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tindroom.R;
import com.example.tindroom.data.model.Chat;
import com.example.tindroom.data.model.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.Viewholder> {

    private Context context;
    private User chatUser;

    private ArrayList<Chat> chatList;
    User sessionUser;

    private StorageReference mStorageReference;
    private final String FOLDER_NAME = "volarevic";

    // Constructor
    public MessageAdapter(Context context, User chatUser, ArrayList<Chat> chatList) {
        this.context = context;
        this.chatUser = chatUser;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public MessageAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_layout, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.Viewholder holder, int position) {
        // to set data to textview and imageview of each card layout
        Chat model = chatList.get(position);

        String sessionId = sessionUser.getUserId();

        if(chatList.get(position).getSender().equals(sessionId)){
            holder.myMessage.setText("" + model.getMessage());
            holder.myMessage.setVisibility(View.VISIBLE);
            holder.othersLayout.setVisibility(View.GONE);
        } else {
            holder.othersMessage.setText("" + model.getMessage());
            holder.othersLayout.setVisibility(View.VISIBLE);
            holder.myMessage.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }


    public class Viewholder extends RecyclerView.ViewHolder {
        private ImageView othersProfilePic;
        private TextView othersMessage, myMessage;
        private LinearLayout othersLayout;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            othersProfilePic = itemView.findViewById(R.id.othersProfilePic);
            othersMessage = itemView.findViewById(R.id.othersMessage);
            myMessage = itemView.findViewById(R.id.myMessage);
            othersLayout = itemView.findViewById(R.id.othersLayout);

            String idOfUser = chatUser.getUserId();
//            mStorageReference = FirebaseStorage.getInstance().getReference().child("images/"+FOLDER_NAME+"/usr" + idOfUser + "/pic1");

            Context cont = context.getApplicationContext();
//            Glide.with(cont)
//                    .load(mStorageReference)
//                    .error(R.drawable.avatar_placeholder)
//                    .into(othersProfilePic);
        }
    }
}