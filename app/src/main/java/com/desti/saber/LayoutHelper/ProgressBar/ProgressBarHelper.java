package com.desti.saber.LayoutHelper.ProgressBar;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import com.desti.saber.R;

public class ProgressBarHelper {
    public static void onProgress(Activity activity, View view, boolean mode) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ViewGroup parent = (ViewGroup) view.getParent();
                Context newConText = parent.getRootView().getContext();
                int layoutId = R.layout.loading_progressbar;

                if(mode){
                    view.setVisibility(View.GONE);
                    View.inflate(newConText, layoutId, parent);
                    ProgressBar frameLayout = parent.findViewById(R.id.progressBar);
                    ViewGroup.LayoutParams layoutParams = frameLayout.getLayoutParams();
                    layoutParams.height = view.getHeight();
                    layoutParams.width = view.getWidth();
                    frameLayout.setLayoutParams(layoutParams);
                }else{
                    FrameLayout progressBar = parent.findViewById(R.id.progressBarLoading);

                    if(progressBar != null){
                        parent.removeView(progressBar);
                    }

                    view.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
