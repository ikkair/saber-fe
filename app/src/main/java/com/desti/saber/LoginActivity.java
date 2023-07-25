package com.desti.saber;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import java.io.InputStream;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        try{
            AssetManager asm = getAssets();
            InputStream ism = asm.open("logIn_picture_decoration.png");
            ImageView imageViewDecoration = findViewById(R.id.logInPictureDecoration);
            imageViewDecoration.setImageDrawable(Drawable.createFromStream(ism, null));

        }catch (Exception err){
            System.out.println(err);
        }
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