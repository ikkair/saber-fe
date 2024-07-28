package com.desti.saber;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.view.*;
import android.widget.*;

import androidx.core.app.ActivityCompat;
import com.desti.saber.LayoutHelper.CustomFileChooser.CustomFileChooser;
import com.desti.saber.LayoutHelper.CustomFileChooser.SuccessSetImage;
import com.desti.saber.LayoutHelper.FailedNotif.FailServerConnectToast;
import com.desti.saber.LayoutHelper.GreetingDecoDashboard.GreetingDecoDashboard;
import com.desti.saber.LayoutHelper.ProgressBar.ProgressBarHelper;
import com.desti.saber.LayoutHelper.UserAccountDetails.UserAccountDetails;
import com.desti.saber.LayoutHelper.UserDetails.UserDetails;
import com.desti.saber.utils.GPSTrackerHelper;
import com.desti.saber.utils.IDRFormatCurr;
import com.desti.saber.utils.ImageSetterFromStream;
import com.desti.saber.utils.constant.PathUrl;
import com.desti.saber.utils.constant.UserDetailKeys;
import com.desti.saber.utils.dto.PickupDetailDto;
import com.desti.saber.utils.dto.ResponseGlobalJsonDTO;
import com.desti.saber.utils.dto.TrashType;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
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

public class UserActivity extends CommonObject{

    private FailServerConnectToast failServerConnectToast;
    private MainDashboard mainDashboard;
    private String tempLocPickup;
    private String finalLocPickUp;
    private ImageView trashPhoto;
    private String trashPathPhotoLoc;
    private String trashTypeSelectedId;
    private String token;
    private String userId;
    private String notFoundLoc;
    private boolean disableBackPress = false;
    private View trashDeliverOnClickBtn;
    private String password;
    private SharedPreferences sharedPreferences;
    private GeoPoint geoPoint;

    public UserActivity(Activity activity, ViewGroup rootView, UserDetails userDetails) {
        super(activity, rootView, userDetails);

        getActivity().getLayoutInflater().inflate(R.layout.activity_user_dashboard, rootView);
        mainDashboard = new MainDashboard();
    }

    public void onCreate() {
        password = getSharedPreferences().getString(UserDetailKeys.PASSWORD_KEY, null);
        userId = getSharedPreferences().getString(UserDetailKeys.USER_ID_KEY, null);
        token = getSharedPreferences().getString(UserDetailKeys.TOKEN_KEY, null);
        failServerConnectToast = new FailServerConnectToast(getActivity());
        mainDashboard.setFailServerConnectToast(failServerConnectToast);
        notFoundLoc = getActivity().getString(R.string.not_found_loc);

        ImageSetterFromStream isfs = new ImageSetterFromStream(getActivity());
        LinearLayout withdrawLabelClickable = getActivity().findViewById(R.id.withdrawLabelClickable);
        LinearLayout detailAccountClickable = getActivity().findViewById(R.id.detailAccountClickable);
        LinearLayout trashDeliverClickable = getActivity().findViewById(R.id.trashDeliverClickable);
        LinearLayout pinPointLocClickable = getActivity().findViewById(R.id.pinPointLocClickable);
        GreetingDecoDashboard greetingDecoDashboard = new GreetingDecoDashboard(getActivity(), getActivity().findViewById(R.id.decoContainer));

        greetingDecoDashboard.show();
        //set pinpoint loc title
        this.setPinPointLocTitle("Tidak Ada Lokasi Dipilih");

        isfs.setAsImageDrawable("withdraw_icon.png", R.id.withdrawLabelIcon);
        isfs.setAsImageDrawable("account_detail_icon.png", R.id.detailLabelIcon);
        isfs.setAsImageDrawable("trash_deliver_icon.png", R.id.trashDeliverIcon);
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
                UserAccountDetails userAccountDetails = new UserAccountDetails(getUserDetails(), getActivity(), sharedPreferences);
                userAccountDetails.show(v);
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
    }

    private void withdrawLabelOnClick(){
        Intent withDrawIntent = new Intent(getActivity().getApplicationContext(), WithdrawActivity.class);
        withDrawIntent.putExtra(UserDetailKeys.PICK_LOCATION_KEY, finalLocPickUp);
        getActivity().startActivity(withDrawIntent);
    }

    private void pinPointLocOnClick(){
        int checkPermissionFineLoc = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
        int checkPermissionCoarse = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionGrantedCode = PackageManager.PERMISSION_GRANTED;

        if (checkPermissionFineLoc != permissionGrantedCode &&
            checkPermissionCoarse != permissionGrantedCode) {

            String[] permissionList = {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            };

            ActivityCompat.requestPermissions(
                getActivity(),
                permissionList,
                PackageManager.PERMISSION_GRANTED
            );
        }else {
            try {
                Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
                View layoutInflater = getActivity().getLayoutInflater().inflate(R.layout.maps_pop_up_layout, null);
                PopupWindow popupWindow = new PopupWindow(layoutInflater, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                Button cancelButton = layoutInflater.findViewById(R.id.cancelSetLoc);
                GPSTrackerHelper gpsTrackerHelper = new GPSTrackerHelper(getActivity());
                MapView mapView = layoutInflater.findViewById(R.id.mapsSetLoc);
                EditText searchMapField = popupWindow.getContentView().findViewById(R.id.searchMapField);
                GeoPoint geoPoint = new GeoPoint(
                    gpsTrackerHelper.getLatitude(),
                    gpsTrackerHelper.getLongitude()
                );
                Marker markerPin = new Marker(mapView);

                Overlay overlay = new Overlay(getActivity()) {
                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent e, MapView mapView) {
                        try {
                            Projection proj = mapView.getProjection();
                            GeoPoint loc = (GeoPoint) proj.fromPixels((int) e.getX(), (int) e.getY());
                            setGeoPoint(geoPoint);
                            getGeocoder(loc, popupWindow, markerPin, mapView);
                        } catch (IOException ex) {
                            ex.printStackTrace();
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
                            if (keyCode == 66) {
                                String value = ((EditText) v).getText().toString();
                                if (!value.equals("")) {
                                    searchMapField.setEnabled(false);
                                    Geocoder geocoder = new Geocoder(popupWindow.getContentView().getContext(), Locale.getDefault());
                                    List<Address> addressList = geocoder.getFromLocationName(value, 1000);

                                    if (addressList.size() > 0) {
                                        Address address = addressList.get(0);
                                        GeoPoint geoPointSearch = new GeoPoint(address.getLatitude(), address.getLongitude());
                                        setGeoPoint(geoPoint);
                                        getGeocoder(geoPointSearch, popupWindow, markerPin, mapView);
                                    } else {
                                        Toast.makeText(popupWindow.getContentView().getContext(), "Lokasi Tidak Ditemukan", Toast.LENGTH_LONG).show();
                                    }

                                    searchMapField.setEnabled(true);
                                    return true;
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                });

                getGeocoder(geoPoint, popupWindow, markerPin, mapView);
                popupWindow.setFocusable(true);
                popupWindow.showAtLocation(layoutInflater, Gravity.CENTER, 0, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getGeocoder(GeoPoint loc, PopupWindow popupWindow, Marker marker, MapView mapView) throws IOException{
        setGeoPoint(geoPoint);

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
                    setGeoPoint(geoPoint);
                }
            }

            if(listGeocoder.size() == 0){
                locByPin.setText(notFoundLoc);
                adapter.add(notFoundLoc);
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
                        setGeoPoint(geoPoint);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            setLoc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(tempLocPickup == null){
                        Toast.makeText(v.getContext(), R.string.not_found_loc, Toast.LENGTH_SHORT).show();
                        return;
                    }

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
        trashDeliverOnClickBtn = view;

        if ((finalLocPickUp == null || finalLocPickUp.equals(notFoundLoc)) && geoPoint == null) {
            Toast.makeText(getActivity(), R.string.loc_details, Toast.LENGTH_LONG).show();
            return;
        }

        disableBackPress = true;
        ProgressBarHelper.onProgress(getActivity(), view, true);
        OkHttpClient okHttpClient = new OkHttpClient();
        Request getAllPickup = new Request.Builder().get().url(PathUrl.ROOT_PATH_PICKUP).build();

        okHttpClient.newCall(getAllPickup).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        disableBackPress = false;
                        ProgressBarHelper.onProgress(getActivity(), view, false);
                        failServerConnectToast.show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProgressBarHelper.onProgress(getActivity(), view, false);
                        try {
                            if (response.isSuccessful() || response.code() == 404) {
                                if (response.body() != null) {
                                    String responseBody = response.body().string();
                                    TypeToken<ResponseGlobalJsonDTO<PickupDetailDto>> jsonDTOTypeToken = new TypeToken<ResponseGlobalJsonDTO<PickupDetailDto>>() {
                                    };
                                    ResponseGlobalJsonDTO<PickupDetailDto> jsonDTO = new Gson().fromJson(responseBody, jsonDTOTypeToken);
                                    List<PickupDetailDto> pickupDetailDtos = new ArrayList<>(Arrays.asList(jsonDTO.getData()));
                                    HashMap<String, PickupDetailDto> pickupIdEditedStatus = new HashMap<>();
                                    List<String> pickupId = new ArrayList<>();

                                    for (int i = 0; i < pickupDetailDtos.size(); i++) {
                                        PickupDetailDto singlePickupDetail = pickupDetailDtos.get(i);

                                        if (singlePickupDetail.getUser_id().equals(userId) && singlePickupDetail.getStatus().equals("editing")) {
                                            pickupIdEditedStatus.put(singlePickupDetail.getId(), singlePickupDetail);
                                            pickupId.add(singlePickupDetail.getId());
                                        }
                                    }

                                    trashStorePickupIdScreening(
                                        getActivity(),
                                        okHttpClient,
                                        view,
                                        pickupIdEditedStatus,
                                        pickupId
                                    );
                                    return;
                                }
                            }

                            failServerConnectToast.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void trashStorePickupIdScreening(Activity activity, OkHttpClient okHttpClient, View view,  HashMap<String, PickupDetailDto> pickupIdKey, List<String> pickupId){
        View screeningLayout = getActivity().getLayoutInflater().inflate(R.layout.user_pickup_screening_pop_up, null);
        PopupWindow window = new PopupWindow(screeningLayout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        View windowContentView = window.getContentView();
        View wrapperPickupId = windowContentView.findViewById(R.id.wrapperPickupID);
        Button requestPickupTrash = windowContentView.findViewById(R.id.requestPickupTrash);
        Button cancelEditPickId = windowContentView.findViewById(R.id.cancelEditPickId);
        Button closePickupWindow = windowContentView.findViewById(R.id.cancelPickId);
        Button addNewPickupId = windowContentView.findViewById(R.id.addNewPickupId);
        Button editPickupId = windowContentView.findViewById(R.id.editPickupId);
        Button saveEdited = windowContentView.findViewById(R.id.saveEdited);
        Button addNewTrash = windowContentView.findViewById(R.id.addNewTrash);
        EditText pickupDate = windowContentView.findViewById(R.id.pickupDate);
        Spinner pickupIdList = windowContentView.findViewById(R.id.pickupIdOption);
        EditText locPickUp = windowContentView.findViewById(R.id.pickupLocationEditable);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
            activity, R.layout.trash_list_dialog_dropdown
        );

        arrayAdapter.add("Buat Pickup Id Baru");
        arrayAdapter.addAll(pickupId);
        pickupIdList.setAdapter(arrayAdapter);

        editPickupId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                requestPickupTrash.setVisibility(View.GONE);
                addNewTrash.setVisibility(View.GONE);
                cancelEditPickId.setVisibility(View.VISIBLE);
                saveEdited.setVisibility(View.VISIBLE);

                disableEditText(pickupIdList, false);
                disableEditText(pickupDate, true);
                disableEditText(locPickUp, true);
            }
        });

        pickupIdList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    locPickUp.setText(finalLocPickUp);
                    pickupDate.setText(new Date(System.currentTimeMillis()).toString());

                    addNewPickupId.setVisibility(View.VISIBLE);
                    requestPickupTrash.setVisibility(View.GONE);
                    addNewTrash.setVisibility(View.GONE);
                    editPickupId.setVisibility(View.GONE);

                    disableEditText(pickupDate, true);
                    disableEditText(locPickUp, true);
                }else{
                    disableEditText(pickupDate, false);
                    disableEditText(locPickUp, false);

                    PickupDetailDto singleDtoDetail = pickupIdKey.get(pickupIdList.getSelectedItem());
                    pickupDate.setText(singleDtoDetail.getTime());
                    locPickUp.setText(singleDtoDetail.getAddress());

                    addNewPickupId.setVisibility(View.GONE);
                    addNewTrash.setVisibility(View.VISIBLE);
                    editPickupId.setVisibility(View.VISIBLE);
                    requestPickupTrash.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addNewPickupId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String pickUpDate = pickupDate.getText().toString();
               String editableLocVal = locPickUp.getText().toString();
               int selectedPickId = pickupIdList.getSelectedItemPosition();

               if(selectedPickId == 0){
                   createPickupId(
                       okHttpClient,
                       window,
                       activity,
                       editableLocVal,
                       pickUpDate,
                       addNewPickupId
                   );
               }
            }
        });

        closePickupWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableBackPress = false;
                window.dismiss();
            }
        });

        cancelEditPickId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                requestPickupTrash.setVisibility(View.VISIBLE);
                addNewTrash.setVisibility(View.VISIBLE);
                editPickupId.setVisibility(View.VISIBLE);
                saveEdited.setVisibility(View.GONE);

                disableEditText(pickupIdList, true);
                disableEditText(pickupDate, false);
                disableEditText(locPickUp, false);
            }
        });

        saveEdited.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        HashMap<String, String> requestList = new HashMap<>();
                        String pickUpDate = pickupDate.getText().toString();
                        String editableLocVal = locPickUp.getText().toString();

                        requestList.put("address", editableLocVal);
                        requestList.put("time", pickUpDate);

                        String finalJsonPayload = new Gson().toJson(requestList);
                        RequestBody requestBody = RequestBody.create(finalJsonPayload, MediaType.parse("application/json"));
                        Request requestCreatePickupId = new Request
                        .Builder().put(requestBody)
                        .header("Authorization", "Bearer " + token)
                        .url(PathUrl.ROOT_PATH_PICKUP + "/" +  pickupIdList.getSelectedItem().toString()).build();

                        cancelEditPickId.setVisibility(View.GONE);
                        ProgressBarHelper.onProgress(getActivity(), v, true);
                        okHttpClient.newCall(requestCreatePickupId).enqueue(new Callback() {
                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        cancelEditPickId.setVisibility(View.VISIBLE);
                                        ProgressBarHelper.onProgress(getActivity(), v, false);

                                        if(response.isSuccessful()){
                                            PickupDetailDto newDetailsPickupId = pickupIdKey.get(pickupIdList.getSelectedItem());

                                            newDetailsPickupId.setAddress(locPickUp.getText().toString());
                                            newDetailsPickupId.setTime(pickupDate.getText().toString());
                                            pickupIdKey.put(newDetailsPickupId.getId(), newDetailsPickupId);
                                            Toast.makeText(getActivity(), "Berhasil Memperbarui Detail Puickup", Toast.LENGTH_LONG).show();
                                        }
                                    }

                                });
                            }

                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        cancelEditPickId.setVisibility(View.VISIBLE);
                                        ProgressBarHelper.onProgress(getActivity(), v, false);
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });

        addNewTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableEditText(pickupIdList, false);
                editPickupId.setVisibility(View.GONE);
                requestPickupTrash.setVisibility(View.GONE);
                ProgressBarHelper.onProgress(getActivity(), v, true);
                Request request = new Request.Builder().url(PathUrl.ROOT_PATH_TRASH_TYPE).get().build();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                disableEditText(pickupIdList, true);
                                editPickupId.setVisibility(View.VISIBLE);
                                requestPickupTrash.setVisibility(View.VISIBLE);
                                ProgressBarHelper.onProgress(getActivity(), v, false);
                            }
                        });
                        failServerConnectToast.show();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                onResponseTrashMenuSuccess(
                                    activity,
                                    response,
                                    view,
                                    okHttpClient,
                                    String.valueOf(pickupIdList.getSelectedItem()),
                                    wrapperPickupId
                                );

                                disableEditText(pickupIdList, true);
                                editPickupId.setVisibility(View.VISIBLE);
                                requestPickupTrash.setVisibility(View.VISIBLE);
                                ProgressBarHelper.onProgress(getActivity(), v, false);
                            }
                        });
                    }
                });
            }
        });

        requestPickupTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPickupId.setVisibility(View.GONE);
                addNewTrash.setVisibility(View.GONE);
                disableEditText(pickupIdList, false);
                ProgressBarHelper.onProgress(getActivity(), v, true);

                HashMap<String, String> requestList = new HashMap<>();
                requestList.put("status", "waiting");
                requestList.put("pickup_id", pickupIdList.getSelectedItem().toString());

                String finalJsonPayload = new Gson().toJson(requestList);
                RequestBody requestBody = RequestBody.create(finalJsonPayload, MediaType.parse("application/json"));
                Request requestPickupTrash = new Request
                .Builder().put(requestBody)
                .header("Authorization", "Bearer " + token)
                .url(PathUrl.ROOT_PATH_PICKUP+ "/user/status").build();

                okHttpClient.newCall(requestPickupTrash).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                editPickupId.setVisibility(View.VISIBLE);
                                addNewTrash.setVisibility(View.VISIBLE);
                                disableEditText(pickupIdList, true);
                                ProgressBarHelper.onProgress(getActivity(), v, false);

                                Toast.makeText(getActivity(), "Gagal Melakukan Request Pickup Sampah", Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(response.isSuccessful()){
                                    Toast.makeText(getActivity(), "Sukses Melakukan Request Pickup Sampah", Toast.LENGTH_LONG).show();
                                    window.dismiss();
                                }else{
                                    editPickupId.setVisibility(View.VISIBLE);
                                    addNewTrash.setVisibility(View.VISIBLE);
                                    disableEditText(pickupIdList, true);
                                    ProgressBarHelper.onProgress(getActivity(), v, false);

                                    if(response.code() == 403){
                                        Toast.makeText(getActivity(), "Silahkan Tambahkan Sampah Terlebih Dahulu", Toast.LENGTH_LONG).show();
                                    }else{
                                        Toast.makeText(getActivity(), "Gagal Melakuan Request Pickup Sampah", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        });
                    }
                });
            }
        });

        window.setFocusable(true);
        window.showAtLocation(screeningLayout, Gravity.CENTER, 0, 0);
    }

    private void createPickupId(OkHttpClient okHttpClient, PopupWindow window, Activity activity, String editableLocVal, String pickUpDate, View addNewPickupIdBtn){
        if(pickUpDate.equals("") || editableLocVal.equals("")) {
            Toast.makeText(getActivity(), "Silahkan  Lengkapi Tanggal Serta Lokasi Pickup", Toast.LENGTH_LONG).show();
            return;
        }

        addNewPickupIdBtn.setVisibility(View.GONE);
        ProgressBarHelper.onProgress(getActivity(), addNewPickupIdBtn, true);

        String latitude = String.valueOf(geoPoint.getLatitude());
        String longitude = String.valueOf(geoPoint.getLongitude());
        HashMap<String, String> requestList = new HashMap<>();

        requestList.put("address", editableLocVal);
        requestList.put("user_id", userId);
        requestList.put("time", pickUpDate);
        requestList.put("latitude", latitude);
        requestList.put("longitude", longitude);

        System.out.println(latitude + " < == > " + longitude);

        String finalJsonPayload = new Gson().toJson(requestList);
        RequestBody requestBody = RequestBody.create(finalJsonPayload, MediaType.parse("application/json"));
        Request requestCreatePickupId = new Request
        .Builder().post(requestBody)
        .header("Authorization", "Bearer " + token)
        .url(PathUrl.ROOT_PATH_PICKUP).build();

        okHttpClient.newCall(requestCreatePickupId).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        addNewPickupIdBtn.setVisibility(View.VISIBLE);
                        ProgressBarHelper.onProgress(getActivity(), addNewPickupIdBtn, false);
                        failServerConnectToast.show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProgressBarHelper.onProgress(getActivity(), addNewPickupIdBtn, false);

                        if(response.isSuccessful()){
                            addNewPickupIdBtn.setVisibility(View.VISIBLE);
                            window.dismiss();
                            Toast.makeText(getActivity(), "Pikcup Id Berhasil Dibuat, Tunggu Sesaat..", Toast.LENGTH_SHORT).show();

                            if(trashDeliverOnClickBtn != null){
                                trashDeliverOnClick(trashDeliverOnClickBtn);
                            }
                        } else {
                            Toast.makeText(getActivity(), "Pikcup Id Gagal Dibuat", Toast.LENGTH_SHORT).show();
                            try {
                                System.out.println(response.body().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
    }

    private void setPinPointLocTitle(String location){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int maxLocLength = 25;
                String newLocation = (location.length() > maxLocLength) ? location.substring(0, maxLocLength) + "..." : location;
                ((TextView) getActivity().findViewById(R.id.pinPointLocTitle)).setText(newLocation);
            }
        });
    }

    private void onResponseTrashMenuSuccess(Activity activity, Response response, View view, OkHttpClient okHttpClient, String pickupId, View wrapperPickupId){
        ImageSetterFromStream imageSetterFromStream = new ImageSetterFromStream(getActivity());
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

                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.trash_list_dialog_dropdown, trashTypeNames);
                Spinner listTrashType = inflateTrashLay.findViewById(R.id.trashTypeLists);
                TextView trashAmount = inflateTrashLay.findViewById(R.id.trashAmount);
                Button cancel = inflateTrashLay.findViewById(R.id.cancelTrashProcess);
                Button storeTrashesButton = inflateTrashLay.findViewById(R.id.processStoreTrash);
                CustomFileChooser customFileChooser = new CustomFileChooser(getActivity());

                trashPhoto = inflateTrashLay.findViewById(R.id.trashPhoto);
                imageSetterFromStream.setAsImageDrawable("defImage.png", trashPhoto);

                trashPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customFileChooser.setParent((ViewGroup) trashPhoto.getParent().getParent().getParent());
                        customFileChooser.startImageChooser(new SuccessSetImage() {
                            @Override
                            public void success(Bitmap bitmap, String bitmapLoc, ViewGroup parentFileChooser) {
                                trashPathPhotoLoc = bitmapLoc;
                                trashPhoto.setImageBitmap(bitmap);
                                customFileChooser.closeCustomChooser(parentFileChooser);
                            }
                        });
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        wrapperPickupId.setVisibility(View.VISIBLE);
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
                            v, cancel,
                            popupWindow,
                            pickupId,
                            wrapperPickupId
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
                wrapperPickupId.setVisibility(View.GONE);
            }catch (Exception e){
                failServerConnectToast.show();
            }
        }else{
            failServerConnectToast.show(R.string.unavailable_service);
        }
    }

    private void doPostStoreTrash(ViewGroup inflater, OkHttpClient okHttpClient, View postBtn, View cancelBtn, PopupWindow popupWindow, String pickupId, View wrapperPickupId){
        if(token == null){
            Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
            getActivity().finishAffinity();
            getActivity().finish();
            getActivity().startActivity(intent);
            return;
        }

        String trashDesc = String.valueOf(((EditText) inflater.findViewById(R.id.trashDesc)).getText());
        String trashWeight = String.valueOf(((EditText)  inflater.findViewById(R.id.trashWeight)).getText());

        if(trashDesc.equals("") || trashWeight.equals("") || trashTypeSelectedId == null || trashPathPhotoLoc == null){
            Toast.makeText(getActivity().getApplicationContext(), R.string.empty_field, Toast.LENGTH_LONG).show();
            return;
        }

        cancelBtn.setVisibility(View.GONE);
        ProgressBarHelper.onProgress(getActivity(), postBtn, true);

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
        .addFormDataPart("pickup_id", pickupId)
        .addFormDataPart("description", trashDesc)
        .build();

        Request uploadTrashRequest = new Request.Builder()
        .header("Authorization", "Bearer " + token)
        .post(requestBody)
        .url(PathUrl.ROOT_PATH_TRASH)
        .build();

        okHttpClient.newCall(uploadTrashRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cancelBtn.setVisibility(View.VISIBLE);
                        ProgressBarHelper.onProgress(getActivity(), postBtn, false);
                        Toast.makeText(
                        getActivity().getApplicationContext(),
                            R.string.err_network,
                            Toast.LENGTH_LONG
                        ).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cancelBtn.setVisibility(View.VISIBLE);
                        ProgressBarHelper.onProgress(getActivity(), postBtn, false);

                        if(response.isSuccessful()){
                            trashPathPhotoLoc = null;
                            wrapperPickupId.setVisibility(View.VISIBLE);
                            popupWindow.dismiss();
                            Toast.makeText(
                            getActivity().getApplicationContext(),
                            getActivity().getString(R.string.success_trash_store) + " Dengan Pickup Id " + pickupId,
                                Toast.LENGTH_LONG
                            ).show();
                        }else{
                            Toast.makeText(
                                getActivity().getApplicationContext(),
                                R.string.fail_trash_store,
                                Toast.LENGTH_LONG
                            ).show();
                        }
                    }
                });
            }
        });
    }

    private void disableEditText(View view, boolean mode){
        view.setFocusable(mode);
        view.setEnabled(mode);
        view.setFocusableInTouchMode(mode);
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }
}
