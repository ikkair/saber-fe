package com.desti.saber;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class DetailBankTrasnfer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_bank_trasnfer);
        String detailsTrf = getIntent().getExtras().getString("detailsData");

    }
}