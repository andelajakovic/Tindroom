package com.example.tindroom.data.model;

import com.google.gson.annotations.SerializedName;

public class Faculty {

    @SerializedName("faculty_id")
    private Long facultyId;

    @SerializedName("name")
    private String name;

    @SerializedName("area")
    private String area;

    public Long getFacultyId() {
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
