package com.desti.saber.LayoutHelper.FailedNotif;

import android.app.Activity;
import android.widget.Toast;
import com.desti.saber.R;

public class FailServerConnectToast {

    private Activity activity;

    public FailServerConnectToast(Activity activity) {
        this.activity = activity;
    }

    public void show(){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(
                    activity,
                    R.string.failed_con_server,
                    Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    public void show(String message){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(
                    activity,
                    message,
                    Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    public void show(int message){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(
                    activity,
                    message,
                    Toast.LENGTH_LONG
                ).show();
            }
        });
    }
}
