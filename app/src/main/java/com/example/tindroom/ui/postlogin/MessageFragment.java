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
    final String FOLDER_NAME = "volarevic";

    private ImageButton sendButton;
    private EditText textMessage;
    private TextView name;
    private ImageButton backButton;
    private CircleImageView profilePic;


    public static MessageFragment newInstance(User chatUser){
        MessageFragment fragment = new MessageFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("ChatUser", (Serializable) chatUser);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_message, container, false);

        chatUser = (User) getArguments().getSerializable("ChatUser");
        sessionUser = SharedPreferencesStorage.getSessionUser(requireContext());

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = getView().findViewById(R.id.recyclerView);

        chatList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");

        MessageAdapter messageAdapter = new MessageAdapter(getActivity(), chatUser, chatList);

        initViews();
        initListeners();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(messageAdapter);

        recyclerView.scrollToPosition(messageAdapter.getItemCount()-1);



        readMessage();
    }

    public void initViews(){
        sendButton = getView().findViewById(R.id.sendButton);
        textMessage = getView().findViewById(R.id.textMessage);
        name = getView().findViewById(R.id.name);
        name.setText(chatUser.getName());
        backButton = getView().findViewById(R.id.back);
        profilePic = getView().findViewById(R.id.profilePic);

        int idOfUser = Integer.parseInt(chatUser.getUserId());
//        mStorageReference = FirebaseStorage.getInstance().getReference().child("images/"+FOLDER_NAME+"/usr" + idOfUser + "/pic1");

        Context cont = getActivity().getApplicationContext();
//        Glide.with(cont)
//                .load(mStorageReference)
//                .error(R.drawable.ic_baseline_person_24)
//                .into(profilePic);
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
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
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

        reference = FirebaseDatabase.getInstance().getReference("Chats");
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
                        MessageAdapter messageAdapter = new MessageAdapter(getActivity(), chatUser, chatList);
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