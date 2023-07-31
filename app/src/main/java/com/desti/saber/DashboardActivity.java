package com.desti.saber;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.desti.saber.utils.ImageSetterFromStream;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ImageSetterFromStream isfs = new ImageSetterFromStream(this);
        isfs.setAsImageDrawable("location_icon.png", R.id.locationIcon);
    }

}
