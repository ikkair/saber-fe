package com.desti.saber;

import android.os.StrictMode;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.desti.saber.LayoutHelper.ProgressBar.ProgressBarHelper;
import com.desti.saber.configs.OkHttpHandler;
import com.desti.saber.utils.ImageSetterFromStream;
import com.desti.saber.utils.constant.PathUrl;
import com.desti.saber.utils.constant.UserDetailKeys;
import com.desti.saber.utils.dto.DataLogInDTO;
import com.desti.saber.utils.dto.ResponseGlobalJsonDTO;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private boolean rememberMeStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        ImageSetterFromStream imageSetter = new ImageSetterFromStream(this);
        setContentView(R.layout.activity_login);

        CheckBox rememberMe = findViewById(R.id.rememberMeCheckBox);
        Button logInButton = findViewById(R.id.logInBtnWithCredential);
        TextView titleSignUpClickable = findViewById(R.id.titleTextSignUpClickable);
        TextView titleForgotPasswordClickable = findViewById(R.id.titleTextForgotPasswordClickable);

        StrictMode.setThreadPolicy(policy);
        imageSetter.setAsImageDrawable("transparent_form_data_bg.png", R.id.formDataTransparentContainer);
        imageSetter.setAsImageDrawable("logIn_picture_decoration.png", R.id.logInPictureDecoration);
        imageSetter.setAsImageDrawable("password_icon.png", R.id.userInputPasswordIcon);
        imageSetter.setAsImageDrawable("user_icon.png", R.id.userInputEmailIcon);

        SharedPreferences rememberLogin = getSharedPreferences("InputLoginInfo", Context.MODE_PRIVATE);
        if(rememberLogin.contains("username") && rememberLogin.contains("password")){
            EditText password = findViewById(R.id.fieldInputPasswordLogIn);
            EditText emailOrNickName = findViewById(R.id.fieldInputEmailLogIn);
            password.setText(rememberLogin.getString("password", ""));
            emailOrNickName.setText(rememberLogin.getString("username", ""));
            rememberMe.setChecked(true);
            rememberMeOnChecked(true);
        }

        rememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rememberMeOnChecked(isChecked);
            }
        });

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logInBtnOnClicked(v);
            }
        });

        titleSignUpClickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpOnClicked();
            }
        });

        titleForgotPasswordClickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotPasswordOnClicked();
            }
        });
    }

    private void rememberMeOnChecked(boolean checkStatus){
        //TODO : if remember me checked
        this.rememberMeStatus = checkStatus;
    }

    private void rememberMeData(String email, String password){
        SharedPreferences rememberLogin = getSharedPreferences("InputLoginInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor rememberLoginEditor = rememberLogin.edit();

        if(this.rememberMeStatus){
            rememberLoginEditor.putString("username", email);
            rememberLoginEditor.putString("password", password);
            rememberLoginEditor.apply();
        } else {
            rememberLoginEditor.clear();
            rememberLoginEditor.commit();
        }
    }

    private void logInBtnOnClicked(View view){
        //TODO : if logIn button on clicked
        EditText password = findViewById(R.id.fieldInputPasswordLogIn);
        EditText emailOrNickName = findViewById(R.id.fieldInputEmailLogIn);
        String passwordValue = password.getText().toString();
        String emailOrNickNameValue = emailOrNickName.getText().toString();

        // Creating JSON for payload
        JsonObject loginPayload = new JsonObject();
        loginPayload.addProperty("email", emailOrNickNameValue);
        loginPayload.addProperty("password", passwordValue);
        RequestBody requestBody = RequestBody.create(
            loginPayload.toString(),
            MediaType.parse("application/json")
        );

        OkHttpHandler okHttpHandler = new OkHttpHandler();

        Request request = new Request
        .Builder()
        .url(PathUrl.ENP_LOGIN_USER)
        .post(requestBody)
        .build();

        ProgressBarHelper.onProgress(LoginActivity.this, view, true);
        okHttpHandler.requestAsync(this, request, new OkHttpHandler.MyCallback() {
            @Override
            public void onSuccess(Context context, Response response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProgressBarHelper.onProgress(LoginActivity.this, view, false);
                        int responseType = response.code()/100;

                        SharedPreferences loginInfo = getSharedPreferences(UserDetailKeys.SHARED_PREF_LOGIN_KEY, Context.MODE_PRIVATE);
                        SharedPreferences.Editor loginInfoEditor = loginInfo.edit();

                        ResponseGlobalJsonDTO globalResponse = null;
                        DataLogInDTO[] loginData = null;
                        try {
                            Gson gson = new Gson();
                            TypeToken<ResponseGlobalJsonDTO<DataLogInDTO>> resToken = new TypeToken<ResponseGlobalJsonDTO<DataLogInDTO>>(){};
                            globalResponse = gson.fromJson(response.body().string(), resToken.getType());
                            loginData = (DataLogInDTO[]) globalResponse.getData();

                        } catch (Exception e){
                            Log.e("Parsing Login Error", e.getMessage());
                        }
                        switch (responseType){
                            case 2:
                                loginInfoEditor.putString("username", loginData[0].getName());
                                loginInfoEditor.putString("token", loginData[0].getToken());
                                loginInfoEditor.putString("role", loginData[0].getRole());
                                loginInfoEditor.putString(UserDetailKeys.USER_ID_KEY, loginData[0].getId());
                                loginInfoEditor.putString("photo", loginData[0].getPhoto());
                                loginInfoEditor.putString("balance", String.valueOf(loginData[0].getBalance()));

                                loginInfoEditor.apply();
                                rememberMeData(emailOrNickNameValue, passwordValue);

                                Intent dashboard = new Intent(context, MainDashboard.class);
                                dashboard.putExtra(UserDetailKeys.PASSWORD_KEY, passwordValue);
                                loginInfo.edit().putString(UserDetailKeys.PASSWORD_KEY, passwordValue).apply();
                                startActivity(dashboard);
                                finish();
                                break;
                            case 3:
                            case 4:
                            case 5:
                                try {
                                    ResponseGlobalJsonDTO finalGlobalResponse = globalResponse;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "Login Failed: ".concat(finalGlobalResponse.getMessage()), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } catch (Exception e){
                                    Log.e("Login Toast Error", e.getMessage());
                                }
                                break;
                        }
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProgressBarHelper.onProgress(LoginActivity.this, view, false);
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private  void  signUpOnClicked(){
        Intent signUpIntent = new Intent(this, RegisterActivity.class);
        startActivity(signUpIntent);
        finish();
    }

    private void forgotPasswordOnClicked(){
        //TODO : if the title forgot password on clicked
    }

}

