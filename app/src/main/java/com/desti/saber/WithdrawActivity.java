package com.desti.saber;

import android.content.Context;
import android.view.*;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.desti.saber.LayoutHelper.SingleTrxListLayout.OnClickActionActivity;
import com.desti.saber.LayoutHelper.SingleTrxListLayout.ParentSingleListViewGroup;
import com.desti.saber.utils.ImageSetterFromStream;

import java.sql.Date;
import java.text.NumberFormat;
import java.util.Currency;

public class WithdrawActivity extends AppCompatActivity {

    private ImageSetterFromStream imageSetterFromStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);

        imageSetterFromStream = new ImageSetterFromStream(this);
        Button balanceButton  = findViewById(R.id.balanceNavBtn);
        Button historyTrxButton = findViewById(R.id.historyTransactionNavBtn);

        this.setUserNameTittle("Example User Name");
        this.setPinPointLocTitle("Desa Bojonggede");
        this.setBalance();

        balanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBalance();
            }
        });

        historyTrxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setHistoryTrx();
            }
        });
    }

    private void setBalance(){
        //Please Calling Endpoint Here
        this.setBalanceValue(100000000000L);
    }

    private void setHistoryTrx(){
        clearRecentLayout();
        View inflateHistoryTrx = getLayoutInflater().inflate(R.layout.history_transaction_layout, findViewById(R.id.rootWithDrawActivity));
        ViewGroup rootParentTrxList = inflateHistoryTrx.findViewById(R.id.rootParentTrxList);
        Context context = rootParentTrxList.getContext();

        //section for looping list trx history
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                for(int i =0; i < 100; i++){
                    ParentSingleListViewGroup parentSingleList = new ParentSingleListViewGroup(context);
                    parentSingleList.setUserName("Susu Aku Ada " + String.valueOf(i));
                    parentSingleList.setDate(new Date(System.currentTimeMillis()).toString());
                    parentSingleList.setActivity("Penarikan Tunai " + i, "Rp. 999.999.999.999");
                    parentSingleList.setDescription("Buat Beli " + i + " Toke Rp. " + i);
                    parentSingleList.setActivityStatus("Berhasil", new OnClickActionActivity() {
                        @Override
                        public void onClick() {
                            //use syntax at below if you get callback from server successfully delete history
                            rootParentTrxList.removeView(parentSingleList);
                        }
                    });
                    rootParentTrxList.addView(parentSingleList);
                }

            }
        });
    }

    private void setUserNameTittle(String userName){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int maxUserNameLength = 25;
                String newUserName = (userName.length() > maxUserNameLength) ? userName.substring(0, maxUserNameLength) + "..." : userName;
                ((TextView) findViewById(R.id.userNameLabel)).setText(newUserName);
            }
        });
    }

    private void setPinPointLocTitle(String location){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int maxLocLength = 35;
                String newLocation = (location.length() > maxLocLength) ? location.substring(0, maxLocLength) + "..." : location;
                ((TextView) findViewById(R.id.pinPointLocTitle)).setText(newLocation);
            }
        });
    }

    private void setImageProfile(Bitmap imageProfile){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((ImageView) findViewById(R.id.profileImage)).setImageBitmap(imageProfile);
            }
        });
    }

    private void setBalanceValue(Long balance){
        NumberFormat currNf = NumberFormat.getCurrencyInstance();
        currNf.setMaximumFractionDigits(0);
        currNf.setCurrency(Currency.getInstance("IDR"));
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                clearRecentLayout();

                ViewGroup rootActivity = findViewById(R.id.rootWithDrawActivity);
                View balanceLayout = getLayoutInflater().inflate(R.layout.balance_layout, rootActivity);
                int balanceWrapper = balanceLayout.findViewById(R.id.balanceWrapper).getId();
                TextView balanceTextView = balanceLayout.findViewById(R.id.balanceTitle);

                imageSetterFromStream.setAsImageBackground("balance_bg.png", balanceWrapper);

                if(balance == null){
                    balanceTextView.setText(0);
                }else{
                    balanceTextView.setText(currNf.format(balance).replace("IDR", "Rp. "));
                }
            }
        });
    }

    private void clearRecentLayout(){
        if(findViewById(R.id.balanceLayout) != null){
            ((ViewGroup) findViewById(R.id.balanceLayout).getParent()).removeView(findViewById(R.id.balanceLayout));
        }

        if(findViewById(R.id.historyTransactionLayout) != null){
            ((ViewGroup) findViewById(R.id.historyTransactionLayout).getParent()).removeView(findViewById(R.id.historyTransactionLayout));
        }
    }

}