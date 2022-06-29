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
import com.example.tindroom.data.local.SharedPreferencesStorage;
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
    private final String FOLDER_NAME = "pictures";

    // Constructor
    public MessageAdapter(Context context, User chatUser, User sessionUser, ArrayList<Chat> chatList) {
        this.context = context;
        this.chatUser = chatUser;
        this.chatList = chatList;
        this.sessionUser = sessionUser;
    }

    @NonNull
    @Override
    public MessageAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_layout, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.Viewholder holder, int position) {
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

            Context cont = context.getApplicationContext();
            Glide.with(cont)
                    .asBitmap()
                    .load(chatUser.getImageUrl())
                    .error(cont.getResources().getDrawable(R.drawable.avatar_placeholder))
                    .into(othersProfilePic);
        }
    }
}