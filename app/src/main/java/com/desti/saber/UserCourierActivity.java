package com.desti.saber;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.desti.saber.LayoutHelper.GreetingDecoDashboard.GreetingDecoDashboard;
import com.desti.saber.LayoutHelper.PickupTrashes.PickupTrashesList;
import com.desti.saber.LayoutHelper.UserAccountDetails.UserAccountDetails;
import com.desti.saber.LayoutHelper.UserDetails.UserDetails;
import com.desti.saber.configs.OkHttpHandler;
import com.desti.saber.utils.GPSLocationChanged;
import com.desti.saber.utils.GPSTrackerHelper;
import com.desti.saber.utils.constant.UserDetailKeys;
import com.desti.saber.utils.dto.PickupDetailDto;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;

public class UserCourierActivity extends CommonObject{

    private SharedPreferences sharedPreferences;
    private LinearLayout rootContainer;
    private ViewGroup rootViewContainerActivity;
    private PickupDetailDto pickupDetailDto;

    public UserCourierActivity(Activity activity, ViewGroup rootView, UserDetails userDetails) {
        super(activity, rootView, userDetails);
    }

    protected void onCreate() {
        rootViewContainerActivity = (ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.activity_courier, getRootView());
        GreetingDecoDashboard greetingDecoDashboard = new GreetingDecoDashboard(getActivity(), getActivity().findViewById(R.id.decoContainerCourier));
        UserAccountDetails userAccountDetails = new UserAccountDetails(getUserDetails(), getActivity(), getSharedPreferences());
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
    }

    private void courierTaskPickupOnClicked(View view){
        clearMenu();

        UserAdminActivity userAdminActivity = new UserAdminActivity(getActivity(), getRootView(), getUserDetails());
        OkHttpHandler okHttpHandler = new OkHttpHandler();

        userAdminActivity.setRootViewContainer(rootContainer);
        userAdminActivity.baseTrashPickupRequest(view, okHttpHandler, new BaseTrashPickupOnClick() {
            @Override
            public void onClickPickupBtn(View v, PickupTrashesList pickupTrashesList, PickupDetailDto pickupDetailDto) {
                setPickupDetailDto(pickupDetailDto);
                pickupTrashesList.close();
                pickupTrashesList.getReportDownload().closeReport();
                pickupLocationOnClicked(v);
            }

            @Override
            public void pickupPopUpList(PickupTrashesList pickupTrashesList) {
                pickupTrashesList.visibilityPickupButton(true);
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
            Button focusPosition = getActivity().findViewById(R.id.centerLocPoint);
            MapView mapView = getActivity().findViewById(R.id.mapsViewPickup);
            Marker markerPinStart = new Marker(mapView);
            Marker makerPinEnd = new Marker(mapView);

            pickupLocation.setText(pickupDetailDto.getAddress());

            markerPinStart.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            markerPinStart.setTitle(sharedPreferences.getString(UserDetailKeys.USERNAME_KEY, ""));

            mapView.setMultiTouchControls(true);
            mapView.getOverlays().add(markerPinStart);
            mapView.getController().setZoom(18.0);

            gpsTrackerHelper.onLocationChanged(new GPSLocationChanged() {
                @Override
                public void onLocationChanged(Location location) {
                    GeoPoint endPoint = new GeoPoint(
                        Double.parseDouble(pickupDetailDto.getLatitude()),
                        Double.parseDouble(pickupDetailDto.getLongitude())
                    );
                    GeoPoint startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    RoadManager roadManager = new OSRMRoadManager(getActivity());
                    ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();

                    waypoints.add(startPoint);
                    waypoints.add(endPoint);

                    Polyline polyline= RoadManager.buildRoadOverlay(roadManager.getRoad(waypoints));

                    polyline.setWidth(2);
                    polyline.setColor(Color.RED);
                    polyline.setGeodesic(true);
                    polyline.usePath(true);

                    mapView.getOverlays().add(polyline);
                    markerPinStart.setPosition(startPoint);
                    mapView.getController().setCenter(startPoint);
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
        }
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