package com.desti.saber;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.desti.saber.utils.ImageSetterFromStream;

import org.json.JSONArray;

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
        EditText address = findViewById(R.id.fieldInputAddressRegister);
        EditText password = findViewById(R.id.fieldInputPasswordRegister);
        EditText phoneNumber = findViewById(R.id.fieldInputPhoneRegister);
        EditText firstName = findViewById(R.id.fieldInputFirstNameRegister);
        EditText passwordConfirm = findViewById(R.id.fieldInputPasswordConfirmRegister);
        String passwordConfirmValue = passwordConfirm.getText().toString();
        String phoneNumberValue = phoneNumber.getText().toString();
        String firstNameValue = firstName.getText().toString();
        String passwordValue = password.getText().toString();
        String addressValue = address.getText().toString();
        String emailValue = email.getText().toString();


    }
}