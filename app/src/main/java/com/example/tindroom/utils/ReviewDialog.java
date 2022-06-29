package com.example.tindroom.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.tindroom.R;
import com.example.tindroom.data.model.Review;
import com.example.tindroom.data.model.User;
import com.example.tindroom.network.RetrofitService;
import com.example.tindroom.network.TindroomApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ReviewDialog {

    private Activity activity;
    private AlertDialog dialog;
    private View rootView;
    private TextView ok, cancel;
    private RatingBar ratingBar;
    private User reviewer, user;
    private Integer rating = null;
    private ProgressBar progressBar;

    private TindroomApiService tindroomApiService;

    public ReviewDialog(Activity myActivity, User reviewer, User user) {
        activity = myActivity;
        this.reviewer = reviewer;
        this.user = user;
    }

    public void startReviewDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        rootView = inflater.inflate(R.layout.review_popup_dialog, null);
        builder.setView(rootView);
        builder.setCancelable(false);

        ok = rootView.findViewById(R.id.ok);
        cancel = rootView.findViewById(R.id.cancel);
        ratingBar = rootView.findViewById(R.id.rating);
        progressBar = rootView.findViewById(R.id.progressBar);

        initListeners();

        Retrofit retrofit = RetrofitService.getRetrofit();
        tindroomApiService = retrofit.create(TindroomApiService.class);

        dialog = builder.create();
        dialog.show();

    }

    @SuppressLint("ClickableViewAccessibility")
    private void initListeners() {
        ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View view) {
                insertReview();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View view) {
                insertNullReview();
            }
        });

        ratingBar.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(final View view, final MotionEvent motionEvent) {
                float x = motionEvent.getX();
                int width = ratingBar.getWidth();

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        int percentageX = (int) ((x * 100) / width);
                        if (percentageX <= 20) {
                            ratingBar.setRating(1f);
                            rating = 1;
                        } else if (percentageX <= 40) {
                            ratingBar.setRating(2f);
                            rating = 2;
                        } else if (percentageX <= 60) {
                            ratingBar.setRating(3f);
                            rating = 3;
                        } else if (percentageX <= 80) {
                            ratingBar.setRating(4f);
                            rating = 4;
                        } else {
                            ratingBar.setRating(5f);
                            rating = 5;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return true;
            }
        });
    }

    private void insertNullReview() {
        progressBar.setVisibility(View.VISIBLE);
        cancel.setVisibility(View.GONE);
        ok.setVisibility(View.GONE);

        Call<Review> reviewCall = tindroomApiService.insertReview(new Review(reviewer.getUserId(), user.getUserId(), null));
        reviewCall.enqueue(new Callback<Review>() {

            @Override
            public void onResponse(final Call<Review> call, final Response<Review> response) {
                dismissReviewDialog();

            }

            @Override
            public void onFailure(final Call<Review> call, final Throwable t) {
                dismissReviewDialog();

            }
        });
    }

    private void insertReview() {
        progressBar.setVisibility(View.VISIBLE);
        cancel.setVisibility(View.GONE);
        ok.setVisibility(View.GONE);
        Call<Review> reviewCall = tindroomApiService.insertReview(new Review(reviewer.getUserId(), user.getUserId(), rating));
        reviewCall.enqueue(new Callback<Review>() {

            @Override
            public void onResponse(final Call<Review> call, final Response<Review> response) {
                if (rating != null) {
                    updateUser();
                } else {
                    dismissReviewDialog();
                }
            }

            @Override
            public void onFailure(final Call<Review> call, final Throwable t) {
                dismissReviewDialog();

            }
        });
    }

    private void updateUser() {
        user.setReview(((user.getReview() * user.getNumberOfReviews()) + rating) / (user.getNumberOfReviews() + 1));
        user.setNumberOfReviews(user.getNumberOfReviews() + 1);
        user.setDateOfBirth(user.getDateOfBirth().substring(0, 10));

        Call<User> userCall = tindroomApiService.updateUserById(user.getUserId(), user);
        userCall.enqueue(new Callback<User>() {

            @Override
            public void onResponse(final Call<User> call, final Response<User> response) {
                Log.d("SUCCESS", String.valueOf(response.body()));
                dismissReviewDialog();

            }

            @Override
            public void onFailure(final Call<User> call, final Throwable t) {
                Log.d("FAILURE", t.toString());
                dismissReviewDialog();

            }
        });
    }

    public void dismissReviewDialog() {
        dialog.dismiss();
    }
}
