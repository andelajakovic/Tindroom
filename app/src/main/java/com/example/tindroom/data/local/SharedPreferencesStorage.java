package com.example.tindroom.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.tindroom.data.model.User;
import com.google.gson.Gson;

public class SharedPreferencesStorage {

    static final String sessionUser = "Session";

    static SharedPreferences getSharedPreferences(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setSessionUser(Context context, User user){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();

        Gson gson = new Gson();
        String json = gson.toJson(user);

        editor.putString(sessionUser, json);
        editor.commit();
    }

    public static User getSessionUser(Context context){
        Gson gson = new Gson();
        String json = getSharedPreferences(context).getString(sessionUser, "");

        User user = gson.fromJson(json, User.class);

        return user;
    }
}
