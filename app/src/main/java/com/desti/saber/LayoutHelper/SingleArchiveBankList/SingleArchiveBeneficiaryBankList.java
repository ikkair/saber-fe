package com.desti.saber.LayoutHelper.SingleArchiveBankList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.desti.saber.R;

public class SingleArchiveBeneficiaryBankList extends LinearLayout {

    private TextView textView;
    private ImageView imageView;

    public SingleArchiveBeneficiaryBankList(Context context) {
        super(context);

        LayoutParams layoutParams = new LayoutParams(
            LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );

        layoutParams.setMargins(
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
        setLayoutParams(layoutParams);
        setOrientation(HORIZONTAL);

        textView = new TextView(context);
        imageView = new ImageView(context);
    }

    public void setSingleListBankArchive(Bitmap bankIcon, String accountOwnerName, OnClickArchiveBankBeneficiary beneficiary){
        imageView.setImageBitmap(bankIcon);

        textView.setText(accountOwnerName);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setTextAppearance(R.style.activity_result_text_trx_list);

        addView(imageView);
        addView(textView);

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                beneficiary.onClick();
            }
        });
    }
}
