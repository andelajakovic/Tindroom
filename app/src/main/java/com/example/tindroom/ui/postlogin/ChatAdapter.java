package com.example.tindroom.ui.postlogin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tindroom.R;
import com.example.tindroom.data.local.SharedPreferencesStorage;
import com.example.tindroom.data.model.Chat;
import com.example.tindroom.data.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.Viewholder> {

    private Fragment context;
    private ArrayList<User> chatUsers;
    private User sessionUser;
    private RecyclerViewClickListener listener;

    private StorageReference mStorageReference;
    private final String FOLDER_NAME = "";

    public ChatAdapter(Fragment context, ArrayList<User> chatUsers, RecyclerViewClickListener listener) {
        this.context = context;
        this.chatUsers = chatUsers;
        this.listener = listener;
    }

    public class Viewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CircleImageView profilePic;
        private TextView name, message;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.profilePic);
            name = itemView.findViewById(R.id.name);
            message = itemView.findViewById(R.id.message);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            listener.onClick(v, getAdapterPosition(), chatUsers);
        }
    }

    @NonNull
    @Override
    public ChatAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        sessionUser = SharedPreferencesStorage.getSessionUser(context.getContext());

        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.Viewholder holder, int position) {
        User model = chatUsers.get(position);
        holder.name.setText(String.valueOf(model.getName()));

        Context cont = context.requireContext();
        Glide.with(cont)
                .asBitmap()
                .load(model.getImageUrl())
                .error(cont.getResources().getDrawable(R.drawable.avatar_placeholder))
                .into(holder.profilePic);

        String myid = sessionUser.getUserId();
        String userId = model.getUserId();
        ArrayList<Chat> chatList = new ArrayList<Chat>();

        DatabaseReference reference = FirebaseDatabase.getInstance("https://tindroom-64323-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userId) || chat.getReceiver().equals(userId) && chat.getSender().equals(myid)) {
                        holder.message.setText(chat.getMessage());
                        chatList.add(chat);
                    }
                    if (chatList.isEmpty()) {
                        holder.message.setText("Započnite razgovor!");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });

        //holder.courseIV.setImageResource(model.getCourse_image());
        //holder.profilePic.setImageResource();

    }

    @Override
    public int getItemCount() {
        return chatUsers.size();
    }

    public interface RecyclerViewClickListener{
        void onClick(View v, int position, ArrayList<User> chatUsers);
    }

}