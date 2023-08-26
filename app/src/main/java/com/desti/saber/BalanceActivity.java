package com.desti.saber;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.desti.saber.utils.ImageSetterFromStream;

import java.text.NumberFormat;
import java.util.Currency;

public class BalanceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);

        ImageSetterFromStream isfs = new ImageSetterFromStream(this);
        Button withdrawBtn = findViewById(R.id.withdrawBtn);

        this.setUserNameTittle("Example User Name");
        this.setPinPointLocTitle("Desa Bojonggede");
        this.setBalance(100000000000L);
        isfs.setAsImageBackground("balance_bg.png", R.id.balanceWrapper);

        withdrawBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                withdrawBtnOnClick();
            }
        });
    }

    private void withdrawBtnOnClick(){
        Intent withdrawIntent = new Intent(this, WithdrawActivity.class);
        startActivity(withdrawIntent);
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
    private void setBalance(Long balance){
        NumberFormat currNf = NumberFormat.getCurrencyInstance();
        currNf.setMaximumFractionDigits(0);
        currNf.setCurrency(Currency.getInstance("IDR"));
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView) findViewById(R.id.balanceTitle)).setText(currNf.format(balance).replace("IDR", "Rp. "));
            }
        });
    }

}