package com.desti.saber;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.desti.saber.LayoutHelper.FailedNotif.FailServerConnectToast;
import com.desti.saber.LayoutHelper.GreetingDecoDashboard.GreetingDecoDashboard;
import com.desti.saber.LayoutHelper.UserDetails.UserDetails;
import com.desti.saber.configs.OkHttpHandler;
import com.desti.saber.utils.ImageSetterFromStream;
import com.desti.saber.LayoutHelper.UserDetails.GetImageProfileCallback;
import com.desti.saber.utils.constant.UserDetailKeys;
import okhttp3.*;

import java.io.InputStream;

public class MainDashboard extends AppCompatActivity {

    private FailServerConnectToast failServerConnectToast;
    private ImageView iconProfileImage;
    private UserDetails userDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dashboard);
        failServerConnectToast = new FailServerConnectToast((Activity) getWindow().getContext());
        iconProfileImage = findViewById(R.id.profileImage);

        SharedPreferences loginInfo = getSharedPreferences(UserDetailKeys.SHARED_PREF_LOGIN_KEY, Context.MODE_PRIVATE);
        String userPassword = loginInfo.getString(UserDetailKeys.PASSWORD_KEY, null);
        String userRole = loginInfo.getString(UserDetailKeys.USER_ROLE_NAME, null);
        String userToken = loginInfo.getString(UserDetailKeys.TOKEN_KEY, null);
        String userId = loginInfo.getString(UserDetailKeys.USER_ID_KEY, null);
        LinearLayout mainDashboardContainer = findViewById(R.id.dashboardMainWrapper);
        ImageSetterFromStream isfs = new ImageSetterFromStream(this);
        GreetingDecoDashboard greetingDecoDashboard = new GreetingDecoDashboard(this, mainDashboardContainer);

        userDetails = new UserDetails(userId);

        if(userRole.equalsIgnoreCase("user")){
            UserActivity userActivity = new UserActivity(this, mainDashboardContainer, userDetails);
            userActivity.setSharedPreferences(loginInfo);
            userActivity.onCreate();
        } else if(userRole.equalsIgnoreCase("admin")){
            UserAdminActivity userAdminActivity = new UserAdminActivity(this, mainDashboardContainer, userDetails);
            userAdminActivity.setLoginInfo(loginInfo);
            greetingDecoDashboard.show();
            userAdminActivity.onCreate();
        } else if (userRole.equalsIgnoreCase("courier")) {
            UserCourierActivity userCourierActivity = new UserCourierActivity(this, mainDashboardContainer, userDetails);
            userCourierActivity.setSharedPreferences(loginInfo);
            userCourierActivity.onCreate();
        } else {
            System.exit(0);
        }

        setUserNameTittle(loginInfo.getString(UserDetailKeys.USERNAME_KEY, "Cannot get the name"), this);
        isfs.setAsImageDrawable("def_user_profile.png", iconProfileImage);

        getImageProfile(
            loginInfo.getString("photo", null),
            this,
            new GetImageProfileCallback() {
                @Override
                public void ioNetworkFail(Exception e) {
                    failServerConnectToast.show();
                }

                @Override
                public void success(Bitmap bitmap) {
                    setImageProfile(((Activity) getWindow().getContext()), bitmap);
                }

                @Override
                public void endPointFaultResponse(Response response) {
                    failServerConnectToast.show();
                }
            }
        );
    }

    public void setUserNameTittle(String userName, Activity activity){
        MainDashboard.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int maxUserNameLength = 25;
                String newUserName = (userName.length() > maxUserNameLength) ? userName.substring(0, maxUserNameLength) + "..." : userName;
                ((TextView) activity.findViewById(R.id.userNameLabelWithdraw)).setText(newUserName);
            }
        });
    }

    public void getImageProfile(String photoId, Activity activity, GetImageProfileCallback callback){
        if(photoId != null){
            OkHttpHandler okHttpHandler = new OkHttpHandler();
            String uriGetPhoto = photoId.replace("/view", "");
            Request photoProfileRequest = new Request.Builder()
                    .url("https://drive.usercontent.google.com/download?id=".concat(uriGetPhoto))
                    .build();

            okHttpHandler.requestAsync(activity, photoProfileRequest, new OkHttpHandler.MyCallback() {
                @Override
                public void onSuccess(Context context, Response response) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    try{
                                        final InputStream responseStream = response.body().byteStream();
                                        Bitmap bitmap = BitmapFactory.decodeStream(responseStream);
                                        callback.success(bitmap);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                        Toast.makeText(activity, "Gagal Melakukan Pengambilan Foto Profile", Toast.LENGTH_LONG).show();
                                    }
                                }
                            } else {
                                callback.endPointFaultResponse(response);
                            }
                        }
                    });
                }

                @Override
                public void onFailure(Exception e) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            failServerConnectToast.show();
                            callback.ioNetworkFail(e);
                        }
                    });
                }
            });
        }
    }

    public void setImageProfile(Activity activity, Bitmap imageProfile){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((ImageView) activity.findViewById(R.id.profileImage)).setImageBitmap(imageProfile);
            }
        });
    }

    public void setFailServerConnectToast(FailServerConnectToast failServerConnectToast) {
        this.failServerConnectToast = failServerConnectToast;
    }

    @Override
    public void onBackPressed() {

    }
}