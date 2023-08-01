package com.desti.saber;

import android.graphics.Outline;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.desti.saber.utils.ImageSetterFromStream;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ImageSetterFromStream isfs = new ImageSetterFromStream(this);
        FrameLayout iam = findViewById(R.id.iam);

        iam.setClipToOutline(true);
        iam.setTranslationZ(20);
        iam.setTranslationX(0);
        iam.setElevation(30);
        iam.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                Rect rect = new Rect(0, 20, view.getWidth(), view.getHeight());
                outline.setRoundRect(rect, 20);
            }
        });
        isfs.setAsImageDrawable("location_icon.png", R.id.locationIcon);
    }

}
