package com.example.tindroom.ui.postlogin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tindroom.R;
import com.example.tindroom.data.local.SharedPreferencesStorage;
import com.example.tindroom.data.model.User;
import com.example.tindroom.network.RetrofitService;
import com.example.tindroom.network.TindroomApiService;

import java.util.Date;
import java.util.List;

public class SwipeFragment extends Fragment {

    View rootView;
    private TindroomApiService tindroomApiService;
    private User sessionUser;
    private List<User> swipeUsers;

    private ImageView profilePicture;
    private LinearLayout userDescriptionLayout, apartmentDescriptionLayout;
    private TextView roommatesName, roommatesFaculty, roommatesDescription, apartmentDescription;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_swipe, container, false);

        Retrofit retrofit = RetrofitService.getRetrofit();
        tindroomApiService = retrofit.create(TindroomApiService.class);
        sessionUser = SharedPreferencesStorage.getSessionUser(requireContext());

        initViews();
        initListeners();

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Call<List<User>> userCall = tindroomApiService.getUsers();
        userCall.enqueue(new Callback<List<User>>() {

            @Override
            public void onResponse(final Call<List<User>> call, final Response<List<User>> response) {
                swipeUsers = response.body();
                displayUser(swipeUsers);
            }

            @Override
            public void onFailure(final Call<List<User>> call, final Throwable t) {
            }
        });
    }

    private void initViews() {
        profilePicture = rootView.findViewById(R.id.profilePicture);
        roommatesName = rootView.findViewById(R.id.name);
        roommatesFaculty = rootView.findViewById(R.id.faculty);
        roommatesDescription = rootView.findViewById(R.id.description);
        apartmentDescription = rootView.findViewById(R.id.apartmentDescription);
        userDescriptionLayout = rootView.findViewById(R.id.userDescriptionLayout);
        apartmentDescriptionLayout = rootView.findViewById(R.id.apartmentDescriptionLayout);
    }

    private void initListeners() {
    }

    private void displayUser(List<User> swipeUsers){

        if(swipeUsers.isEmpty()){
            userDescriptionLayout.setVisibility(View.GONE);
            // TODO (Andrea: dodati layout kada vise nema korisnika za swipeanje)
        }
        else{
            User swipeUser = swipeUsers.get(0);
            //long ageInMillis = new Date().getTime() - new Date(swipeUser.getDateOfBirth()).getTime();
            //Date age = new Date(ageInMillis);

            roommatesName.setText(swipeUser.getName() + ", ");
            if(swipeUser.getDescription() == null || swipeUser.getDescription().equals("")){
                roommatesDescription.setVisibility(View.GONE);
            } else{
                roommatesDescription.setText(swipeUser.getDescription());
            }

            if (swipeUser.isHasApartment()) {
                apartmentDescriptionLayout.setVisibility(View.VISIBLE);
                apartmentDescription.setText(getResources().getString(R.string.swipe_fragment_apartment_description, swipeUser.getIdNeighborhood().toString(), String.valueOf(swipeUser.getPriceFrom())));
            } else {
                apartmentDescription.setVisibility(View.GONE);
            }

        }

    }
}