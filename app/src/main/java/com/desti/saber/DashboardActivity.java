package com.desti.saber;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.*;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;
import com.desti.saber.configs.OkHttpHandler;
import com.desti.saber.data.Result;
import com.desti.saber.utils.HelperListDirButton;
import com.desti.saber.utils.IDRFormatCurr;
import com.desti.saber.utils.ImageSetterFromStream;
import com.desti.saber.utils.constant.PathUrl;
import com.desti.saber.utils.dto.ResponseGlobalJsonDTO;
import com.desti.saber.utils.dto.TrashType;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.zip.Inflater;

public class DashboardActivity extends AppCompatActivity {

    private ImageView profileImage;
    private TextView pinPointLocTitle;
    private  Context context;
    private ImageView trashPhoto;
    private String trashPathPhotoLoc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        ImageSetterFromStream isfs = new ImageSetterFromStream(this);
        LinearLayout withdrawLabelClickable = findViewById(R.id.withdrawLabelClickable);
        LinearLayout detailAccountClickable = findViewById(R.id.detailAccountClickable);
        LinearLayout trashDeliverClickable = findViewById(R.id.trashDeliverClickable);
        LinearLayout pinPointLocClickable = findViewById(R.id.pinPointLocClickable);
        SharedPreferences loginInfo = getSharedPreferences("LoginInfo", Context.MODE_PRIVATE);


        //change user name
        this.setUserNameTittle(loginInfo.getString("username", "Cannot get the name"));
        //set pinpoint loc title
        this.setPinPointLocTitle("Jl. Bukit Cimanggu City Raya Jl. Sholeh Iskandar No.1, RT.01/RW.13, Cibadak, Kec. Tanah Sereal, Kota Bogor, Jawa Barat 16168");
        //set image profile
        this.setImageProfile(null);

        isfs.setAsImageDrawable("withdraw_icon.png", R.id.withdrawLabelIcon);
        isfs.setAsImageDrawable("account_detail_icon.png", R.id.detailLabelIcon);
        isfs.setAsImageDrawable("trash_deliver_icon.png", R.id.trashDeliverIcon);
        isfs.setAsImageDrawable("img_btn_3.png", R.id.imageTopDecoration);
        isfs.setAsImageBackground("dashboard_block_bg.png", R.id.wrapperDecorationDashboard);
        isfs.setAsImageDrawable("location_icon.png", R.id.locationIcon);

        withdrawLabelClickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                withdrawLabelOnClick();
            }
        });
        detailAccountClickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailAccountOnClick();
            }
        });
        trashDeliverClickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trashDeliverOnClick();
            }
        });
        pinPointLocClickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pinPointLocOnClick();
            }
        });

        context = getApplicationContext();
    }

    private void withdrawLabelOnClick(){
        Intent withDrawIntent = new Intent(getApplicationContext(), WithdrawActivity.class);
        startActivity(withDrawIntent);
    }

    private void detailAccountOnClick(){
        Toast.makeText(this, "Detail Account on click", Toast.LENGTH_SHORT).show();
    }

    private void pinPointLocOnClick(){
        Toast.makeText(this, "Pin Point OnClick", Toast.LENGTH_SHORT).show();
    }

    private void trashDeliverOnClick(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Activity activity = this;
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(PathUrl.ROOT_PATH_TRASH_TYPE).get().build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println(e);
                failedConnectToServer();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful() && response.body() != null){
                    ImageSetterFromStream imageSetterFromStream = new ImageSetterFromStream(activity);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Gson gson = new Gson();
                                String bodyReponse = response.body().string();
                                TypeToken<ResponseGlobalJsonDTO<TrashType>> trashTypeResponse =new TypeToken<ResponseGlobalJsonDTO<TrashType>>(){};
                                ResponseGlobalJsonDTO finalResponse = gson.fromJson(bodyReponse, trashTypeResponse.getType());
                                TrashType[] trashTypes = (TrashType[]) finalResponse.getData();
                                List<String> trashTypeNames = new ArrayList<>();
                                List<String> trashTypeAmount = new ArrayList<>();

                                for(TrashType trashType : trashTypes){
                                    trashTypeAmount.add(trashType.getAmount());
                                    trashTypeNames.add(trashType.getType());
                                }

                                int wrapperParam = ViewGroup.LayoutParams.MATCH_PARENT;
                                ViewGroup inflateTrashLay = (ViewGroup) LayoutInflater.from(activity).inflate(R.layout.store_trash_popup, null);
                                PopupWindow  popupWindow = new PopupWindow(inflateTrashLay, wrapperParam, wrapperParam);

                                ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, R.layout.trash_list_dialog_drodown, trashTypeNames);
                                Spinner listTrashType = inflateTrashLay.findViewById(R.id.trashTypeLists);
                                TextView trashAmount = inflateTrashLay.findViewById(R.id.trashAmount);
                                Button cancel = inflateTrashLay.findViewById(R.id.cancelTrashProcess);
                                trashPhoto = inflateTrashLay.findViewById(R.id.trashPhoto);
                                imageSetterFromStream.setAsImageDrawable("defImage.png", trashPhoto);

                                trashPhoto.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        int checkStorageRead = getBaseContext().checkCallingOrSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
                                        ViewGroup popParent = (ViewGroup) trashPhoto.getParent().getParent().getParent();
                                        ViewGroup inflater = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.directory_pop_up_layout, popParent, true);
                                        LinearLayout dirListLocationView = inflater.findViewById(R.id.rootDirectoryList);
                                        Button backButtonDir = inflater.findViewById(R.id.cancelSelectImg);

                                        if(checkStorageRead == PackageManager.PERMISSION_GRANTED){
                                            File envExternalStorage = Environment.getExternalStorageDirectory();
                                            File[] lisFiles = envExternalStorage.listFiles();

                                            if(lisFiles != null){
                                                for(File sgFile : lisFiles){
                                                    TextView singleDirPathTv = new TextView(context);
                                                    String fullPath = sgFile.getAbsolutePath();

                                                    singleDirPathTv.setText(fullPath);
                                                    singleDirPathTv.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            String pathFromClicked = singleDirPathTv.getText().toString();
                                                            if(new File(pathFromClicked).isDirectory()){
                                                                File clickedExtendFiles = new File(pathFromClicked);
                                                                File[] listFiles = clickedExtendFiles.listFiles();

                                                                if(listFiles != null){
                                                                    dirListLocationView.removeAllViews();
                                                                    for(File sgFile : listFiles){
                                                                        TextView sgTextView = new TextView(context);
                                                                        sgTextView.setText(sgFile.getAbsolutePath());
                                                                        sgTextView.setOnClickListener(new View.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(View v) {
                                                                                singleDirPathTv.setText(sgFile.getAbsolutePath());
                                                                                singleDirPathTv.callOnClick();
                                                                            }
                                                                        });
                                                                        dirListLocationView.addView(sgTextView);
                                                                    }
                                                                }
                                                            }else{
                                                                try{
                                                                    Bitmap bitmap = BitmapFactory.decodeFile(pathFromClicked);
                                                                    if(bitmap != null){
                                                                        trashPathPhotoLoc = pathFromClicked;
                                                                        trashPhoto.setImageBitmap(bitmap);
                                                                        inflater.removeView(inflater.findViewById(R.id.parentDirectoryList));
                                                                    }else{
                                                                        Toast.makeText(context,"Silahkan Pilih File Berformat PNG, JPG, GIF", Toast.LENGTH_LONG).show();
                                                                    }
                                                                }catch (Exception e){
                                                                    Toast.makeText(context,"Gagal Melakukan Set Image", Toast.LENGTH_LONG).show();
                                                                }
                                                            }
                                                        }
                                                    });
                                                    dirListLocationView.addView(singleDirPathTv);
                                                }
                                            }
                                        }else{
                                            requestStorageAccess();
                                        }

                                        backButtonDir.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                inflater.removeView(inflater.findViewById(R.id.parentDirectoryList));
                                            }
                                        });
                                    }
                                });

                                cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        popupWindow.dismiss();
                                    }
                                });

                                listTrashType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        trashAmount.setText(IDRFormatCurr.currFormat(Long.valueOf(trashTypeAmount.get(position))) + " / Kilo Gram");
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                    }
                                });

                                listTrashType.setAdapter(adapter);
                                popupWindow.setFocusable(true);
                                popupWindow.showAtLocation(inflateTrashLay, Gravity.CENTER, 0, 0);
                            }catch (Exception e){
                                System.out.println(e);
                                failedConnectToServer();
                            }
                        }
                    });
                }else{
                    failedConnectToServer();
                }
            }
        });

    }

    private void setUserNameTittle(String userName){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int maxUserNameLength = 25;
                String newUserName = (userName.length() > maxUserNameLength) ? userName.substring(0, maxUserNameLength) + "..." : userName;
                ((TextView) findViewById(R.id.userNameLabel)).setText(newUserName);
            }
        });
    }

    private void setPinPointLocTitle(String location){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int maxLocLength = 35;
                String newLocation = (location.length() > maxLocLength) ? location.substring(0, maxLocLength) + "..." : location;
                ((TextView) findViewById(R.id.pinPointLocTitle)).setText(newLocation);
            }
        });
    }

    private void setImageProfile(Bitmap imageProfile){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((ImageView) findViewById(R.id.profileImage)).setImageBitmap(imageProfile);
            }
        });
    }

    private void failedConnectToServer(){
        DashboardActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(
                    getApplication().getApplicationContext(),
                "Gagal Tersambung ke Server ",
                    Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    void requestStorageAccess(){
        String[] permissionList = {Manifest.permission.READ_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(
        this, permissionList, PackageManager.PERMISSION_GRANTED
        );

        if(getApplicationContext().checkCallingOrSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            Toast.makeText(getApplicationContext(), "Berikan Izin Akses Berkas", Toast.LENGTH_LONG).show();
        }
    }
}
