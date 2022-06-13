package com.example.tindroom.data.model;

import com.google.gson.annotations.SerializedName;

public class Neighborhood {
    @SerializedName("neighborhood_id")
    private Long neighborhoodId;

    @SerializedName("name")
    private String name;

    @SerializedName("area")
    private String area;

    public Long getNeighborhoodId() {
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
