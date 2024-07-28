package com.desti.saber;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import android.widget.*;
import com.desti.saber.LayoutHelper.FailedNotif.FailServerConnectToast;
import com.desti.saber.LayoutHelper.GreetingDecoDashboard.GreetingDecoDashboard;
import com.desti.saber.LayoutHelper.PickupTrashes.PickupTrashesList;
import com.desti.saber.LayoutHelper.ProgressBar.ProgressBarHelper;
import com.desti.saber.LayoutHelper.SinglePickupDetail.SinglePickupDetail;
import com.desti.saber.LayoutHelper.UserAccountDetails.UserAccountDetails;
import com.desti.saber.LayoutHelper.UserDetails.UserDetails;
import com.desti.saber.configs.OkHttpHandler;
import com.desti.saber.data.Result;
import com.desti.saber.utils.GPSLocationChanged;
import com.desti.saber.utils.GPSTrackerHelper;
import com.desti.saber.utils.constant.PathUrl;
import com.desti.saber.utils.constant.UserDetailKeys;
import com.desti.saber.utils.dto.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class UserCourierActivity extends CommonObject{

    private SharedPreferences sharedPreferences;
    private LinearLayout rootContainer;
    private PickupDetailDto pickupDetailDto;
    private FailServerConnectToast failServerConnectToast;
    private UserAdminActivity userAdminActivity;
    private String hPickupId;

    public UserCourierActivity(Activity activity, ViewGroup rootView, UserDetails userDetails) {
        super(activity, rootView, userDetails);
    }

    protected void onCreate() {
        ViewGroup rootViewContainerActivity = (ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.activity_courier, getRootView());
        GreetingDecoDashboard greetingDecoDashboard = new GreetingDecoDashboard(getActivity(), getActivity().findViewById(R.id.decoContainerCourier));
        UserAccountDetails userAccountDetails = new UserAccountDetails(getUserDetails(), getActivity(), getSharedPreferences());
        userAdminActivity = new UserAdminActivity(getActivity(), getRootView(), getUserDetails());
        Button courierPickupTask = getActivity().findViewById(R.id.courierPickupTaskBtn);
        Button courierProfile = getActivity().findViewById(R.id.courierProfileBtn);
        Button pickupLocation = getActivity().findViewById(R.id.pickupLocationBtn);

        rootContainer = getActivity().findViewById(R.id.rootContainerCourier);
        greetingDecoDashboard.show();

        courierPickupTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                courierTaskPickupOnClicked(v);
            }
        });
        courierProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userAccountDetails.show(v);
            }
        });
        pickupLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickupLocationOnClicked(v);
            }
        });

        courierPickupTask.callOnClick();
    }

    private void courierTaskPickupOnClicked(View view){
        clearMenu();

        List<String> pickupStatus = new ArrayList<>();
        OkHttpHandler okHttpHandler = new OkHttpHandler();
        String pickupUrl = PathUrl.ROOT_PATH_PICKUP_PROGRESS.concat("/").concat(sharedPreferences.getString(UserDetailKeys.USER_ID_KEY, ""));
        Request request = new Request.Builder().get().url(pickupUrl)
                .header("Authorization", "Bearer ".concat(sharedPreferences.getString(UserDetailKeys.TOKEN_KEY, ""))).build();

        pickupStatus.add("waiting");

        ProgressBarHelper.onProgress(getActivity(), view, true);
        okHttpHandler.requestAsync(getActivity(), request, new OkHttpHandler.MyCallback() {
            @Override
            public void onSuccess(Context context, Response response) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProgressBarHelper.onProgress(getActivity(), view, false);
                        try{
                            if(response.isSuccessful() && response.body() != null){

                                String results = response.body().string();
                                TypeToken<ResponseGlobalJsonDTO<PickupProgress>> dtoTypeToken = new TypeToken<ResponseGlobalJsonDTO<PickupProgress>>(){};
                                ResponseGlobalJsonDTO<PickupProgress> responseGlobalJsonDTO = new Gson().fromJson(results, dtoTypeToken);
                                PickupProgress[] pickupProgresses = responseGlobalJsonDTO.getData();

                                if(pickupProgresses.length > 0 && pickupProgresses[0].getPickup_progress().equals("on_progress")){
                                    PickupDetailDto pickupDetailDto = pickupProgresses[0];

                                    hPickupId = pickupProgresses[0].getHelper_pickup_id();
                                    pickupDetailDto.setId(pickupProgresses[0].getPickup_id());
                                    setPickupDetailDto(pickupDetailDto);
                                    pickupLocationOnClicked(view);
                                } else {
                                    userAdminActivity.setRootViewContainer(rootContainer);
                                    userAdminActivity.baseTrashPickupRequest(view, okHttpHandler, pickupStatus, new BaseTrashPickupOnClick() {
                                        @Override
                                        public void onClickPickupBtn(View v, PickupTrashesList pickupTrashesList, PickupDetailDto pickupDetailDto) {
                                            setPickupDetailDto(pickupDetailDto);
                                            pickupTrashesList.close();
                                            pickupTrashesList.getReportDownload().closeReport();
                                            OkHttpHandler okHttpHandler = new OkHttpHandler();
                                            JsonObject jsonObject = new JsonObject();

                                            jsonObject.addProperty("pickupId", pickupDetailDto.getId());
                                            jsonObject.addProperty("courierId", sharedPreferences.getString(UserDetailKeys.USER_ID_KEY, ""));
                                            jsonObject.addProperty("userId", pickupDetailDto.getUser_id());

                                            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                                            Request request = new Request.Builder()
                                                    .post(requestBody).url(PathUrl.ROOT_PATH_PICKUP_PROGRESS)
                                                    .header("Authorization", "Bearer ".concat(sharedPreferences.getString(UserDetailKeys.TOKEN_KEY, ""))).build();

                                            okHttpHandler.requestAsync(getActivity(), request, new OkHttpHandler.MyCallback() {
                                                @Override
                                                public void onSuccess(Context context, Response response) {
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            if(response.isSuccessful() && response.body() != null){
                                                                try{
                                                                    String results = response.body().string();
                                                                    ResponseGlobalJsonDTO<PickupProgress> responseGlobalJsonDTO = new Gson().fromJson(results, dtoTypeToken);
                                                                    PickupProgress[] pickupProgresses = responseGlobalJsonDTO.getData();
                                                                    hPickupId = pickupProgresses[0].getHelper_pickup_id();

                                                                    pickupLocationOnClicked(view);
                                                                } catch (Exception e){
                                                                    e.printStackTrace();
                                                                    failServerConnectToast.show(e.getMessage());
                                                                }
                                                            }
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onFailure(Exception e) {
                                                    ProgressBarHelper.onProgress(getActivity(), view, false);
                                                    failServerConnectToast.show();
                                                }
                                            });
                                        }

                                        @Override
                                        public void pickupPopUpList(PickupTrashesList pickupTrashesList) {
                                            pickupTrashesList.visibilityPickupButton(true);
                                        }
                                    });
                                }
                            } else {
                                System.out.println(response.body().string());
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                ProgressBarHelper.onProgress(getActivity(), view, false);
                failServerConnectToast.show();
            }
        });
    }

    private void pickupLocationOnClicked(View view){
        if(pickupDetailDto == null){
            Toast.makeText(getActivity(), "Menu Tersedia Jika Kurir Sedang Pickup", Toast.LENGTH_LONG).show();
        } else {
            clearMenu();

            Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
            getActivity().getLayoutInflater().inflate(R.layout.maps_pickup_location, rootContainer);
            TextView pickupLocation = getActivity().findViewById(R.id.pickupLocationLabel);
            GPSTrackerHelper gpsTrackerHelper = new GPSTrackerHelper(getActivity());
            GeoPoint startPoint = new GeoPoint(gpsTrackerHelper.getLatitude(), gpsTrackerHelper.getLongitude());
            Button focusPosition = getActivity().findViewById(R.id.centerLocPoint);
            MapView mapView = getActivity().findViewById(R.id.mapsViewPickup);
            Button pickUpButton = getActivity().findViewById(R.id.donePickup);
            Marker markerPinStart = new Marker(mapView);
            Marker makerPinEnd = new Marker(mapView);
            GeoPoint endPoint = new GeoPoint(
                Double.parseDouble(pickupDetailDto.getLatitude()),
                Double.parseDouble(pickupDetailDto.getLongitude())
            );
            RoadManager roadManager = new OSRMRoadManager(getActivity());
            ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
            waypoints.add(startPoint);
            waypoints.add(endPoint);
            Polyline polyline= RoadManager.buildRoadOverlay(roadManager.getRoad(waypoints));


            polyline.setWidth(2);
            polyline.setColor(Color.RED);
            polyline.setGeodesic(true);
            polyline.usePath(true);
            pickupLocation.setText(pickupDetailDto.getAddress());
            markerPinStart.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            markerPinStart.setTitle(sharedPreferences.getString(UserDetailKeys.USERNAME_KEY, ""));
            mapView.setMultiTouchControls(true);
            mapView.getOverlays().add(polyline);
            mapView.getOverlays().add(markerPinStart);
            mapView.getController().setZoom(18.0);

            gpsTrackerHelper.onLocationChanged(new GPSLocationChanged() {
                @Override
                public void onLocationChanged(Location location) {
                    try{
                        GeoPoint newGeoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());

                        markerPinStart.setPosition(newGeoPoint);
                        mapView.getController().setCenter(newGeoPoint);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
            focusPosition.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gpsTrackerHelper.onLocationChanged(new GPSLocationChanged() {
                        @Override
                        public void onLocationChanged(Location location) {
                            GeoPoint newGeoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());

                            markerPinStart.setPosition(newGeoPoint);
                            mapView.getController().setCenter(newGeoPoint);
                        }
                    });

                    mapView.getController().setZoom(20);
                    Toast.makeText(getActivity(), "Diposisikan Ulang", Toast.LENGTH_LONG).show();
                }
            });
            pickUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pickupList(v);
                }
            });
        }
    }

    private void pickupList(View v){
        String url = PathUrl.ROOT_PATH_PICKUP.concat("/").concat(pickupDetailDto.getId());
        ProgressBarHelper.onProgress(getActivity(), v, true);
        Request newReq = new Request.Builder().url(url).build();
        OkHttpHandler okHttpHandler = new OkHttpHandler();
        okHttpHandler.requestAsync(getActivity(), newReq, new OkHttpHandler.MyCallback() {
            @Override
            public void onSuccess(Context context, Response response) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProgressBarHelper.onProgress(getActivity(), v, false);
                        try{
                            if(response.isSuccessful() && response.body() != null){
                                String results = response.body().string();
                                ViewGroup rootLayout = (ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.finish_pickup_layout, (ViewGroup) getActivity().getWindow().getDecorView(), false);
                                PopupWindow popupWindow = new PopupWindow(rootLayout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                                LinearLayout singlePickupDetailContainer = rootLayout.findViewById(R.id.rootSinglePickupDetail);
                                LinearLayout rootContainerTrash = rootLayout.findViewById(R.id.globalListContainer);
                                SinglePickupDetail singlePickupDetail = new SinglePickupDetail(getActivity());
                                Button closeButton = rootLayout.findViewById(R.id.closeFinishPickup);
                                Button finishPickup = rootLayout.findViewById(R.id.finishPickup);
                                CheckBox checkAll = rootLayout.findViewById(R.id.checkAllTrash);
                                List<String> acceptTrash = new ArrayList<>();
                                TypeToken<ResponseGlobalJsonDTO<TrashDetailDTO>> typeToken = new  TypeToken<ResponseGlobalJsonDTO<TrashDetailDTO>>(){};
                                ResponseGlobalJsonDTO<TrashDetailDTO> trashDetailDTOResponse = new Gson().fromJson(results, typeToken);

                                for(TrashDetailDTO trashDetailDTO : trashDetailDTOResponse.getData()){
                                    String uriGetPhoto = (trashDetailDTO.getPhoto() != null) ? "https://drive.usercontent.google.com/download?id=".concat(trashDetailDTO.getPhoto().replace("/view", "")) : "";
                                    ViewGroup singleTrashDetail = (ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.single_pickup_trash_layout, rootContainerTrash, false);
                                    ImageView trashImage = singleTrashDetail.findViewById(R.id.trashDetailImageFinal);
                                    FrameLayout progressBar = singleTrashDetail.findViewById(R.id.progressBarLoading);
                                    TextView trashName = singleTrashDetail.findViewById(R.id.trashTextSingle);
                                    CheckBox trashCheck = singleTrashDetail.findViewById(R.id.trashTextCheck);
                                    Request trashImageReq = new Request.Builder().url(uriGetPhoto).build();

                                    acceptTrash.add(trashDetailDTO.getId());

                                    if(trashDetailDTO.getPhoto() != null){
                                        okHttpHandler.requestAsync(getActivity(), trashImageReq, new OkHttpHandler.MyCallback() {
                                            @Override
                                            public void onSuccess(Context context, Response response) {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        progressBar.setVisibility(View.GONE);
                                                        try{
                                                            if(response.isSuccessful() && response.body() != null){
                                                                trashImage.setImageDrawable(Drawable.createFromStream(response.body().byteStream(), null));
                                                            }
                                                        } catch (Exception e){
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onFailure(Exception e) {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        progressBar.setVisibility(View.GONE);
                                                    }
                                                });
                                            }
                                        });
                                    }

                                    trashCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                            if(isChecked){
                                                if (!acceptTrash.contains(trashDetailDTO.getId())){
                                                    acceptTrash.add(trashDetailDTO.getId());
                                                }
                                            } else {
                                                acceptTrash.remove(trashDetailDTO.getId());
                                            }
                                        }
                                    });

                                    trashName.setText(trashDetailDTO.getDescription());
                                    rootContainerTrash.addView(singleTrashDetail);
                                }

                                checkAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        int childLength = rootContainerTrash.getChildCount();

                                        for(int i =0; i < childLength; i++){
                                            ViewGroup singleTrashView = (ViewGroup) rootContainerTrash.getChildAt(i);
                                            CheckBox singleCheckBox = singleTrashView.findViewById(R.id.trashTextCheck);

                                            if(isChecked){
                                                if(!acceptTrash.contains(trashDetailDTOResponse.getData()[i].getId())){
                                                    acceptTrash.add(trashDetailDTOResponse.getData()[i].getId());
                                                }
                                            } else {
                                                acceptTrash.remove(trashDetailDTOResponse.getData()[i].getId());
                                            }

                                            singleCheckBox.setChecked(isChecked);
                                        }
                                    }
                                });
                                closeButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        popupWindow.dismiss();
                                    }
                                });
                                finishPickup.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(acceptTrash.size() == 0){
                                            Toast.makeText(getActivity(), "Tidak Ada Sampah Dipilih", Toast.LENGTH_LONG).show();
                                        } else {
                                            RequestFinishPickupDTO requestFinishPickupDTO = new RequestFinishPickupDTO();

                                            requestFinishPickupDTO.setAcceptPickup(acceptTrash);
                                            requestFinishPickupDTO.sethPickupId(hPickupId);
                                            requestFinishPickupDTO.setPickupId(pickupDetailDto.getId());
                                            requestFinishPickupDTO.setUserId(pickupDetailDto.getUser_id());

                                            String jsonRequest = new Gson().toJson(requestFinishPickupDTO);
                                            RequestBody requestBody = RequestBody.create(jsonRequest.getBytes());
                                            Request request = new Request.Builder().post(requestBody)
                                                    .header("Content-Type", "application/json")
                                                    .header("Authorization", "Bearer ".concat(sharedPreferences.getString(UserDetailKeys.TOKEN_KEY, "")))
                                                    .url(PathUrl.ROOT_PATH_PICKUP_FINAL_PICKUP).build();

                                            closeButton.setVisibility(View.INVISIBLE);
                                            ProgressBarHelper.onProgress(getActivity(), v, true);
                                            okHttpHandler.requestAsync(getActivity(), request, new OkHttpHandler.MyCallback() {
                                                @Override
                                                public void onSuccess(Context context, Response response) {
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            closeButton.setVisibility(View.VISIBLE);
                                                            ProgressBarHelper.onProgress(getActivity(), v, false);

                                                            try {
                                                                if(response.isSuccessful() && response.body() != null){
                                                                    popupWindow.dismiss();
                                                                    clearMenu();
                                                                    hPickupId = null;
                                                                    pickupDetailDto = null;
                                                                    courierTaskPickupOnClicked(v);
                                                                    Toast.makeText(getActivity(), "Pickup Sampah Telah Selesai", Toast.LENGTH_LONG).show();
                                                                }
                                                            }catch (Exception e){
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onFailure(Exception e) {
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            closeButton.setVisibility(View.VISIBLE);
                                                            ProgressBarHelper.onProgress(getActivity(), v, false);
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    }
                                });

                                singlePickupDetail.showPickupDetail(singlePickupDetailContainer, pickupDetailDto);
                                popupWindow.showAtLocation(rootLayout, Gravity.CENTER, 0, 0);
                            } else {
                                System.out.println(response.body().string());
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                ProgressBarHelper.onProgress(getActivity(), v, false);
                failServerConnectToast.show();
            }
        });
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    private void clearMenu(){
        if(rootContainer != null){
            rootContainer.removeAllViews();
        }
    }

    public PickupDetailDto getPickupDetailDto() {
        return pickupDetailDto;
    }

    public void setPickupDetailDto(PickupDetailDto pickupDetailDto) {
        this.pickupDetailDto = pickupDetailDto;
    }
}