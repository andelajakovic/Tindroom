package com.example.tindroom.ui.postlogin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tindroom.R;
import com.example.tindroom.data.local.SharedPreferencesStorage;
import com.example.tindroom.data.model.Faculty;
import com.example.tindroom.data.model.Swipe;
import com.example.tindroom.data.model.User;
import com.example.tindroom.network.RetrofitService;
import com.example.tindroom.network.TindroomApiService;
import com.example.tindroom.utils.LoadingDialogBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ChatFragment extends Fragment {

    View rootView;
    private TindroomApiService tindroomApiService;
    private User sessionUser;
    static ArrayList<Swipe> swipes = new ArrayList<>();
    static ArrayList<User> chatUsers = new ArrayList<>();
    private RecyclerView recyclerView;
    private ChatAdapter.RecyclerViewClickListener listener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        return rootView;

    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Retrofit retrofit = RetrofitService.getRetrofit();
        tindroomApiService = retrofit.create(TindroomApiService.class);
        sessionUser = SharedPreferencesStorage.getSessionUser(requireContext());

        getSwipes();
        initViews();
        initListeners();
        Log.d("chatusers", chatUsers.toString());
        setOnClickListener();

    }
    private void initViews() {
        recyclerView = rootView.findViewById(R.id.recyclerView);
    }

    private void initListeners() {

    }

    private void getSwipes() {
        Call<List<Swipe>> usersSwipesCall = tindroomApiService.getUsersSwipes(sessionUser.getUserId());
        usersSwipesCall.enqueue(new Callback<List<Swipe>>() {
            @Override
            public void onResponse(final Call<List<Swipe>> call, final Response<List<Swipe>> response) {
                if (response.body() != null) {
                    addData(response.body());
                }
            }
            @Override
            public void onFailure(final Call<List<Swipe>> call, final Throwable t) {
                Toast.makeText(getContext(), getResources().getString(R.string.unexpected_error_occurred), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private ArrayList<User> addData(List<Swipe> data){
        swipes.addAll(data);
        if(!swipes.isEmpty()){
            for(Swipe swipe : swipes){
                User user = new User();
                if(swipe.isSwipe_1() != null && swipe.isSwipe_2() != null && swipe.isSwipe_1() && swipe.isSwipe_2() ){
                    if(swipe.getUserId1().equals(sessionUser.getUserId())){
                        user.setUserId(swipe.getUserId2());
                    }else{
                        user.setUserId(swipe.getUserId1());
                    }
                    addChatUser(user);
                }
            }
        }
        Log.d("!!!", chatUsers.toString());
        return chatUsers;
    }

    private void addChatUser(User user){
        if(!chatUsers.contains(user)){
            Log.d("usao u if", chatUsers.toString());
            Call<User> userIdCall = tindroomApiService.getUserById(user.getUserId());
            userIdCall.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if(response.body() != null){
                        chatUsers.add(response.body());
                    }
                }
                @Override
                public void onFailure(Call<User> call, Throwable t) {

                }
            });
            Log.d("chatuserss", chatUsers.toString());
        }
        Log.d("nije usao u if", chatUsers.toString());
        addAdapter();
    }

    private void addAdapter(){
        ChatAdapter chatAdapter = new ChatAdapter(getActivity(), chatUsers, listener);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(chatAdapter);
    }

    private void setOnClickListener() {
        listener = new ChatAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position, ArrayList<User> chatUsers) {
                Fragment selectedFragment = null;
                selectedFragment = new MessageFragment().newInstance(chatUsers.get(position));
                Log.d("andrea", chatUsers.get(position).toString());
                navigateToMessageFragment(rootView);
            }
        };
    }

    public void navigateToMessageFragment (View view) {
        NavDirections action = ChatFragmentDirections.actionChatFragmentToMessageFragment();
        Navigation.findNavController(view).navigate(action);
    }

}