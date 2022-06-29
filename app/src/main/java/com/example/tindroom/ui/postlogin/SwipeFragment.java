package com.example.tindroom.ui.postlogin;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.example.tindroom.utils.MatchDialog;
import com.example.tindroom.utils.OnSwipeTouchListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class SwipeFragment extends Fragment {

    View rootView;
    private TindroomApiService tindroomApiService;
    private User sessionUser;
    private List<User> swipeUsers, sortedUsers;
    private List<Faculty> faculties;
    private List<Neighborhood> neighborhoods;
    private List<Swipe> swipes;

    private ImageView profilePicture;
    private LinearLayout userDescriptionLayout, apartmentDescriptionLayout;
    private TextView roommatesName, roommatesFaculty, roommatesDescription, apartmentDescription;
    private Button settings;
    private RelativeLayout layout;
    private ConstraintLayout layout2;
    private CardView card;
    LoadingDialogBar loadingDialogBar;
    MatchDialog matchDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_swipe, container, false);

        Retrofit retrofit = RetrofitService.getRetrofit();
        tindroomApiService = retrofit.create(TindroomApiService.class);
        sessionUser = SharedPreferencesStorage.getSessionUser(requireContext());
        loadingDialogBar = new LoadingDialogBar(getActivity());
        matchDialog = new MatchDialog(getActivity());

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
        settings = rootView.findViewById(R.id.settings);
        layout = rootView.findViewById(R.id.layout);
        layout2 = rootView.findViewById(R.id.layout2);

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

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                navigateToSettingsFragment(view);
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
            // obavijest
            matchDialog.startMatchDialog();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                        matchDialog.dismissMatchDialog();
                }
            }, 1500);
        }

        if (swipeCall != null) {
            loadingDialogBar.startLoadingDialog();
            swipeCall.enqueue(new Callback<Swipe>() {

                @Override
                public void onResponse(final Call<Swipe> call, final Response<Swipe> response) {
                    Log.d("!!!!!!!", String.valueOf(response));
                    loadingDialogBar.dismissDialog();
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
            layout2.setVisibility(View.VISIBLE);
        } else {
            displayUser(swipeUsers.get(0));
        }
    }

    private void displayUser(User swipeUser) {
        Glide.with(rootView)
             .asBitmap()
             .load(swipeUser.getImageUrl())
             .error(getResources().getDrawable(R.drawable.avatar_placeholder))
             .into(profilePicture);

        roommatesName.setText(String.format("%s, %s", swipeUser.getName(), swipeUser.getAge()));

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
                                                                  String.valueOf((int)swipeUser.getPriceFrom())));
        } else {
            apartmentDescriptionLayout.setVisibility(View.GONE);
        }
    }

    @SuppressLint("NewApi")
    private List<User> sortSwipeUsers(List<User> swipeUsers) {
        List<User> sortedUsers = new ArrayList<>();

        for (User swipeUser: swipeUsers) {

            if(!swipeUser.isRegistered() || swipeUser.getUserId().equals(sessionUser.getUserId()) || checkIfUserIsAlreadySwiped(swipeUser)) {
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

            int divider = 1;

            if (Integer.parseInt(sessionUser.getAge()) >= swipeUser.getRoommateAgeFrom() && Integer.parseInt(sessionUser.getAge()) <= swipeUser.getRoommateAgeTo()) {
                swipeUser.grade += 10;
            } else if (Integer.parseInt(sessionUser.getAge()) >= (swipeUser.getRoommateAgeFrom() - 1) && Integer.parseInt(sessionUser.getAge()) <= (swipeUser.getRoommateAgeTo() + 1)) {
                swipeUser.grade += 8;
            } else if (Integer.parseInt(sessionUser.getAge()) >= (swipeUser.getRoommateAgeFrom() - 2) && Integer.parseInt(sessionUser.getAge()) <= (swipeUser.getRoommateAgeTo() + 2)) {
                swipeUser.grade += 6;
            } else if (Integer.parseInt(sessionUser.getAge()) >= (swipeUser.getRoommateAgeFrom() - 3) && Integer.parseInt(sessionUser.getAge()) <= (swipeUser.getRoommateAgeTo() + 3)) {
                swipeUser.grade += 4;
            } else if (Integer.parseInt(sessionUser.getAge()) >= (swipeUser.getRoommateAgeFrom() - 4) && Integer.parseInt(sessionUser.getAge()) <= (swipeUser.getRoommateAgeTo() + 4)) {
                swipeUser.grade += 2;
            }

            if (Integer.parseInt(swipeUser.getAge()) >= sessionUser.getRoommateAgeFrom() && Integer.parseInt(swipeUser.getAge()) <= sessionUser.getRoommateAgeTo()) {
                swipeUser.grade += 10;
            } else if (Integer.parseInt(swipeUser.getAge()) >= (sessionUser.getRoommateAgeFrom() - 1) && Integer.parseInt(swipeUser.getAge()) <= (sessionUser.getRoommateAgeTo() + 1)) {
                swipeUser.grade += 8;
            } else if (Integer.parseInt(swipeUser.getAge()) >= (sessionUser.getRoommateAgeFrom() - 2) && Integer.parseInt(swipeUser.getAge()) <= (sessionUser.getRoommateAgeTo() + 2)) {
                swipeUser.grade += 6;
            } else if (Integer.parseInt(swipeUser.getAge()) >= (sessionUser.getRoommateAgeFrom() - 3) && Integer.parseInt(swipeUser.getAge()) <= (sessionUser.getRoommateAgeTo() + 3)) {
                swipeUser.grade += 4;
            } else if (Integer.parseInt(swipeUser.getAge()) >= (sessionUser.getRoommateAgeFrom() - 4) && Integer.parseInt(swipeUser.getAge()) <= (sessionUser.getRoommateAgeTo() + 4)) {
                swipeUser.grade += 2;
            }

            if (sessionUser.getRoommateGender() == 'A') {
                swipeUser.grade += 10;
            } else if (sessionUser.getRoommateGender() == swipeUser.getGender()) {
                swipeUser.grade += 10;
            } else {
                divider++;
            }

            if (swipeUser.getRoommateGender() == 'A') {
                swipeUser.grade += 10;
            } else if (swipeUser.getRoommateGender() == sessionUser.getGender()) {
                swipeUser.grade += 10;
            } else {
                divider++;
            }

            if (!sessionUser.isHasApartment() && !swipeUser.isHasApartment()) {
                if (Math.abs(sessionUser.getPriceFrom() - swipeUser.getPriceFrom()) < 100) {
                    swipeUser.grade += 10;
                } else if (Math.abs(sessionUser.getPriceFrom() - swipeUser.getPriceFrom()) < 500) {
                    swipeUser.grade += 7;
                } else if (Math.abs(sessionUser.getPriceFrom() - swipeUser.getPriceFrom()) < 1000) {
                    swipeUser.grade += 3;
                }

                if (Math.abs(sessionUser.getPriceTo() - swipeUser.getPriceTo()) < 100) {
                    swipeUser.grade += 10;
                } else if (Math.abs(sessionUser.getPriceTo() - swipeUser.getPriceTo()) < 500) {
                    swipeUser.grade += 7;
                } else if (Math.abs(sessionUser.getPriceTo() - swipeUser.getPriceTo()) < 1000) {
                    swipeUser.grade += 3;
                }

            } else if (!sessionUser.isHasApartment() && swipeUser.isHasApartment()) {
                if (sessionUser.getPriceFrom() <= swipeUser.getPriceFrom() && sessionUser.getPriceTo() >= swipeUser.getPriceFrom()) {
                    swipeUser.grade += 10;
                } else if ((sessionUser.getPriceFrom() - 500) <= swipeUser.getPriceFrom() && (sessionUser.getPriceTo() + 500) >= swipeUser.getPriceFrom()) {
                    swipeUser.grade += 7;
                } else if ((sessionUser.getPriceFrom() - 1000) <= swipeUser.getPriceFrom() && (sessionUser.getPriceTo() + 1000) >= swipeUser.getPriceFrom()) {
                    swipeUser.grade += 3;
                }

                if(sessionUser.getFaculty().getArea().equals(swipeUser.getNeighborhood().getArea())) {
                    swipeUser.grade += 20;
                }

            } else if (sessionUser.isHasApartment() && !swipeUser.isHasApartment()) {
                if (swipeUser.getPriceFrom() <= sessionUser.getPriceFrom() && swipeUser.getPriceTo() >= sessionUser.getPriceFrom()) {
                    swipeUser.grade += 10;
                } else if ((swipeUser.getPriceFrom() - 500) <= sessionUser.getPriceFrom() && (swipeUser.getPriceTo() + 500) >= sessionUser.getPriceFrom()) {
                    swipeUser.grade += 7;
                } else if ((swipeUser.getPriceFrom() - 1000) <= sessionUser.getPriceFrom() && (swipeUser.getPriceTo() + 1000) >= sessionUser.getPriceFrom()) {
                    swipeUser.grade += 3;
                }

                if(swipeUser.getFaculty().getArea().equals(sessionUser.getNeighborhood().getArea())) {
                    swipeUser.grade += 20;
                }

            } else {
                continue;
            }

            if (sessionUser.getFaculty().getFacultyId().equals(swipeUser.getFaculty().getFacultyId())) {
                swipeUser.grade += 5;
            }

            swipeUser.grade /= divider;
            sortedUsers.add(swipeUser);


        }
        sortedUsers.sort(new Comparator<User>() {
            @Override
            public int compare(final User user, final User t1) {
                return Double.compare(t1.grade, user.grade);
            }
        });

        Log.d("!!!!!!!!!!!!", sortedUsers.toString());

        return sortedUsers;
    }

    private boolean checkIfUserIsAlreadySwiped (User user) {
        for (Swipe swipe: swipes) {
            if (swipe.getUserId1().equals(user.getUserId()) && swipe.isSwipe_2() != null) {
                return true;
            } else if (swipe.getUserId2().equals(user.getUserId()) && swipe.isSwipe_1() != null) {
                return true;
            }
        }
        return false;
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
                getFaculties();
            }

            @Override
            public void onFailure(final Call<List<Swipe>> call, final Throwable t) {
                loadingDialogBar.dismissDialog();
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
                loadingDialogBar.dismissDialog();
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
                loadingDialogBar.dismissDialog();
                Toast.makeText(getContext(), getResources().getString(R.string.unexpected_error_occurred), Toast.LENGTH_SHORT).show();            }
        });
    }

    private void getSwipeUsers() {
        swipeUsers = new ArrayList<>();

        Call<List<User>> userCall = tindroomApiService.getUsers();
        userCall.enqueue(new Callback<List<User>>() {

            @Override
            public void onResponse(final Call<List<User>> call, final Response<List<User>> response) {
                loadingDialogBar.dismissDialog();
                if (response.body() != null)
                    swipeUsers = response.body();
                sortedUsers = sortSwipeUsers(swipeUsers);
                nextUser(sortedUsers);
            }

            @Override
            public void onFailure(final Call<List<User>> call, final Throwable t) {
                loadingDialogBar.dismissDialog();
                Toast.makeText(getContext(), getResources().getString(R.string.unexpected_error_occurred), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void navigateToSettingsFragment (View view) {
        NavDirections action = SwipeFragmentDirections.actionSwipeFragmentToSettingsFragment();
        Navigation.findNavController(view).navigate(action);
    }
}