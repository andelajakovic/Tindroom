package com.example.tindroom.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.WindowManager;

import com.example.tindroom.R;

public class MatchDialog {
    private Activity activity;
    private AlertDialog dialog;

    public MatchDialog(Activity myActivity){
        activity = myActivity;
    }

    public void startMatchDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.match_dialog, null));
        builder.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        dialog = builder.create();
        dialog.show();
        dialog.getWindow().setAttributes(lp);

    }

    public void dismissMatchDialog(){
        dialog.dismiss();
    }
}
