package com.desti.saber;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.desti.saber.utils.ImageSetterFromStream;

import org.json.JSONArray;

public class LoginActivity extends AppCompatActivity {

    private Boolean rememberMeStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ImageSetterFromStream imageSetter = new ImageSetterFromStream(this);
        CheckBox rememberMe = findViewById(R.id.rememberMeCheckBox);
        Button logInButton = findViewById(R.id.logInBtnWithCredential);
        TextView titleSignUpClickable = findViewById(R.id.titleTextSignUpClickable);
        TextView titleForgotPasswordClickable = findViewById(R.id.titleTextForgotPasswordClickable);

        imageSetter.setAsImageBackground("transparent_form_data_bg.png", R.id.formDataTransparentContainer);
        imageSetter.setAsImageDrawable("logIn_picture_decoration.png", R.id.logInPictureDecoration);
        imageSetter.setAsImageDrawable("password_icon.png", R.id.userInputPasswordIcon);
        imageSetter.setAsImageDrawable("user_icon.png", R.id.userInputEmailIcon);

        rememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rememberMeOnChecked(isChecked);
            }
        });

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logInBtnOnClicked();
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

    private void rememberMeOnChecked(Boolean checkStatus){
        //TODO : if remember me checked
        this.rememberMeStatus = checkStatus;
    }

    private void logInBtnOnClicked(){
        //TODO : if logIn button on clicked
        EditText password = findViewById(R.id.fieldInputPasswordLogIn);
        EditText emailOrNickName = findViewById(R.id.fieldInputEmailLogIn);
        String passwordValue = password.getText().toString();
        String emailOrNickNameValue = emailOrNickName.getText().toString();

        //refer to dashboard if credential valid
        Intent intentDashboard = new Intent(this, DashboardActivity.class);
        startActivity(intentDashboard);
    }

    private  void  signUpOnClicked(){
        Intent signUpIntent = new Intent(this, RegisterActivity.class);
        startActivity(signUpIntent);
    }

    private void forgotPasswordOnClicked(){
        //TODO : if the title forgot password on clicked
    }


    public void loginButtonHandler(View v){
        String url = "http://";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Toast.makeText(LoginActivity.this, "Toast Login berhasil", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this, "Toast Login Gagal", Toast.LENGTH_SHORT).show();
            }
        });

        QuerySingleton.getInstance(this).addToRequestQueue(request);
    }
}