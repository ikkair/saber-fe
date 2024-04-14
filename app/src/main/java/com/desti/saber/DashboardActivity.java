package com.desti.saber;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaParser;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.*;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.desti.saber.LayoutHelper.ManualImageChoser.ManualImageChooser;
import com.desti.saber.LayoutHelper.ManualImageChoser.SuccessSetImage;
import com.desti.saber.LayoutHelper.ProgressBar.ProgressBarHelper;
import com.desti.saber.utils.GPSTrackerHelper;
import com.desti.saber.utils.IDRFormatCurr;
import com.desti.saber.utils.ImageSetterFromStream;
import com.desti.saber.utils.constant.PathUrl;
import com.desti.saber.utils.dto.ResponseGlobalJsonDTO;
import com.desti.saber.utils.dto.TrashType;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;
import okio.BufferedSink;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.*;

public class DashboardActivity extends AppCompatActivity {

    private ImageView profileImage;
    private String tempLocPickup;
    private String finalLocPickUp;
    private Context context;
    private ImageView trashPhoto;
    private String trashPathPhotoLoc;
    private String trashTypeSelectedId;
    private String token;
    private String userId;
    private String notFoundLoc = getString(R.string.not_found_loc);


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
        userId = loginInfo.getString("user_id", null);

        //change user name
        this.setUserNameTittle(loginInfo.getString("username", "Cannot get the name"));
        //set pinpoint loc title
        this.setPinPointLocTitle("Tidak Ada Lokasi Dipilih");
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
                detailAccountOnClick(v);
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

    private void detailAccountOnClick(View v){
        View inflateProfileDetail = getLayoutInflater().inflate(R.layout.profile_detail_layout, null);
        PopupWindow popupWindow = new PopupWindow(inflateProfileDetail);
        popupWindow.showAtLocation(v, Gravity.CENTER, 1, 1);

//        Toast.makeText(this, "Detail Account on click", Toast.LENGTH_SHORT).show();
    }

    private void pinPointLocOnClick(){
        try {
            Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
            View layoutInflater = DashboardActivity.this.getLayoutInflater().inflate(R.layout.maps_pop_up_layout, null);
            PopupWindow popupWindow = new PopupWindow(layoutInflater, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            Button cancelButton = layoutInflater.findViewById(R.id.cancelSetLoc);
            GPSTrackerHelper gpsTrackerHelper = new GPSTrackerHelper(this);
            MapView mapView = layoutInflater.findViewById(R.id.maps);
            EditText searchMapField = popupWindow.getContentView().findViewById(R.id.searchMapField);
            GeoPoint geoPoint = new GeoPoint(
                gpsTrackerHelper.getLatitude(),
                gpsTrackerHelper.getLongitude()
            );
            Marker markerPin = new Marker(mapView);

            Overlay overlay = new Overlay(this) {
                @Override
                public boolean onSingleTapConfirmed(MotionEvent e, MapView mapView) {
                    try {
                        Projection proj = mapView.getProjection();
                        GeoPoint loc = (GeoPoint) proj.fromPixels((int)e.getX(), (int)e.getY());
                        getGeocoder(loc,popupWindow, markerPin, mapView);
                    } catch (IOException ex) {
                        System.out.println(ex);
                    }
                    return true;
                }
            };

            markerPin.setPosition(geoPoint);
            markerPin.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

            mapView.getController().setZoom(18.0);
            mapView.getController().setCenter(geoPoint);
            mapView.setMultiTouchControls(true);
            mapView.getOverlays().add(markerPin);
            mapView.getOverlays().add(overlay);

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });

            searchMapField.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    try {
                        if(keyCode == 66){
                            String value = ((EditText) v).getText().toString();
                            if(!value.equals("")){
                                searchMapField.setEnabled(false);
                                Geocoder geocoder = new Geocoder(popupWindow.getContentView().getContext(), Locale.getDefault());
                                List<Address> addressList = geocoder.getFromLocationName(value, 1000);

                                if(addressList.size() > 0){
                                    Address address = addressList.get(0);
                                    GeoPoint geoPointSearch = new GeoPoint(address.getLatitude(), address.getLongitude());
                                    getGeocoder(geoPointSearch, popupWindow, markerPin, mapView);
                                }else {
                                    Toast.makeText(popupWindow.getContentView().getContext(), "Lokasi Tidak Ditemukan", Toast.LENGTH_LONG).show();
                                }

                                searchMapField.setEnabled(true);
                                return true;
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return false;
                }
            });

            getGeocoder(geoPoint, popupWindow, markerPin, mapView);
            popupWindow.setFocusable(true);
            popupWindow.showAtLocation(layoutInflater, Gravity.CENTER, 0, 0);
        }catch (Exception e){
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }

    private void getGeocoder(GeoPoint loc, PopupWindow popupWindow, Marker marker, MapView mapView) throws IOException{
        Context appCon = popupWindow.getContentView().getContext();
        View popUpView = popupWindow.getContentView();
        Geocoder geocoder = new Geocoder(appCon, Locale.getDefault());
        List<Address> listGeocoder = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1000);

        EditText locByPin = popUpView.findViewById(R.id.locationByPinPoint);
        Spinner spinnerLocOption = popUpView.findViewById(R.id.locOption);
        Button cancelSetLoc = popUpView.findViewById(R.id.cancelSetLoc);
        EditText detailsLoc = popUpView.findViewById(R.id.detailsLoc);
        Button setLoc = popUpView.findViewById(R.id.setLoc);

        mapView.postInvalidate();

        if(locByPin != null && spinnerLocOption != null){
            List<String> addressList = new ArrayList<>();
            ArrayAdapter<String> adapter = new ArrayAdapter<>(appCon, R.layout.trash_list_dialog_dropdown, addressList);

            for(int i=0; i < listGeocoder.size(); i++){
                addressList.add(listGeocoder.get(i).getAddressLine(0));

                if(i == 0){
                    Address singleAddress = listGeocoder.get(0);
                    GeoPoint geoPoint = new GeoPoint(singleAddress.getLatitude(), singleAddress.getLatitude());
                    tempLocPickup = listGeocoder.get(0).getAddressLine(0);
                    locByPin.setText(tempLocPickup);
                    marker.setPosition(geoPoint);
                    mapView.getController().animateTo(geoPoint);
                }
            }

            if(listGeocoder.size() == 0){
                locByPin.setText(getString(R.string.not_found_loc));
                adapter.add(getString(R.string.not_found_loc));
            }

            popupWindow.setFocusable(true);
            spinnerLocOption.setAdapter(adapter);
            spinnerLocOption.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(listGeocoder.size() > 0) {
                        Address address = listGeocoder.get(position);
                        GeoPoint geoPoint = new GeoPoint(address.getLatitude(), address.getLongitude());
                        tempLocPickup = addressList.get(position);
                        locByPin.setText(tempLocPickup);
                        marker.setPosition(geoPoint);
                        mapView.getController().animateTo(geoPoint);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            setLoc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                    String detailLocFinal = detailsLoc.getText().toString();
                    detailLocFinal = (detailLocFinal.equals("")) ? "" : " (" + detailLocFinal + ")";
                    finalLocPickUp = tempLocPickup + detailLocFinal;
                    setPinPointLocTitle(finalLocPickUp);
                }
            });

            cancelSetLoc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });
        }
    }

    private void trashDeliverOnClick(View view){
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
        if(finalLocPickUp != null && finalLocPickUp != ""){

        }

        Activity activity = DashboardActivity.this;
        ProgressBarHelper.onProgress(activity, view, true);

        HashMap<String, String> requestList = new HashMap<>();
        OkHttpClient okHttpClient = new OkHttpClient();

        requestList.put("address", finalLocPickUp);
        requestList.put("user_id", userId);
        requestList.put("time", new Date(System.currentTimeMillis()).toString());

        String finalJsonPayload = new Gson().toJson(requestList);
        RequestBody requestBody = RequestBody.create(finalJsonPayload, MediaType.parse("application/json"));
        Request requestCreatePickupId = new Request
        .Builder().post(requestBody)
        .header("Authorization", "Bearer " + token)
        .url(PathUrl.ROOT_PATH_PICKUP).build();

        okHttpClient.newCall(requestCreatePickupId).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                ProgressBarHelper.onProgress(activity, view, false);
                failedConnectToServer(R.string.failed_con_server);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProgressBarHelper.onProgress(activity, view, false);
                        if(response.isSuccessful()){
                            try {
                                System.out.println(call.request().body().toString());
                                System.out.println(response.body().string());
                                View screeningLayout = activity.getLayoutInflater().inflate(R.layout.popup_notif_fullscreen, null);
                                PopupWindow window = new PopupWindow(screeningLayout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                                TextView tvPickupId = screeningLayout.findViewById(R.id.messagesPoUp);
                                Button leftButton  = screeningLayout.findViewById(R.id.leftPopUpBtn);
                                Button rightButton = screeningLayout.findViewById(R.id.rightPopUpBtn);

                                leftButton.setText("Batal");
                                rightButton.setText("Lanjutkan");
                                tvPickupId.setText("Detail Pickup Id");

                                leftButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        window.dismiss();
                                    }
                                });
                                window.showAtLocation(screeningLayout, Gravity.CENTER, 0, 0);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                });
            }
        });



//        Request request = new Request.Builder().url(PathUrl.ROOT_PATH_TRASH_TYPE).get().build();
//        okHttpClient.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        ProgressBarHelper.onProgress(activity.getApplication(), view, false);
//                    }
//                });
//                failedConnectToServer(R.string.failed_con_server);
//            }
//
//            @Override
//            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        ProgressBarHelper.onProgress(activity.getApplication(), view, false);
//                        onResponseTrashMenuSuccess(
//                            activity,
//                            response,
//                            view,
//                            okHttpClient
//                        );
//                    }
//                });
//            }
//        });

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

                ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, R.layout.trash_list_dialog_dropdown, trashTypeNames);
                Spinner listTrashType = inflateTrashLay.findViewById(R.id.trashTypeLists);
                TextView trashAmount = inflateTrashLay.findViewById(R.id.trashAmount);
                Button cancel = inflateTrashLay.findViewById(R.id.cancelTrashProcess);
                Button storeTrashesButton = inflateTrashLay.findViewById(R.id.processStoreTrash);
                ManualImageChooser manualImageChooser = new ManualImageChooser(this);

                trashPhoto = inflateTrashLay.findViewById(R.id.trashPhoto);
                imageSetterFromStream.setAsImageDrawable("defImage.png", trashPhoto);

                trashPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        manualImageChooser.setParent((ViewGroup) trashPhoto.getParent().getParent().getParent());
                        manualImageChooser.startChooser(new SuccessSetImage() {
                            @Override
                            public void success(Bitmap bitmap, String bitmapLoc, ViewGroup parentFileChooser) {
                                trashPathPhotoLoc = bitmapLoc;
                                trashPhoto.setImageBitmap(bitmap);
                                manualImageChooser.closeImageChooser(parentFileChooser);
                            }
                        });
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        trashPathPhotoLoc = null;
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
                            trashPathPhotoLoc = null;
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
                        }
                    }
                });
            }
        });
    }
}
