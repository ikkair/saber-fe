package com.desti.saber;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.desti.saber.utils.ImageSetterFromStream;

import org.json.JSONArray;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ImageSetterFromStream imageSetter = new ImageSetterFromStream(this);
        imageSetter.setAsImageDrawable("logIn_picture_decoration.png", R.id.logInPictureDecoration);
        imageSetter.setAsImageBackground("transparent_form_data_bg.png", R.id.formDataTransparentContainer);
        imageSetter.setAsImageDrawable("user_icon.png", R.id.userInputEmailIcon);
        imageSetter.setAsImageDrawable("password_icon.png", R.id.userInputPasswordIcon);
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