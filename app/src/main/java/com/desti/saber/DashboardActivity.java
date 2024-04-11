package com.desti.saber;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.*;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;
import com.desti.saber.LayoutHelper.ProgressBar.ProgressBarHelper;
import com.desti.saber.utils.IDRFormatCurr;
import com.desti.saber.utils.ImageSetterFromStream;
import com.desti.saber.utils.constant.PathUrl;
import com.desti.saber.utils.dto.ResponseGlobalJsonDTO;
import com.desti.saber.utils.dto.TrashType;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class DashboardActivity extends AppCompatActivity {

    private ImageView profileImage;
    private TextView pinPointLocTitle;
    private  Context context;
    private ImageView trashPhoto;
    private String trashPathPhotoLoc;
    private String trashTypeSelectedId;
    private String token;


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
        token = loginInfo.getString("token", null);

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
                trashDeliverOnClick(v);
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

    private void trashDeliverOnClick(View view){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Activity activity = this;
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(PathUrl.ROOT_PATH_TRASH_TYPE).get().build();

        ProgressBarHelper.onProgress(getApplication(), view, true);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProgressBarHelper.onProgress(activity.getApplication(), view, false);
                    }
                });
                failedConnectToServer(R.string.failed_con_server);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onResponseTrashMenuSuccess(
                            activity,
                            response,
                            view,
                            okHttpClient
                        );
                    }
                });
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

    private void failedConnectToServer(int messages){
        DashboardActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(
                    getApplication().getApplicationContext(),
                    R.string.failed_con_server,
                    Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void onResponseTrashMenuSuccess(Activity activity, Response response, View view, OkHttpClient okHttpClient){
        ImageSetterFromStream imageSetterFromStream = new ImageSetterFromStream(activity);
        ProgressBarHelper.onProgress(activity.getApplication(), view, false);
        if(response.isSuccessful() && response.body() != null){
            try {
                Gson gson = new Gson();
                String bodyReponse = response.body().string();
                TypeToken<ResponseGlobalJsonDTO<TrashType>> trashTypeResponse =new TypeToken<ResponseGlobalJsonDTO<TrashType>>(){};
                ResponseGlobalJsonDTO finalResponse = gson.fromJson(bodyReponse, trashTypeResponse.getType());
                TrashType[] trashTypes = (TrashType[]) finalResponse.getData();
                List<String> trashTypeNames = new ArrayList<>();
                List<TrashType> trashBundle = new ArrayList<>();

                for(TrashType trashType : trashTypes){
                    trashTypeNames.add(trashType.getType());
                    trashBundle.add(trashType);
                }

                int wrapperParam = ViewGroup.LayoutParams.MATCH_PARENT;
                ViewGroup inflateTrashLay = (ViewGroup) LayoutInflater.from(activity).inflate(R.layout.store_trash_popup, null);
                PopupWindow  popupWindow = new PopupWindow(inflateTrashLay, wrapperParam, wrapperParam);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, R.layout.trash_list_dialog_drodown, trashTypeNames);
                Spinner listTrashType = inflateTrashLay.findViewById(R.id.trashTypeLists);
                TextView trashAmount = inflateTrashLay.findViewById(R.id.trashAmount);
                Button cancel = inflateTrashLay.findViewById(R.id.cancelTrashProcess);
                Button storeTrashesButton = inflateTrashLay.findViewById(R.id.processStoreTrash);

                trashPhoto = inflateTrashLay.findViewById(R.id.trashPhoto);
                imageSetterFromStream.setAsImageDrawable("defImage.png", trashPhoto);

                trashPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int checkStorageRead = getBaseContext().checkCallingOrSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
                        ViewGroup popParent = (ViewGroup) trashPhoto.getParent().getParent().getParent();
                        ViewGroup inflater = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.directory_pop_up_layout, popParent, true);
                        ViewGroup dirListLocationView = inflater.findViewById(R.id.rootDirectoryList);
                        Button backWardLoc = inflater.findViewById(R.id.backTraceLocBtn);
                        Button backButtonDir = inflater.findViewById(R.id.cancelSelectImg);
                        List<String> fileTraceClicked = new ArrayList<>();
                        int singleTvLocId = R.id.singleTvFileLoc;
                        int singleTvNameId = R.id.singleTvFileName;

                        if(checkStorageRead == PackageManager.PERMISSION_GRANTED){
                            File envExternalStorage = Environment.getExternalStorageDirectory();
                            File[] lisFiles = envExternalStorage.listFiles();

                            fileTraceClicked.add(envExternalStorage.getAbsolutePath());

                            if(lisFiles != null){
                                for(int i =0; i < lisFiles.length; i++){
                                    File sgFile = lisFiles[i];
                                    View inflateTvFile = getLayoutInflater()
                                    .inflate(
                                        R.layout.single_list_file_choser,
                                        dirListLocationView, false
                                    );
                                    TextView  singleTvFile = inflateTvFile.findViewById(singleTvNameId);
                                    TextView singleTvLoc = inflateTvFile.findViewById(singleTvLocId);

                                    singleTvLoc.setText(sgFile.getAbsolutePath());
                                    singleTvFile.setText(sgFile.getName());
                                    dirListLocationView.addView(inflateTvFile);
                                    inflateTvFile.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           TextView sgFileNextDirTvLoc = v.findViewById(singleTvLocId);
                                           String fullPathByLocField = sgFileNextDirTvLoc.getText().toString();
                                           File nextDir = new File(fullPathByLocField);

                                           if(nextDir.isDirectory()){
                                               File[] nextDirList = nextDir.listFiles();

                                               if(nextDirList != null){
                                                   if(nextDirList.length < 1){
                                                       Toast.makeText(
                                                           getApplicationContext(),
                                                           "Tidak Ada Berkas Pada Folder " + nextDir.getName(),
                                                           Toast.LENGTH_LONG
                                                       ).show();
                                                   }else{
                                                       if(!fileTraceClicked.contains(fullPathByLocField)){
                                                           fileTraceClicked.add(fullPathByLocField);
                                                       }

                                                       dirListLocationView.removeAllViews();
                                                       for(File nextDirSgFile : nextDirList){
                                                           View inflateNextDir = getLayoutInflater()
                                                           .inflate(
                                                               R.layout.single_list_file_choser,
                                                               dirListLocationView, false
                                                           );
                                                           TextView tvNextDir = inflateNextDir.findViewById(singleTvNameId);
                                                           TextView singleTvLoc = inflateNextDir.findViewById(singleTvLocId);

                                                           tvNextDir.setText(nextDirSgFile.getName());
                                                           singleTvLoc.setText(nextDirSgFile.getAbsolutePath());
                                                           dirListLocationView.addView(inflateNextDir);

                                                           inflateNextDir.setOnClickListener(new View.OnClickListener() {
                                                               @Override
                                                               public void onClick(View v) {
                                                                   ((TextView) inflateTvFile.findViewById(singleTvLocId))
                                                                   .setText(nextDirSgFile.getAbsolutePath());
                                                                   inflateTvFile.callOnClick();
                                                               }
                                                           });
                                                       }
                                                   }
                                               }
                                           }else{
                                               try{
                                                    Bitmap bitmap = BitmapFactory.decodeFile(fullPathByLocField);
                                                    if(bitmap != null){
                                                        trashPathPhotoLoc = fullPathByLocField;
                                                        trashPhoto.setImageBitmap(bitmap);
                                                        inflater.removeView(inflater.findViewById(R.id.parentDirectoryList));
                                                    }else{
                                                        Toast.makeText(context,R.string.trash_photo_format, Toast.LENGTH_LONG).show();
                                                    }
                                                }catch (Exception e){
                                                    Toast.makeText(context,R.string.fail_set_image, Toast.LENGTH_LONG).show();
                                                }
                                           }
                                       }
                                   });

                                    if(i == 0){
                                        backWardLoc.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                int trashBackwardClick = fileTraceClicked.size();

                                                if(trashBackwardClick >= 1){
                                                    int getBackTracePath = (trashBackwardClick == 1) ? 0 : trashBackwardClick-2;
                                                    ((TextView) inflateTvFile.findViewById(singleTvLocId)).setText(fileTraceClicked.get(getBackTracePath));
                                                    inflateTvFile.callOnClick();

                                                    if(trashBackwardClick > 1){
                                                        fileTraceClicked.remove(trashBackwardClick-1);
                                                    }
                                                }

                                            }
                                        });
                                    }
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

                storeTrashesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doPostStoreTrash(
                            inflateTrashLay,
                            okHttpClient,
                            v, popupWindow
                        );
                    }
                });

                listTrashType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        TrashType singleTrashBySelected = trashBundle.get(position);
                        String trashPrice = IDRFormatCurr.currFormat(Long.valueOf(singleTrashBySelected.getAmount())) + " / Kilo Gram";
                        trashTypeSelectedId = singleTrashBySelected.getId();
                        trashAmount.setText(trashPrice);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                listTrashType.setAdapter(adapter);
                popupWindow.setFocusable(true);
                popupWindow.showAtLocation(inflateTrashLay, Gravity.CENTER, 0, 0);
            }catch (Exception e){
                System.out.println(Arrays.toString(e.getStackTrace()));
                failedConnectToServer(R.string.failed_con_server);
            }
        }else{
            failedConnectToServer(R.string.unavailable_service);
        }
    }

    private void doPostStoreTrash(ViewGroup inflater, OkHttpClient okHttpClient, View view, PopupWindow popupWindow){
        if(token == null){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            finishAffinity();
            finish();
            startActivity(intent);
            return;
        }

        String trashDesc = String.valueOf(((EditText) inflater.findViewById(R.id.trashDesc)).getText());
        String trashWeight = String.valueOf(((EditText)  inflater.findViewById(R.id.trashWeight)).getText());

        if(trashDesc.equals("") || trashWeight.equals("") || trashTypeSelectedId == null || trashPathPhotoLoc == null){
            Toast.makeText(getApplicationContext(), R.string.empty_field, Toast.LENGTH_LONG).show();
            return;
        }

        File trashPhotoDetails = new File(trashPathPhotoLoc);
        RequestBody requestBody = new MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("photo",
            trashPhotoDetails.getName(),
            RequestBody.create(
                MediaType.parse("image/png"),
                trashPhotoDetails
            )
        )
        .addFormDataPart("type", trashTypeSelectedId)
        .addFormDataPart("weight", trashWeight)
        .addFormDataPart("pickup_id", "f64c6e7f-7abd-4d73-b256-9bdc98bc7077")
        .addFormDataPart("description", trashDesc)
        .build();

        Request uploadTrashRequest = new Request.Builder()
        .header("Authorization", "Bearer " + token)
        .post(requestBody)
        .url(PathUrl.ROOT_PATH_TRASH)
        .build();

        ProgressBarHelper.onProgress(getApplication(), view, true);
        okHttpClient.newCall(uploadTrashRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProgressBarHelper.onProgress(getApplication(), view, false);
                        Toast.makeText(
                            getApplicationContext(),
                            R.string.err_network,
                            Toast.LENGTH_LONG
                        ).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProgressBarHelper.onProgress(getApplication(), view, false);
                        if(response.isSuccessful()){
                            popupWindow.dismiss();
                            Toast.makeText(
                                getApplicationContext(),
                                R.string.success_trash_store,
                                Toast.LENGTH_LONG
                            ).show();
                        }else{
                            Toast.makeText(
                                getApplicationContext(),
                                R.string.fail_trash_store,
                                Toast.LENGTH_LONG
                            ).show();
                            System.out.println(response.toString());
                        }
                    }
                });
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
