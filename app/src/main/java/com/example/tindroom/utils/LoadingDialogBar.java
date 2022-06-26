package com.example.tindroom.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.tindroom.R;

public class LoadingDialogBar {
    private Activity activity;
    private AlertDialog dialog;

    public LoadingDialogBar(Activity myActivity){
        activity = myActivity;
    }

    public void startLoadingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog, null));
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();

        dialog.getWindow().setLayout(600,400);

    }

    public void dismissDialog(){
        dialog.dismiss();
    }
}
