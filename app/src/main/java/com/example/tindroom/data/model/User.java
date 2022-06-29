package com.example.tindroom.data.model;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.sql.Date;
import java.util.Calendar;

@SuppressLint("ParcelCreator")
public class User implements Parcelable {
    @SerializedName("user_id")
    private String userId;

    @SerializedName("is_registered")
    private int isRegistered;

    @SerializedName("name")
    private String name;

    @SerializedName("date_of_birth")
    private String dateOfBirth;

    @SerializedName("description")
    private String description;

    @SerializedName("gender")
    private char gender;

    @SerializedName("faculty_id")
    private long idFaculty;

    @SerializedName("roommate_gender")
    private char roommateGender;

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

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("notification_token")
    private String notificationToken;

    @SerializedName("review")
    private double review;

    @SerializedName("number_of_reviews")
    private int numberOfReviews;

    private String imageUri;

    private Faculty faculty;

    private Neighborhood neighborhood;

    private String token;

    private Date lastSeen;

    public double grade = 0.0;

    public String getUserId() {
        return userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

    public boolean isRegistered() {
        return  isRegistered > 0 ;
    }

    public void setRegistered(final boolean isRegistered) {
        this.isRegistered = isRegistered ? 1 : 0;
    }

    public void setToken(final String token){
        this.token = token;
    }

    public String getToken(){
        return token;
    }

    public void setLastSeen(Date lastSeen){
        this.lastSeen = lastSeen;
    }

    public Date getLastSeen(){
        return lastSeen;
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

    public char getGender() {
        return gender;
    }

    public void setGender(final char gender) {
        this.gender = gender;
    }

    public long getIdFaculty() {
        return idFaculty;
    }

    public void setIdFaculty(final long idFaculty) {
        this.idFaculty = idFaculty;
    }

    public char getRoommateGender() {
        return roommateGender;
    }

    public void setRoommateGender(final char roommateGender) {
        this.roommateGender = roommateGender;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(final String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(final String imageUri) {
        this.imageUri = imageUri;
    }

    public Faculty getFaculty() {
        return faculty;
    }

    public void setFaculty(final Faculty faculty) {
        this.faculty = faculty;
    }

    public Neighborhood getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(final Neighborhood neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getNotificationToken() {
        return notificationToken;
    }

    public void setNotificationToken(String notificationToken) {
        this.notificationToken = notificationToken;
    }

    public double getReview() {
        return review;
    }

    public void setReview(double review) {
        this.review = review;
    }

    public int getNumberOfReviews() {
        return numberOfReviews;
    }

    public void setNumberOfReviews(int numberOfReviews) {
        this.numberOfReviews = numberOfReviews;
    }

    @SerializedName("feedback")
    private Object feedback;

    public Object getFeedback() {
        return feedback;
    }

    public String getAge(){

        int month = Integer.parseInt(this.getDateOfBirth().substring(5,7));
        int day = Integer.parseInt(this.getDateOfBirth().substring(9,10));
        int year = Integer.parseInt(this.getDateOfBirth().substring(0,4));

        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", isRegistered=" + (isRegistered > 0) +
                ", name='" + name + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", description='" + description + '\'' +
                ", sex=" + gender +
                ", idFaculty=" + idFaculty +
                ", roommateSex=" + roommateGender +
                ", roommateAgeFrom=" + roommateAgeFrom +
                ", roommateAgeTo=" + roommateAgeTo +
                ", hasApartment=" + (hasApartment > 0) +
                ", priceFrom=" + priceFrom +
                ", priceTo=" + priceTo +
                ", idNeighborhood=" + idNeighborhood +
                ", imageUrl=" + imageUrl +
                ", grade=" + grade +
                ", token=" + notificationToken +
                ", feedback=" + feedback +
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
