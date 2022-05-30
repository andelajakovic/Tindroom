package com.example.tindroom.data.model;

import com.google.gson.annotations.SerializedName;

public class Faculty {

    @SerializedName("id_faculty")
    private long idFaculty;

    @SerializedName("name")
    private String name;

    @SerializedName("area")
    private String area;

    public float getId_faculty() {
        return idFaculty;
    }

    public String getName() {
        return name;
    }

    public String getArea() {
        return area;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setArea(String area) {
        this.area = area;
    }

    @Override
    public String toString() {
        return "Faculty{"
                + "id_faculty = " + idFaculty
                + "name = " + name
                + "area = " + area
                + "}";
    }

}
