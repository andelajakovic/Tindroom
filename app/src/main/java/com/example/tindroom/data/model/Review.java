package com.example.tindroom.data.model;

import com.google.gson.annotations.SerializedName;

public class Review {
    @SerializedName("reviewer_id")
    String reviewerId;

    @SerializedName("user_id")
    String userId;

    @SerializedName("review")
    Integer review;

    @SerializedName("feedback")
    private Object feedback;

    public Review(final String reviewerId, final String userId, final Integer review) {
        this.reviewerId = reviewerId;
        this.userId = userId;
        this.review = review;
    }

    public String getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(final String reviewerId) {
        this.reviewerId = reviewerId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

    public Integer getReview() {
        return review;
    }

    public void setReview(final Integer review) {
        this.review = review;
    }

    public Object getFeedback() {
        return feedback;
    }

    @Override
    public String toString() {
        return "Review{" +
                "reviewerId='" + reviewerId + '\'' +
                ", userId='" + userId + '\'' +
                ", review=" + review +
                '}';
    }
}
