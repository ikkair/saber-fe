package com.desti.saber;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.desti.saber.utils.ImageSetterFromStream;
import com.desti.saber.utils.constant.PathUrl;
import com.desti.saber.utils.dto.DataLogInDTO;
import com.desti.saber.utils.dto.ResponseGlobalJsonDTO;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ImageSetterFromStream isfs = new ImageSetterFromStream(this);
        TextView signInTextViewButton = findViewById(R.id.titleTextSignUpClickable);
        Button signUpBtn = findViewById(R.id.signUpBtnRegister);

        isfs.setAsImageDrawable("logIn_picture_decoration.png", R.id.logInPictureDecoration);
        isfs.setAsImageBackground("transparent_form_data_bg_long.png", R.id.formDataTransparentContainer);
        isfs.setAsImageDrawable("user_icon.png", R.id.userInputFirstNameIcon);
        isfs.setAsImageDrawable("phone_icon.png", R.id.userInputAddressIcon);
        isfs.setAsImageDrawable("phone_icon.png", R.id.userInputPhoneIcon);
        isfs.setAsImageDrawable("email_icon.png", R.id.userInputEmailIcon);
        isfs.setAsImageDrawable("password_icon.png", R.id.userInputPasswordIcon);
        isfs.setAsImageDrawable("password_icon.png", R.id.userInputConfirmPasswordIcon);

        signInTextViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logIn = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(logIn);
            }
        });
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickedButtonSignUpRegister();
            }
        });
    }

    private void onClickedButtonSignUpRegister(){
        // TODO : if button signup clicked
        EditText email = findViewById(R.id.fieldInputEmailRegister);
        EditText password = findViewById(R.id.fieldInputPasswordRegister);
        EditText Name = findViewById(R.id.fieldInputNameRegister);
        String NameValue = Name.getText().toString();
        String passwordValue = password.getText().toString();
        String emailValue = email.getText().toString();

    JsonObject registerPayload = new JsonObject();
        registerPayload.addProperty("name", NameValue);
        registerPayload.addProperty("email", emailValue);
        registerPayload.addProperty("password", passwordValue);
        RequestBody requestBody = RequestBody.create(
                registerPayload.toString(),
                MediaType.parse("application/json")
        );
        OkHttpHandler okHttpHandler = new OkHttpHandler();

        Request request = new Request
                .Builder()
                .url(PathUrl.ENP_REGISTER_USER)
                .post(requestBody)
                .build();
        okHttpHandler.requestAsync(this, request, new OkHttpHandler.MyCallback() {
            @Override
            public void onSuccess(Context context, Response response) {
                int responseType = response.code()/100;
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Register Success, Check your email",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
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
            @Override
            public void onFailure(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}