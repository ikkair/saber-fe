package com.desti.saber.LayoutHelper.SingleTrxListLayout;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.desti.saber.R;

public class ParentSingleListViewGroup extends LinearLayout {

    public ParentSingleListViewGroup(Context context) {
        super(context, null, 0, R.style.single_layout_list_trx_history);

        LayoutParams layoutParams = new LayoutParams(
            LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );

        layoutParams.setMargins(20, 10, 20, 10);
        setBackgroundResource(R.drawable.white_rounded_bg_border);
        setPadding(10, 10, 10, 10);
        setOrientation(LinearLayout.VERTICAL);
        setLayoutParams(layoutParams);
    }

    public void setUserName(String userNameText){
        SingleTrxViewText viewText = new SingleTrxViewText(getContext());
        viewText.setUserName(userNameText);
        addView(viewText);
    }

    public void setDate(String dateText){
        SingleTrxViewText viewText = new SingleTrxViewText(getContext());
        viewText.setDate(dateText);
        addView(viewText);
    }

    public void setActivity(String activityName, String activityResult){
        ActivityTitleTrxList activityTitleTrxList = new ActivityTitleTrxList(getContext());
        activityTitleTrxList.setTitleActivityList(activityName, activityResult);
        addView(activityTitleTrxList);
    }

    public void setDescription(String descriptionText){
        ActivityTitleTrxList activityDescription = new ActivityTitleTrxList(getContext());
        activityDescription.setActivityDescription(descriptionText);
        addView(activityDescription);
    }

    public void setActivityStatus(String activityStatusText, OnClickActionActivity onClickActionActivity){
        ActivityTitleTrxList activityStatus = new ActivityTitleTrxList(getContext());
        activityStatus.setActivityStatus(activityStatusText, onClickActionActivity);
        addView(activityStatus);
    }
}
