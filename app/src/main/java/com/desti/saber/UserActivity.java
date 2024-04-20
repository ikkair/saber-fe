package com.desti.saber;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class UserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
    }

    public void setorSampahButtonHandler(View v){
        Toast.makeText(this /* MyActivity */, "Toast Setor Sampah berhasil", Toast.LENGTH_SHORT).show();
    }

    public void tarikTunaiButtonHandler(View v){
        Toast.makeText(this /* MyActivity */, "Toast Tarik Tunai berhasil", Toast.LENGTH_SHORT).show();
    }

    public void detailAkunButtonHandler(View v){
        Toast.makeText(this /* MyActivity */, "Toast Detail Akun berhasil", Toast.LENGTH_SHORT).show();
    }
}