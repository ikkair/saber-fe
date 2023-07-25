package com.desti.saber;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private AssetManager asm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.asm = getAssets();
        Button signUpBtn = findViewById(R.id.signUpBtn);
        Button logInBtn = findViewById(R.id.logInBtn);

        //Looping for background image btn circle
        for (int i = 1; i <= 5; i++){
            String viewXmlId = "image_btn_" + i;
            ImageView resourceView = findViewById(getResources().getIdentifier(viewXmlId, "id", getPackageName()));
            setCircleButtonImage("img_btn_" + i + ".png", resourceView);
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

    public void loginButtonHandler(View v){
        Intent loginActivityIntent = new Intent(this, LoginActivity.class);
        startActivity(loginActivityIntent);
    }

    public void registerButtonHandler(View v){
        Intent registerActivityIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerActivityIntent);
    }

    private void setCircleButtonImage(String imageName, ImageView imv){
        try {
            InputStream ism = this.asm.open(imageName);
            imv.setImageDrawable(Drawable.createFromStream(ism, null));
        }catch (Exception err){
            System.out.println(err);
        }
    }

    private void doLogInBtnClick(){
        Intent logInActivityIntent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(logInActivityIntent);
    }

    private void doSignUpBtnClick(){
        System.out.println(this.toString());
    }

}