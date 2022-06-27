package com.example.tindroom.ui.postlogin;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tindroom.R;
import com.example.tindroom.data.local.SharedPreferencesStorage;
import com.example.tindroom.data.model.Faculty;
import com.example.tindroom.data.model.Neighborhood;
import com.example.tindroom.data.model.Swipe;
import com.example.tindroom.data.model.User;
import com.example.tindroom.network.RetrofitService;
import com.example.tindroom.network.TindroomApiService;
import com.example.tindroom.utils.LoadingDialogBar;
import com.example.tindroom.utils.OnSwipeTouchListener;

import java.util.ArrayList;
import java.util.List;

public class SwipeFragment extends Fragment {

    View rootView;
    private TindroomApiService tindroomApiService;
    private User sessionUser;
    private List<User> swipeUsers, sortedUsers;
    private List<Faculty> faculties;
    private List<Neighborhood> neighborhoods;
    private List<Swipe> swipes;

    private ProgressDialog progressDialog;

    private ImageView profilePicture;
    private LinearLayout userDescriptionLayout, apartmentDescriptionLayout;
    private TextView roommatesName, roommatesFaculty, roommatesDescription, apartmentDescription;
    private RelativeLayout layout;
    LoadingDialogBar loadingDialogBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_swipe, container, false);

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
    }

    private void initViews() {
        profilePicture = rootView.findViewById(R.id.profilePicture);
        roommatesName = rootView.findViewById(R.id.name);
        roommatesFaculty = rootView.findViewById(R.id.faculty);
        roommatesDescription = rootView.findViewById(R.id.description);
        apartmentDescription = rootView.findViewById(R.id.apartmentDescription);
        userDescriptionLayout = rootView.findViewById(R.id.userDescriptionLayout);
        apartmentDescriptionLayout = rootView.findViewById(R.id.apartmentDescriptionLayout);
        layout = rootView.findViewById(R.id.layout);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initListeners() {

        layout.setOnTouchListener(new OnSwipeTouchListener(requireContext()) {
            // TODO (Andrea: dodati animacije)
            public void onSwipeRight() {
                swipe(true);
            }

            public void onSwipeLeft() {
                swipe(false);
            }
        });
    }

    private void swipe (boolean swipeValue) {
        User swipedUser = sortedUsers.get(0);
        boolean swipeFound = false;
        Call<Swipe> swipeCall = null;

        for (Swipe swipe : swipes) {
            Log.d("!!!!!", swipe.toString());
            if (swipe.getUserId1().equals(swipedUser.getUserId())) {
                swipe.setSwipe_2(swipeValue);
                swipeFound = true;
                swipeCall = tindroomApiService.updateSwipe(swipe);
                break;
            }
            if(swipe.getUserId2().equals(swipedUser.getUserId())) {
                Log.d("!!!!!", swipe.toString());
                swipe.setSwipe_1(swipeValue);
                swipeFound = true;
                swipeCall = tindroomApiService.updateSwipe(swipe);
                break;
            }
        }

        if(!swipeFound) {
            Swipe swipe = new Swipe();
            if(sessionUser.getUserId().compareTo(swipedUser.getUserId()) < 0){
                swipe.setUserId1(sessionUser.getUserId());
                swipe.setUserId2(swipedUser.getUserId());
                swipe.setSwipe_1(swipeValue);
            } else {
                swipe.setUserId1(swipedUser.getUserId());
                swipe.setUserId2(sessionUser.getUserId());
                swipe.setSwipe_2(swipeValue);
            }
            swipes.add(swipe);
            swipeCall = tindroomApiService.insertSwipe(swipe);
        }

        if (swipeCall != null) {
            loadingDialogBar.startLoadingDialog();
            swipeCall.enqueue(new Callback<Swipe>() {

                @Override
                public void onResponse(final Call<Swipe> call, final Response<Swipe> response) {
                    Log.d("!!!!!!!", String.valueOf(response));
                    progressDialog.dismiss();
                    sortedUsers.remove(0);
                    nextUser(sortedUsers);
                }

                @Override
                public void onFailure(final Call<Swipe> call, final Throwable t) {
                    loadingDialogBar.dismissDialog();
                    Toast.makeText(getContext(), getResources().getString(R.string.unexpected_error_occurred), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void nextUser(List<User> swipeUsers) {
        if (swipeUsers.isEmpty()) {
            layout.setVisibility(View.GONE);
            // TODO (Andrea: dodati layout kada vise nema korisnika za swipeanje)
        } else {
            displayUser(swipeUsers.get(0));
        }
    }

    private void displayUser(User swipeUser) {

        //long ageInMillis = new Date().getTime() - new Date(swipeUser.getDateOfBirth()).getTime();
        //Date age = new Date(ageInMillis);

        Glide.with(rootView)
             .asBitmap()
             .load(swipeUser.getImageUrl())
             .error(getResources().getDrawable(R.drawable.avatar_placeholder))
             .into(profilePicture);
        roommatesName.setText(swipeUser.getName());

        roommatesFaculty.setText(swipeUser.getFaculty().getName());

        if (swipeUser.getDescription() == null || swipeUser.getDescription().equals("")) {
            roommatesDescription.setVisibility(View.GONE);
        } else {
            roommatesDescription.setText(swipeUser.getDescription());
        }

        if (swipeUser.isHasApartment()) {
            apartmentDescriptionLayout.setVisibility(View.VISIBLE);
            apartmentDescription.setText(getResources().getString(R.string.swipe_fragment_apartment_description,
                                                                  swipeUser.getNeighborhood().getName(),
                                                                  String.valueOf(swipeUser.getPriceFrom())));
        } else {
            apartmentDescription.setVisibility(View.GONE);
        }
    }

    private List<User> sortSwipeUsers(List<User> swipeUsers) {
        List<User> sortedUsers = new ArrayList<>();

        for (User swipeUser: swipeUsers) {
            if(!swipeUser.isRegistered() || swipeUser.getUserId().equals(sessionUser.getUserId())) {
                continue;
            }

            for (Faculty faculty : faculties) {
                if (faculty.getFacultyId() == swipeUser.getIdFaculty()) {
                    swipeUser.setFaculty(faculty);
                    break;
                }
            }

            if (swipeUser.isHasApartment()) {
                for (Neighborhood neighborhood : neighborhoods) {
                    if (neighborhood.getNeighborhoodId().equals(swipeUser.getIdNeighborhood())) {
                        swipeUser.setNeighborhood(neighborhood);
                        break;
                    }
                }
            }

            if (!checkIfUserIsAlreadySwiped(swipeUser)) {
                sortedUsers.add(swipeUser);
            }

        }

        return sortedUsers;
    }

    private boolean checkIfUserIsAlreadySwiped (User user) {
        for (Swipe swipe: swipes) {
            if (swipe.getUserId1().equals(user.getUserId()) && swipe.isSwipe_2() != null) {
                Log.d("!!!!!!", user.toString());
                return true;
            } else if (swipe.getUserId2().equals(user.getUserId()) && swipe.isSwipe_1() != null) {
                Log.d("!!!!!!", user.toString());
                return true;
            }
        }
        return false;
    }

    private void getSwipes() {
        swipes = new ArrayList<>();
        progressDialog = ProgressDialog.show(getContext(), "Loading...", "Please wait", true);

        Call<List<Swipe>> usersSwipesCall = tindroomApiService.getUsersSwipes(sessionUser.getUserId());
        usersSwipesCall.enqueue(new Callback<List<Swipe>>() {

            @Override
            public void onResponse(final Call<List<Swipe>> call, final Response<List<Swipe>> response) {
                if (response.body() != null)
                    swipes.addAll(response.body());
                getFaculties();
            }

            @Override
            public void onFailure(final Call<List<Swipe>> call, final Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), getResources().getString(R.string.unexpected_error_occurred), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getFaculties() {
        faculties = new ArrayList<>();

        Call<List<Faculty>> facultiesCall = tindroomApiService.getFaculties();

        facultiesCall.enqueue(new Callback<List<Faculty>>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(@NonNull final Call<List<Faculty>> call, @NonNull final Response<List<Faculty>> response) {
                if (response.body() != null)
                    faculties.addAll(response.body());
                getNeighborhoods();
            }

            @Override
            public void onFailure(@NonNull final Call<List<Faculty>> call, @NonNull final Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), getResources().getString(R.string.unexpected_error_occurred), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getNeighborhoods() {
        neighborhoods = new ArrayList<>();

        Call<List<Neighborhood>> neighborhoodsCall = tindroomApiService.getNeighborhoods();

        neighborhoodsCall.enqueue(new Callback<List<Neighborhood>>() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(@NonNull final Call<List<Neighborhood>> call, @NonNull final Response<List<Neighborhood>> response) {
                if (response.body() != null)
                    neighborhoods.addAll(response.body());
                getSwipeUsers();
            }

            @Override
            public void onFailure(@NonNull final Call<List<Neighborhood>> call, @NonNull final Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), getResources().getString(R.string.unexpected_error_occurred), Toast.LENGTH_SHORT).show();            }
        });
    }

    private void getSwipeUsers() {
        swipeUsers = new ArrayList<>();

        Call<List<User>> userCall = tindroomApiService.getUsers();
        userCall.enqueue(new Callback<List<User>>() {

            @Override
            public void onResponse(final Call<List<User>> call, final Response<List<User>> response) {
                progressDialog.dismiss();
                if (response.body() != null)
                    swipeUsers = response.body();
                sortedUsers = sortSwipeUsers(swipeUsers);
                nextUser(sortedUsers);
            }

            @Override
            public void onFailure(final Call<List<User>> call, final Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), getResources().getString(R.string.unexpected_error_occurred), Toast.LENGTH_SHORT).show();
            }
        });
    }
}