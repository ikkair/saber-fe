package com.desti.saber;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.desti.saber.LayoutHelper.UserAccountDetails.UserAccountDetails;
import com.desti.saber.LayoutHelper.UserDetails.UserDetails;
import com.desti.saber.utils.ImageSetterFromStream;
import com.desti.saber.utils.constant.UserDetailKeys;
import com.google.gson.JsonObject;
import okhttp3.OkHttp;
import okhttp3.OkHttpClient;

import java.util.HashMap;
import java.util.UUID;

public class UserAdminActivity {

    private final Activity activity;
    private final ViewGroup viewGroup;
    private final UserDetails userDetails;
    private LinearLayout rootViewContainer;
    private SharedPreferences loginInfo;
    private ImageSetterFromStream imageSetterFromStream;

    public UserAdminActivity(Activity activity, ViewGroup viewGroup, UserDetails userDetails) {
        this.activity = activity;
        this.viewGroup = viewGroup;
        this.userDetails = userDetails;
    }

    protected void onCreate() {
        activity.getLayoutInflater().inflate(R.layout.activity_user_admin, viewGroup);
        rootViewContainer = activity.findViewById(R.id.rootAdminContainerMenu);
        imageSetterFromStream = new ImageSetterFromStream(activity);

        String username = loginInfo.getString(UserDetailKeys.USERNAME_KEY, null);
        TextView greetingTextWelcome = activity.findViewById(R.id.adminGreetingText);
        Button trashPickupData = activity.findViewById(R.id.requestPickupTrash);
        Button registerUserBtn = activity.findViewById(R.id.registerNewUserBtn);
        Button trashInData = activity.findViewById(R.id.trashDataBtn);
        String greetingText = "Hai.. ".concat(username.toUpperCase())
        .concat(" Selamat Datang Kembali, Dimenu SABER ADMIN");
        Button userDataBtn = activity.findViewById(R.id.userDataBtn);
        Button cashOut = activity.findViewById(R.id.cashOutDataBtn);
        Button profileBtn = activity.findViewById(R.id.profileBtn);
        MainDashboard mainDashboard = new MainDashboard();

        greetingTextWelcome.setText(greetingText);

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserAccountDetails userAccountDetails = new UserAccountDetails(userDetails, activity, getLoginInfo());
                userAccountDetails.show(v);
            }
        });

        registerUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerMenuOnClicked(v);
            }
        });
    }

    private void registerMenuOnClicked(View v){
        clearMenu();
        activity.getLayoutInflater().inflate(R.layout.admin_register_user_menu, (ViewGroup) rootViewContainer);

        String[] roleList = {"Admin", "User", "Courier"};
        Spinner roleTypeList = activity.findViewById(R.id.fieldInputRoleType);
        Button registerUserBtn = activity.findViewById(R.id.admRegisterUserBtn);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(activity, R.layout.trash_list_dialog_dropdown, roleList);

        roleTypeList.setAdapter(arrayAdapter);
        imageSetterFromStream.setAsImageDrawable("user_icon.png", R.id.userInputNameAdmIcon);
        imageSetterFromStream.setAsImageDrawable("email_icon.png", R.id.userInputEmailAdmIcon);
        imageSetterFromStream.setAsImageDrawable("role_icon.png", R.id.roleTypeAdmIcon);

        registerUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity registerActivity = new RegisterActivity();
                EditText fieldUsername = activity.findViewById(R.id.fieldInputNameAdmRegister);
                EditText fieldEmail = activity.findViewById(R.id.fieldInputEmailAdmRegister);
                String roleSelected = roleList[roleTypeList.getSelectedItemPosition()];
                String username = fieldUsername.getText().toString();
                String email = fieldEmail.getText().toString();
                String password = UUID.randomUUID().toString();
                password = password.substring((password.length()-8));
                HashMap<String, Object> requestPayload = new HashMap<>();

                requestPayload.put("role", roleSelected);
                requestPayload.put("password", password);
                requestPayload.put("name", username);
                requestPayload.put("email", email);

                registerActivity.onClickedButtonSignUpRegister(v, activity, requestPayload);
            }
        });
    }

    private void clearMenu(){
        rootViewContainer.removeAllViews();
    }

    public SharedPreferences getLoginInfo() {
        return loginInfo;
    }

    public void setLoginInfo(SharedPreferences loginInfo) {
        this.loginInfo = loginInfo;
    }
}