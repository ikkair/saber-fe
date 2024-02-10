package com.desti.saber.LayoutHelper.SingleArchiveBankList;

import android.content.Context;
import android.util.LayoutDirection;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import com.desti.saber.R;

public class SingleStoredBankNameToggle extends FrameLayout {

    private final TextView labelToggle;
    private final Switch switchButton;

    public SingleStoredBankNameToggle(@NonNull Context context) {
        super(context);
        switchButton = new Switch(context);
        labelToggle = new TextView(context);

        FrameLayout.LayoutParams layParamFl = new LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        );
        ViewGroup.LayoutParams layParamSb = new LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            Gravity.END | Gravity.CENTER
        );
        ViewGroup.LayoutParams layParamLb = new LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            Gravity.START | Gravity.CENTER
        );

        layParamFl.setMargins(
            ConstantPropNumber.MARGIN_LEFT,
            ConstantPropNumber.MARGIN_TOP,
            ConstantPropNumber.MARGIN_RIGHT,
            ConstantPropNumber.MARGIN_BOTTOM
        );
        setPadding(
            ConstantPropNumber.PADDING_LEFT,
            ConstantPropNumber.PADDING_TOP,
            ConstantPropNumber.PADDING_RIGHT,
            ConstantPropNumber.PADDING_BOTTOM
        );
        setBackgroundResource(R.drawable.rectagle_bank_trf);
        setLayoutParams(layParamFl);

        switchButton.setLayoutParams(layParamSb);

        labelToggle.setLayoutParams(layParamLb);
        labelToggle.setText(R.string.stored_account_number);
        labelToggle.setTextAppearance(R.style.activity_result_text_trx_list);
    }

    public void setSwitchButton(OnClickSwitchSaved switchButtonOnClick){
        switchButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                switchButtonOnClick.onClick(((Switch) view).isChecked());
            }
        });

        addView(labelToggle);
        addView(switchButton);
    }
}
