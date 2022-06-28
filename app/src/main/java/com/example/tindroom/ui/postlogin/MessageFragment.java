package com.example.tindroom.ui.postlogin;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;


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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageFragment extends Fragment {

    private User sessionUser;
    private User chatUser;
    View rootView;
    private RecyclerView recyclerView;
    ArrayList<Chat> chatList;
    DatabaseReference reference;
    private StorageReference mStorageReference;
    final String FOLDER_NAME = "users";

    private ImageButton sendButton;
    private EditText textMessage;
    private TextView name;
    private ImageButton backButton;
    private CircleImageView profilePic;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            chatUser = getArguments().getParcelable("chatUser");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_message, container, false);
        sessionUser = SharedPreferencesStorage.getSessionUser(requireContext());
        reference = FirebaseDatabase.getInstance("https://tindroom-64323-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Chats");

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        chatList = new ArrayList<>();

        MessageAdapter messageAdapter = new MessageAdapter(getActivity(), chatUser, sessionUser, chatList);

        initViews();
        initListeners();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(messageAdapter);

        recyclerView.scrollToPosition(messageAdapter.getItemCount()-1);
        readMessage();
    }

    public void initViews(){
        sendButton = rootView.findViewById(R.id.sendButton);
        textMessage = rootView.findViewById(R.id.textMessage);
        name = rootView.findViewById(R.id.name);
        name.setText(chatUser.getName());
        backButton = rootView.findViewById(R.id.back);
        profilePic = rootView.findViewById(R.id.profilePic);

        Context cont = requireContext();
        Glide.with(cont)
                .asBitmap()
                .load(chatUser.getImageUrl())
                .error(cont.getResources().getDrawable(R.drawable.avatar_placeholder))
                .into(profilePic);

    }

    public void initListeners(){
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = textMessage.getText().toString();
                if (!msg.equals("")){
                    String sendUser = sessionUser.getUserId();
                    String recUser = chatUser.getUserId();
                    sendMessage(sendUser, recUser, msg);
                }
                textMessage.setText("");
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToMessageFragment(rootView);
            }
        });
    }

    private void sendMessage(String sender, String receiver, String message){
        DatabaseReference reference = FirebaseDatabase.getInstance("https://tindroom-64323-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);

        reference.child("Chats").push().setValue(hashMap);
    }

    private void readMessage(){
        String sessionId = sessionUser.getUserId();
        String chatUserId = chatUser.getUserId();
        chatList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance("https://tindroom-64323-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(sessionId) && chat.getSender().equals(chatUserId) || chat.getReceiver().equals(chatUserId) && chat.getSender().equals(sessionId)){
                        chatList.add(chat);
                    }
                    if(!chatList.isEmpty()){
                        MessageAdapter messageAdapter = new MessageAdapter(getActivity(), chatUser, sessionUser, chatList);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

                        recyclerView.setLayoutManager(linearLayoutManager);
                        recyclerView.setAdapter(messageAdapter);

                        recyclerView.scrollToPosition(messageAdapter.getItemCount()-1);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });
    }


    public void navigateToMessageFragment (View view) {
        NavDirections action = MessageFragmentDirections.actionMessageFragmentToChatFragment();
        Navigation.findNavController(view).navigate(action);
    }

}