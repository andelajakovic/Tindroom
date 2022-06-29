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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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
    static ArrayList<Swipe> swipes;
    static ArrayList<User> chatUsers;
    private RecyclerView recyclerView;
    private ChatAdapter.RecyclerViewClickListener listener;
    LoadingDialogBar loadingDialogBar;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseUser chatUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        Retrofit retrofit = RetrofitService.getRetrofit();
        tindroomApiService = retrofit.create(TindroomApiService.class);
        sessionUser = SharedPreferencesStorage.getSessionUser(requireContext());
        loadingDialogBar = new LoadingDialogBar(getActivity());

        chatUsers = new ArrayList<>();
        swipes  = new ArrayList<>();

        getSwipes();
        initViews();
        initListeners();
        setOnClickListener();

        return rootView;

    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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

    private void addData(List<Swipe> data){
        swipes.addAll(data);
        User user = new User();
        String id = "";

        if(!swipes.isEmpty()){
            for(Swipe swipe : swipes){
                if(swipe.isSwipe_1() != null && swipe.isSwipe_2() != null && swipe.isSwipe_1() && swipe.isSwipe_2() ){
                    if(swipe.getUserId1().equals(sessionUser.getUserId())){
                        user.setUserId(swipe.getUserId2());
                        id = swipe.getUserId2();
                    }else{
                        user.setUserId(swipe.getUserId1());
                        id = swipe.getUserId1();
                    }
                    addChatUser(id);
                }
            }
        }else{
            Log.d("Swipe list is empty", swipes.toString());
        }
    }

    private void addChatUser(String id){
            Call<User> userIdCall = tindroomApiService.getUserById(id);
            userIdCall.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if(response.body() != null){
                        addChatUsers(response.body());
                    }
                }
                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(getContext(), getResources().getString(R.string.unexpected_error_occurred), Toast.LENGTH_SHORT).show();
                }
            });
            Log.d("chatuserss", chatUsers.toString());
    }
    private void addChatUsers(User chatUser){
        chatUsers.add(chatUser);
        addAdapter();
    }

    private void addAdapter(){
        ChatAdapter chatAdapter = new ChatAdapter(ChatFragment.this, chatUsers, listener);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(chatAdapter);
    }

    private void setOnClickListener() {
        listener = new ChatAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position, ArrayList<User> chatUsers) {
               navigateToMessageFragment(v, chatUsers.get(position));
            }
        };
    }

    public void navigateToMessageFragment (View view, User chatUser) {
        NavDirections action = (NavDirections) ChatFragmentDirections.actionChatFragmentToMessageFragment(chatUser);
        Navigation.findNavController(view).navigate(action);
    }

}