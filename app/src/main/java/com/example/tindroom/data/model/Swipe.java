package com.example.tindroom.data.model;

import com.google.gson.annotations.SerializedName;

public class Swipe {

    @SerializedName("user_id_1")
    private String userId1;

    @SerializedName("user_id_2")
    private String userId2;

    @SerializedName("swipe_1")
    private Integer swipe_1 = null;

    @SerializedName("swipe_2")
    private Integer swipe_2 = null;

    @SerializedName("feedback")
    private Object feedback;

    public Object getFeedback() {
        return feedback;
    }

    public String getUserId1() {
        return userId1;
    }

    public void setUserId1(final String userId1) {
        this.userId1 = userId1;
    }

    public String getUserId2() {
        return userId2;
    }

    public void setUserId2(final String userId2) {
        this.userId2 = userId2;
    }

    public Boolean isSwipe_1() {
        if (swipe_1 == null) return null;
        else return swipe_1 == 1;
    }

    public void setSwipe_1(final boolean swipe_1) {
        this.swipe_1 = swipe_1 ? 1 : 0;
    }

    public Boolean isSwipe_2() {
        if (swipe_2 == null) return null;
        else return swipe_2 == 1;
    }

    public void setSwipe_2(final boolean swipe_2) {
        this.swipe_2 = swipe_2 ? 1 : 0;
    }

    @Override
    public String toString() {
        return "Swipe{" +
                "userId1='" + userId1 + '\'' +
                ", userId2='" + userId2 + '\'' +
                ", swipe_1=" + swipe_1 +
                ", swipe_2=" + swipe_2 +
                '}';
    }
}
