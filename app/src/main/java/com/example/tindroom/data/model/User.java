package com.example.tindroom.data.model;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

@SuppressLint("ParcelCreator")
public class User implements Parcelable {
    @SerializedName("user_id")
    private int userId;

    @SerializedName("name")
    private String name;

    @SerializedName("date_of_birth")
    private String dateOfBirth;

    @SerializedName("description")
    private String description;

    @SerializedName("sex")
    private char sex;

    @SerializedName("faculty_id")
    private long idFaculty;

    @SerializedName("roommate_sex")
    private char roommateSex;

    @SerializedName("roommate_age_from")
    private int roommateAgeFrom;

    @SerializedName("roommate_age_to")
    private int roommateAgeTo;

    @SerializedName("has_apartment")
    private int hasApartment;

    @SerializedName("price_from")
    private double priceFrom;

    @SerializedName("price_to")
    private double priceTo;

    @SerializedName("neighborhood_id")
    private Long idNeighborhood;

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(final String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public char getSex() {
        return sex;
    }

    public void setSex(final char sex) {
        this.sex = sex;
    }

    public long getIdFaculty() {
        return idFaculty;
    }

    public void setIdFaculty(final long idFaculty) {
        this.idFaculty = idFaculty;
    }

    public char getRoommateSex() {
        return roommateSex;
    }

    public void setRoommateSex(final char roommateSex) {
        this.roommateSex = roommateSex;
    }

    public int getRoommateAgeFrom() {
        return roommateAgeFrom;
    }

    public void setRoommateAgeFrom(final int roommateAgeFrom) {
        this.roommateAgeFrom = roommateAgeFrom;
    }

    public int getRoommateAgeTo() {
        return roommateAgeTo;
    }

    public void setRoommateAgeTo(final int roommateAgeTo) {
        this.roommateAgeTo = roommateAgeTo;
    }

    public boolean isHasApartment() {
        return  hasApartment > 0 ;
    }

    public void setHasApartment(final boolean hasApartment) {
        this.hasApartment = hasApartment ? 1 : 0;
    }

    public double getPriceFrom() {
        return priceFrom;
    }

    public void setPriceFrom(final double priceFrom) {
        this.priceFrom = priceFrom;
    }

    public double getPriceTo() {
        return priceTo;
    }

    public void setPriceTo(final double priceTo) {
        this.priceTo = priceTo;
    }

    public Long getIdNeighborhood() {
        return idNeighborhood;
    }

    public void setIdNeighborhood(final Long idNeighborhood) {
        this.idNeighborhood = idNeighborhood;
    }

    @SerializedName("feedback")
    private Object feedback;

    public Object getFeedback() {
        return feedback;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", description='" + description + '\'' +
                ", sex=" + sex +
                ", idFaculty=" + idFaculty +
                ", roommateSex=" + roommateSex +
                ", roommateAgeFrom=" + roommateAgeFrom +
                ", roommateAgeTo=" + roommateAgeTo +
                ", hasApartment=" + (hasApartment > 0) +
                ", priceFrom=" + priceFrom +
                ", priceTo=" + priceTo +
                ", idNeighborhood=" + idNeighborhood +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel parcel, final int i) {

    }
}
