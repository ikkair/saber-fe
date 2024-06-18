package com.desti.saber.LayoutHelper.SingleTrxListLayout;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import com.desti.saber.R;
import com.desti.saber.utils.ImageSetterFromStream;

public class ActivityTitleTrxList extends FrameLayout {

    private final ImageSetterFromStream imageSetterFromStream;

    public ActivityTitleTrxList(Context context) {
        super(context);
        this.imageSetterFromStream = new ImageSetterFromStream((Activity) context);
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
        textView.setTextAppearance(R.style.activity_result_text_trx_list);
        textView.setGravity(Gravity.END);
        textView.setPadding(0, 20,0, 20);
        textView.setText(descriptionValue);

        addView(textView);
    }

    public void setActivityStatus(String activityValue, OnClickActionSingleListActivity onClickActionSingleListActivity){
        Button trashButton = new Button(getContext());
        SingleTrxViewText activityStatus = new SingleTrxViewText(getContext());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(50, 60);
        FrameLayout.LayoutParams lp2 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int gravity =  Gravity.CENTER;

        layoutParams.gravity = Gravity.END | gravity;
        lp2.gravity = Gravity.START | gravity;

        trashButton.setLayoutParams(layoutParams);
        imageSetterFromStream.setAsImageBackground("waste_icon.png", trashButton);

        activityStatus.setActivityStatus(activityValue);
        activityStatus.setLayoutParams(lp2);

        setPadding(0, 5, 0, 5);
        addView(activityStatus);
        addView(trashButton);

        trashButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickActionSingleListActivity.onClick(view);
            }
        });
    }


}
