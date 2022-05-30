package com.example.tindroom.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class User {
    @SerializedName("id_user")
    private int idUser;

    @SerializedName("name")
    private String name;

    @SerializedName("date_of_birth")
    private String dateOfBirth;

    @SerializedName("description")
    private String description;

    @SerializedName("sex")
    private char sex;

    @SerializedName("id_faculty")
    private long idFaculty;

    @SerializedName("roommate_sex")
    private char roommateSex;

    @SerializedName("roommate_age_from")
    private int roommateAgeFrom;

    @SerializedName("roommate_age_to")
    private int roommateAgeTo;

    @SerializedName("has_apartment")
    private boolean hasApartment;

    @SerializedName("price_from")
    private double priceFrom;

    @SerializedName("price_to")
    private double priceTo;

    @SerializedName("id_neighborhood")
    private Long idNeighborhood;

    public User(final String name, final String dateOfBirth, final String description, final char sex, final long idFaculty, final char roommateSex, final int roommateAgeFrom,
                final int roommateAgeTo, final boolean hasApartment,
                final double priceFrom, final double priceTo, final Long idNeighborhood) {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.description = description;
        this.sex = sex;
        this.idFaculty = idFaculty;
        this.roommateSex = roommateSex;
        this.roommateAgeFrom = roommateAgeFrom;
        this.roommateAgeTo = roommateAgeTo;
        this.hasApartment = hasApartment;
        this.priceFrom = priceFrom;
        this.priceTo = priceTo;
        this.idNeighborhood = idNeighborhood;
    }

    public int getIdUser() {
        return idUser;
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
        return hasApartment;
    }

    public void setHasApartment(final boolean hasApartment) {
        this.hasApartment = hasApartment;
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
                "idUser=" + idUser +
                ", name='" + name + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", description='" + description + '\'' +
                ", sex=" + sex +
                ", idFaculty=" + idFaculty +
                ", roommateSex=" + roommateSex +
                ", roommateAgeFrom=" + roommateAgeFrom +
                ", roommateAgeTo=" + roommateAgeTo +
                ", hasApartment=" + hasApartment +
                ", priceFrom=" + priceFrom +
                ", priceTo=" + priceTo +
                ", idNeighborhood=" + idNeighborhood +
                '}';
    }
}
