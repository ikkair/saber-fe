package com.desti.saber;

import android.os.StrictMode;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.auth0.android.jwt.DecodeException;
import com.auth0.android.jwt.JWT;
import com.desti.saber.utils.ImageSetterFromStream;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_main);

        ImageSetterFromStream imageSetter = new ImageSetterFromStream(this);
        Button signUpBtn = findViewById(R.id.signUpBtn);
        Button logInBtn = findViewById(R.id.logInBtn);

        SharedPreferences loginInfo = getSharedPreferences("LoginInfo", Context.MODE_PRIVATE);
        String jwtToken = loginInfo.getString("token", "");
        try{
            JWT jwt = new JWT(jwtToken);
            if (!jwt.isExpired(0)){
                String role = loginInfo.getString("role", "");
                if (role.equals("user")){
                    Intent dashboard = new Intent(this, MainDashboard.class);
                    startActivity(dashboard);
                    finish();
                }
            }
        } catch (DecodeException err){
            loginInfo.edit().clear();
            loginInfo.edit().apply();
        }

        //Looping for background image btn circle
        for (int i = 1; i <= 5; i++){
            String viewXmlId = "image_btn_" + i;
            String imageName = "img_btn_" + i + ".png";
            int resourceId = getResources().getIdentifier(viewXmlId, "id", getPackageName());
            System.out.println(resourceId);
            imageSetter.setAsImageDrawable(imageName, resourceId);
        }

        SharedPreferences sharedPreferences = getSharedPreferences("LoginInfo", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("token")){
            Log.d("Token Exist", "Token still exist");
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
        Intent logInActivityIntent = new Intent(this, LoginActivity.class);
        startActivity(logInActivityIntent);
    }

    private void doSignUpBtnClick(){
        Intent signUpActivityIntent = new Intent(this, RegisterActivity.class);
        startActivity(signUpActivityIntent);
    }

}