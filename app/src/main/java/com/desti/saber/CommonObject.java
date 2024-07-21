package com.desti.saber;

import android.app.Activity;
import android.view.ViewGroup;
import com.desti.saber.LayoutHelper.UserDetails.UserDetails;

public abstract class CommonObject {
    private final Activity activity;
    private final ViewGroup rootView;
    private final UserDetails userDetails;

    public CommonObject(Activity activity, ViewGroup rootView, UserDetails userDetails) {
        this.activity = activity;
        this.rootView = rootView;
        this.userDetails = userDetails;
    }

    public Activity getActivity() {
        return activity;
    }

    public ViewGroup getRootView() {
        return rootView;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }
}
