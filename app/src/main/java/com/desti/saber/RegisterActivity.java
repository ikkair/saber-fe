package com.desti.saber;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.desti.saber.LayoutHelper.ProgressBar.ProgressBarHelper;
import com.desti.saber.configs.OkHttpHandler;
import com.desti.saber.utils.ImageSetterFromStream;
import com.desti.saber.utils.constant.PathUrl;
import com.desti.saber.utils.dto.DataLogInDTO;
import com.desti.saber.utils.dto.ResponseGlobalJsonDTO;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

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
        isfs.setAsImageDrawable("transparent_form_data_bg.png", R.id.formDataTransparentContainer);
        isfs.setAsImageDrawable("email_icon.png", R.id.userInputEmailIcon);
        isfs.setAsImageDrawable("user_icon.png", R.id.userInputNameIcon);
        isfs.setAsImageDrawable("password_icon.png", R.id.userInputPasswordIcon);

        signInTextViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logIn = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(logIn);
                finish();
            }
        });
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickedButtonSignUpRegister(v);
            }
        });
    }

    private void onClickedButtonSignUpRegister(View view){
        // TODO : if button signup clicked
        EditText email = findViewById(R.id.fieldInputEmailRegister);
        EditText password = findViewById(R.id.fieldInputPasswordRegister);
        EditText Name = findViewById(R.id.fieldInputNameRegister);
        String nameValue = Name.getText().toString();
        String passwordValue = password.getText().toString();
        String emailValue = email.getText().toString();

        if(nameValue.equals("") || passwordValue.equals("") || emailValue.equals("")){
            Toast.makeText(
                getApplicationContext(),
                R.string.empty_field,
                Toast.LENGTH_LONG
            ).show();
            return;
        }

        JsonObject registerPayload = new JsonObject();
        registerPayload.addProperty("name", nameValue);
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

        ProgressBarHelper.onProgress(getApplicationContext(), view, true);
        okHttpHandler.requestAsync(this, request, new OkHttpHandler.MyCallback() {
            @Override
            public void onSuccess(Context context, Response response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProgressBarHelper.onProgress(getApplicationContext(), view, false);
                        if(response != null){
                            try {
                                Gson gson = new Gson();
                                TypeToken<ResponseGlobalJsonDTO<DataLogInDTO>> resToken = new TypeToken<ResponseGlobalJsonDTO<DataLogInDTO>>(){};
                                ResponseGlobalJsonDTO globalResponse = gson.fromJson(response.body().string(), resToken.getType());
                                DataLogInDTO[] loginData = (DataLogInDTO[]) globalResponse.getData();
                                ResponseGlobalJsonDTO finalGlobalResponse = globalResponse;

                                if(response.isSuccessful()){
                                    Toast.makeText(
                                        getApplicationContext(),
                                        R.string.success_register,
                                        Toast.LENGTH_LONG
                                    ).show();
                                }else{
                                    Toast.makeText(getApplicationContext(), "Gagal Melakukan Registrasi : ".concat(finalGlobalResponse.getMessage()), Toast.LENGTH_SHORT).show();
                                }
                                return;
                            } catch (Exception e){
                                Log.e("Parsing Login Error", e.fillInStackTrace().toString());
                            }
                        }
                        Toast.makeText(getApplicationContext(), "Failed Register", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onFailure(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProgressBarHelper.onProgress(getApplicationContext(), view, false);
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}