package com.desti.saber;

import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.desti.saber.utils.dto.DetailTrfDto;
import com.google.gson.Gson;

public class DetailBankTrasnfer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_detail_bank_trasnfer);

        String detailsTrf = getIntent().getExtras().getString("detailsData");
        DetailTrfDto detailTrfDto = new Gson().fromJson(detailsTrf, DetailTrfDto.class);
        TextView detailsTrfTv = findViewById(R.id.detailsTrf);
        Button backToDashBoard = findViewById(R.id.backToDashBoardFrDtl);

        detailsTrfTv.setText(detailTrfDto.toString());
        backToDashBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}