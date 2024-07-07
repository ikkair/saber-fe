package com.desti.saber.LayoutHelper.GreetingDecoDashboard;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.desti.saber.R;
import com.desti.saber.utils.ImageSetterFromStream;

public class GreetingDecoDashboard {

    private final Activity activity;
    private final ViewGroup viewGroup;

    public GreetingDecoDashboard(Activity activity, ViewGroup viewGroup) {
        this.activity = activity;
        this.viewGroup = viewGroup;
    }

    private FrameLayout getDeco(){
        return activity.findViewById(R.id.wrapperDecorationDashboard);
    }

    public void show(){
        if(getDeco() == null){
            activity.getLayoutInflater().inflate(R.layout.greeting_deco_dashboard, viewGroup);
            ImageSetterFromStream isfs = new ImageSetterFromStream(activity);
            isfs.setAsImageDrawable("img_btn_3.png", R.id.imageTopDecoration);
            isfs.setAsImageBackground("dashboard_block_bg.png", R.id.wrapperDecorationDashboard);
        }
    }

    public void hide(){
        if(getDeco() != null){
            ((ViewGroup) getDeco().getRootView()).removeView(getDeco());
        }
    }
}
