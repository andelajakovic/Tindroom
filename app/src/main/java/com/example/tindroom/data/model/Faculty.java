package com.example.tindroom.data.model;

import com.google.gson.annotations.SerializedName;

public class Faculty {

    @SerializedName("faculty_id")
    private long facultyId;

    @SerializedName("name")
    private String name;

    @SerializedName("area")
    private String area;

    public float getFacultyId() {
        return facultyId;
    }

    public String getName() {
        return name;
    }

    public String getArea() {
        return area;
    }

    @Override
    public String toString() {
        return "Faculty{"
                + "facultyId = " + facultyId
                + ", name = " + name
                + ", area = " + area
                + "}";
    }

}
