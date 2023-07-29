package com.desti.saber;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.desti.saber.utils.ImageSetterFromStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageSetterFromStream imageSetter = new ImageSetterFromStream(this);
        Button signUpBtn = findViewById(R.id.signUpBtn);
        Button logInBtn = findViewById(R.id.logInBtn);

        //Looping for background image btn circle
        for (int i = 1; i <= 5; i++){
            String viewXmlId = "image_btn_" + i;
            String imageName = "img_btn_" + i + ".png";
            int resourceId = getResources().getIdentifier(viewXmlId, "id", getPackageName());
            imageSetter.setAsImageDrawable(imageName, resourceId);
        }

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSignUpBtnClick();
            }
        });

        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogInBtnClick();
            }
        });
    }

    private void doLogInBtnClick(){
        Intent logInActivityIntent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(logInActivityIntent);
    }

    private void doSignUpBtnClick(){
        System.out.println(this.toString());
    }

}