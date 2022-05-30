package com.example.tindroom.data.model;

import com.google.gson.annotations.SerializedName;

public class Neighborhood {
    @SerializedName("neighborhood_id")
    private long neighborhoodId;

    @SerializedName("name")
    private String name;

    @SerializedName("area")
    private String area;

    public float getNeighborhoodId() {
        return neighborhoodId;
    }

    public String getName() {
        return name;
    }

    public String getArea() {
        return area;
    }

    @Override
    public String toString() {
        return "Neighborhood{"
                + "neighborhoodId = " + neighborhoodId
                + ", name = " + name
                + ", area = " + area
                + "}";
    }
}
