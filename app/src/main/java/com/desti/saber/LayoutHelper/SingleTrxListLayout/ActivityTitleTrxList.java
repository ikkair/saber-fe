package com.desti.saber.LayoutHelper.SingleTrxListLayout;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.desti.saber.R;
import com.desti.saber.utils.ImageSetterFromStream;

public class ActivityTitleTrxList extends FrameLayout {

    private final ImageSetterFromStream imageSetterFromStream;

    public ActivityTitleTrxList(@NonNull Context context) {
        super(context);
        this.imageSetterFromStream = new ImageSetterFromStream((Activity)getContext());
        ViewGroup.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(layoutParams);
    }

    public void setTitleActivityList(String titleActivityValue, String activityResultValue){
        SingleTrxViewText titleActivity = new SingleTrxViewText(getContext());
        SingleTrxViewText activityResult = new SingleTrxViewText(getContext());

        titleActivity.setActivityName(titleActivityValue);
        activityResult.setActivityResult(activityResultValue);

        addView(titleActivity);
        addView(activityResult);
    }

    public void setActivityDescription(String descriptionValue){
        TextView textView = new TextView(getContext());
        textView.setGravity(Gravity.END);
        textView.setText(descriptionValue);
        textView.setTextAppearance(R.style.activity_result_text_trx_list);
        textView.setPadding(0, 2,0, 5);
        addView(textView);
    }

    public void setActivityStatus(String activityValue, OnClickActionSingleListActivity onClickActionSingleListActivity){
        ImageView trashButton = new ImageView(getContext());
        SingleTrxViewText activityStatus = new SingleTrxViewText(getContext());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutParams.gravity = Gravity.END;
        trashButton.setLayoutParams(layoutParams);
        activityStatus.setActivityStatus(activityValue);
        imageSetterFromStream.setAsImageDrawable("waste_icon.png", trashButton);

        setPadding(0, 15, 0, 0);
        addView(activityStatus);
        addView(trashButton);

        trashButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickActionSingleListActivity.onClick();
            }
        });
    }


}
