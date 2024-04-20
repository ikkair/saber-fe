package com.desti.saber.LayoutHelper.ProgressBar;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import com.desti.saber.R;

public class ProgressBarHelper {
    public static void onProgress(Context context, View view, boolean mode) {
        ViewGroup parent = (ViewGroup) view.getParent();
        int layoutId = R.layout.loading_progressbar;

        if(mode){
            view.setVisibility(View.GONE);
            View.inflate(context, layoutId, parent);
        }else{
            parent.removeView(parent.findViewById(R.id.progressBarLoading));
            view.setVisibility(View.VISIBLE);
        }
    }
}
