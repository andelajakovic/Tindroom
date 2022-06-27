package com.example.tindroom.ui.postlogin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tindroom.R;
import com.example.tindroom.data.local.SharedPreferencesStorage;
import com.example.tindroom.data.model.Swipe;
import com.example.tindroom.data.model.User;
import com.example.tindroom.network.RetrofitService;
import com.example.tindroom.network.TindroomApiService;
import com.example.tindroom.utils.LoadingDialogBar;
import com.example.tindroom.utils.OnSwipeTouchListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
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
    private List<Swipe> swipes;
    private ArrayList<User> chatUsers;
    LoadingDialogBar loadingDialogBar;
    private RecyclerView recyclerView;
    private ChatAdapter.RecyclerViewClickListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        Retrofit retrofit = RetrofitService.getRetrofit();
        tindroomApiService = retrofit.create(TindroomApiService.class);
        sessionUser = SharedPreferencesStorage.getSessionUser(requireContext());
        loadingDialogBar = new LoadingDialogBar(getActivity());

        initViews();
        initListeners();

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getSwipes();

        if(!swipes.isEmpty()){
            for(Swipe swipe : swipes){
                if(swipe != null && swipe.isSwipe_1() && swipe.isSwipe_2()){
                    User user = new User();
                    user.setUserId(swipe.getUserId2());
                    chatUsers.add(user);
                }
            }
        }

        setOnClickListener();

        ChatAdapter chatAdapter = new ChatAdapter(getActivity(), chatUsers, listener);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(chatAdapter);

    }

    private void getSwipes() {
        swipes = new ArrayList<>();
        loadingDialogBar.startLoadingDialog();

        Call<List<Swipe>> usersSwipesCall = tindroomApiService.getUsersSwipes(sessionUser.getUserId());
        usersSwipesCall.enqueue(new Callback<List<Swipe>>() {

            @Override
            public void onResponse(final Call<List<Swipe>> call, final Response<List<Swipe>> response) {
                if (response.body() != null)
                    swipes.addAll(response.body());
            }

            @Override
            public void onFailure(final Call<List<Swipe>> call, final Throwable t) {
                loadingDialogBar.dismissDialog();
                Toast.makeText(getContext(), getResources().getString(R.string.unexpected_error_occurred), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setOnClickListener() {
        listener = new ChatAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position, ArrayList<User> chatUsers) {

                Fragment selectedFragment = null;
                selectedFragment = new MessageFragment().newInstance(chatUsers.get(position));
                navigateToMessageFragment(rootView);
            }
        };
    }

    private void initViews() {
        recyclerView = rootView.findViewById(R.id.recyclerView);
    }

    private void initListeners() {

    }

    public void navigateToMessageFragment (View view) {
        NavDirections action = ChatFragmentDirections.actionChatFragmentToMessageFragment();
        Navigation.findNavController(view).navigate(action);
    }

}