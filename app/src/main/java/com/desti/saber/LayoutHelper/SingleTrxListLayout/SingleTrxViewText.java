package com.desti.saber.LayoutHelper.SingleTrxListLayout;


import android.content.Context;
import android.view.Gravity;
import androidx.annotation.NonNull;
import com.desti.saber.R;
import org.jetbrains.annotations.NotNull;

public class SingleTrxViewText extends androidx.appcompat.widget.AppCompatTextView {

    public SingleTrxViewText(@NonNull @NotNull Context context) {
        super(context);
    }

    public void setUserName(String valueText){
        setText(valueText);
        setTextAppearance(getContext(), R.style.huge_tittle_text_black);
    }

    public void setDate(String valueDate){
        setText(valueDate);
        setTextAppearance(getContext(), R.style.activity_date_text_trx_list);
    }

    public void setActivityName(String valueActivityName){
        setGravity(Gravity.START);
        setText(valueActivityName);
        setTextAppearance(getContext(), R.style.activity_text_trx_list);
    }

    public void setActivityResult(String valueActivityResult){
        setGravity(Gravity.END);
        setText(valueActivityResult);
        setTextAppearance(getContext(), R.style.activity_result_text_trx_list);
    }

    public void setActivityStatus(String statusActivityValue){
        setText(statusActivityValue);
        setGravity(Gravity.START);
        setTextAppearance(getContext(), R.style.activity_status_text_trx_list);
    }
}
