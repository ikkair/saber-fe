package com.desti.saber.LayoutHelper.UserAccountDetails;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.desti.saber.LayoutHelper.CustomFileChooser.CustomFileChooser;
import com.desti.saber.LayoutHelper.CustomFileChooser.SuccessSetImage;
import com.desti.saber.LayoutHelper.FailedNotif.FailServerConnectToast;
import com.desti.saber.LayoutHelper.ProgressBar.ProgressBarHelper;
import com.desti.saber.LayoutHelper.UserDetails.UserDetails;
import com.desti.saber.LoginActivity;
import com.desti.saber.MainDashboard;
import com.desti.saber.R;
import com.desti.saber.utils.GetUserDetailsCallback;
import com.desti.saber.utils.ImageSetterFromStream;
import com.desti.saber.utils.constant.GetImageProfileCallback;
import com.desti.saber.utils.constant.PathUrl;
import com.desti.saber.utils.constant.UserDetailKeys;
import com.desti.saber.utils.dto.UserDetailsDTO;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class UserAccountDetails {

    private final FailServerConnectToast failServerConnectToast;
    private final SharedPreferences sharedPreferences;
    private final MainDashboard mainDashboard;
    private final UserDetails userDetails;
    private final Activity activity;

    public UserAccountDetails(UserDetails userDetails, Activity activity, SharedPreferences sharedPreferences) {
        this.failServerConnectToast = new FailServerConnectToast(activity);
        this.sharedPreferences = sharedPreferences;
        this.mainDashboard = new MainDashboard();
        this.userDetails = userDetails;
        this.activity = activity;
    }

    public void show(View v){
        String password = sharedPreferences.getString(UserDetailKeys.PASSWORD_KEY, null);
        String token = sharedPreferences.getString(UserDetailKeys.TOKEN_KEY, null);
        OkHttpClient okHttpClient = new OkHttpClient();
        ProgressBarHelper.onProgress( v, true);

        userDetails.get(new GetUserDetailsCallback() {
            @Override
            public void failure(Call call, IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProgressBarHelper.onProgress( v, false);
                        failServerConnectToast.show();
                    }
                });
            }

            @Override
            public void onSuccess(UserDetailsDTO userDetailsDTO) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProgressBarHelper.onProgress( v, false);
                        if (userDetailsDTO != null) {
                            View inflateProfileDetail = activity.getLayoutInflater().inflate(R.layout.profile_detail_layout, null);
                            PopupWindow popupWindow = new PopupWindow(inflateProfileDetail, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                            View popUpViewCv = popupWindow.getContentView();
                            Context cvCtx = popUpViewCv.getContext();
                            TableLayout tL = popUpViewCv.findViewById(R.id.profileTable);
                            Button savedEdited = popUpViewCv.findViewById(R.id.saveEdited);
                            LinearLayout logoutButton = popUpViewCv.findViewById(R.id.logOut);
                            Button editProfileButton = popUpViewCv.findViewById(R.id.editProfile);
                            EditText passwordEdited = popUpViewCv.findViewById(R.id.passwordEdited);
                            TextView editProfileTv = popUpViewCv.findViewById(R.id.titleProfileEdited);
                            EditText usernameEditField = popUpViewCv.findViewById(R.id.userNameEdited);
                            Button cancelEditProfile = popUpViewCv.findViewById(R.id.cancelEditProfile);
                            ImageView profileImage = popUpViewCv.findViewById(R.id.imageProfileDetails);
                            ImageSetterFromStream imageSetterFromStream = new ImageSetterFromStream(activity);
                            FrameLayout progressBarImageProfile = popUpViewCv.findViewById(R.id.progressBarIp);
                            FrameLayout wrapperImageProfile = popUpViewCv.findViewById(R.id.imageProfileWrapper);
                            LinearLayout editProfileWrapper = popUpViewCv.findViewById(R.id.editedWrapperProfile);

                            imageSetterFromStream.setAsImageDrawable("def_user_profile.png", profileImage);
                            profileRowTable(tL, "Tanggal Terdaftar", userDetailsDTO.getCreation_date());
                            profileRowTable(tL, "Nomor Handphone", userDetailsDTO.getPhone());
                            profileRowTable(tL, "Nama Pengguna", userDetailsDTO.getName());
                            profileRowTable(tL, "Email", userDetailsDTO.getEmail());

                            if (userDetailsDTO.getPhoto() != null) {
                                mainDashboard.getImageProfile(userDetailsDTO.getPhoto(), activity, okHttpClient, new GetImageProfileCallback() {
                                    @Override
                                    public void fail(Call call, IOException e) {
                                        progressBarImageProfile.setVisibility(View.GONE);
                                        wrapperImageProfile.setEnabled(true);
                                    }

                                    @Override
                                    public void success(Bitmap bitmap) {
                                        profileImage.setImageBitmap(bitmap);
                                        progressBarImageProfile.setVisibility(View.GONE);
                                        wrapperImageProfile.setEnabled(true);
                                    }
                                });
                            } else {
                                progressBarImageProfile.setVisibility(View.GONE);
                                wrapperImageProfile.setEnabled(true);
                            }

                            wrapperImageProfile.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    CustomFileChooser customFileChooser = new CustomFileChooser(activity);
                                    customFileChooser.setParent((ViewGroup) popUpViewCv);
                                    customFileChooser.startImageChooser(new SuccessSetImage() {
                                        @Override
                                        public void success(Bitmap bitmap, String bitmapLoc, ViewGroup parentFileChooser) {
                                            File profileImageBitmap = new File(bitmapLoc);
                                            RequestBody requestBody = new MultipartBody.Builder()
                                                    .setType(MultipartBody.FORM)
                                                    .addFormDataPart("photo",
                                                        profileImageBitmap.getName(),
                                                        RequestBody.create(
                                                            MediaType.parse("image/png"),
                                                            profileImageBitmap
                                                        )
                                                    )
                                                    .addFormDataPart("name", userDetailsDTO.getName())
                                                    .addFormDataPart("password", password)
                                                    .build();

                                            Request editUsersDetails = new Request.Builder()
                                                    .header("Authorization", "Bearer " + token)
                                                    .put(requestBody)
                                                    .url(PathUrl.ROOT_PATH_USER)
                                                    .build();

                                            okHttpClient.newCall(editUsersDetails).enqueue(new Callback() {
                                                @Override
                                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                                    activity.runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            progressBarImageProfile.setVisibility(View.GONE);
                                                            wrapperImageProfile.setEnabled(true);
                                                            failServerConnectToast.show();
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                                    activity.runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            progressBarImageProfile.setVisibility(View.GONE);
                                                            wrapperImageProfile.setEnabled(true);

                                                            if (response.isSuccessful()) {
                                                                profileImage.setImageBitmap(bitmap);
                                                                mainDashboard.setImageProfile(activity, bitmap);
                                                            } else {
                                                                Toast.makeText(activity, "Gagal Memperbahrui Data", Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                    });
                                                }
                                            });

                                            v.setEnabled(false);
                                            progressBarImageProfile.setVisibility(View.VISIBLE);
                                            customFileChooser.closeCustomChooser(parentFileChooser);
                                        }
                                    });
                                }
                            });
                            logoutButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    activity.getSharedPreferences("LoginInfo", Context.MODE_PRIVATE).edit().clear().apply();
                                    Intent login = new Intent(activity, LoginActivity.class);
                                    activity.startActivity(login);
                                    popupWindow.dismiss();
                                    activity.finish();
                                }
                            });
                            editProfileButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    usernameEditField.setText(userDetailsDTO.getName());
                                    usernameEditField.setFocusableInTouchMode(true);
                                    usernameEditField.setFocusable(true);

                                    v.setVisibility(View.GONE);
                                    logoutButton.setVisibility(View.GONE);
                                    wrapperImageProfile.setVisibility(View.GONE);
                                    editProfileTv.setText(R.string.profile_edited);
                                    tL.setVisibility(View.GONE);
                                    editProfileWrapper.setVisibility(View.VISIBLE);

                                    savedEdited.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String newPassword = passwordEdited.getText().toString();
                                            String newUsername =  usernameEditField.getText().toString();

                                            RequestBody requestBody = new MultipartBody.Builder()
                                                    .setType(MultipartBody.FORM)
                                                    .addFormDataPart("name", newUsername)
                                                    .addFormDataPart("password", (newPassword.equals("")) ? password : newPassword)
                                                    .build();

                                            Request editUsersDetails = new Request.Builder()
                                                    .header("Authorization", "Bearer " + token)
                                                    .put(requestBody)
                                                    .url(PathUrl.ROOT_PATH_USER)
                                                    .build();

                                            ProgressBarHelper.onProgress( v, true);
                                            cancelEditProfile.setVisibility(View.GONE);
                                            okHttpClient.newCall(editUsersDetails).enqueue(new Callback() {
                                                @Override
                                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                                    activity.runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            ProgressBarHelper.onProgress( v, false);
                                                            failServerConnectToast.show();
                                                            cancelEditProfile.setVisibility(View.VISIBLE);
                                                            e.printStackTrace();
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                                    activity.runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            cancelEditProfile.setVisibility(View.VISIBLE);
                                                            ProgressBarHelper.onProgress( v, false);

                                                            if(response.isSuccessful()){
                                                                Toast.makeText(activity,"Sukses Melakukan Update Profile", Toast.LENGTH_LONG).show();
                                                                userDetailsDTO.setName(newUsername);

                                                                mainDashboard.setUserNameTittle(newUsername, activity);
                                                                popupWindow.dismiss();
                                                                show(v);
                                                            }else{
                                                                Toast.makeText(activity,"Gagal Melakukan Update Profile, Coba Kembali", Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                            cancelEditProfile.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(popUpViewCv.getApplicationWindowToken(), 0);

                                    editProfileWrapper.setVisibility(View.GONE);
                                    editProfileButton.setVisibility(View.VISIBLE);
                                    logoutButton.setVisibility(View.VISIBLE);
                                    wrapperImageProfile.setVisibility(View.VISIBLE);
                                    editProfileTv.setText(R.string.profile);
                                    tL.setVisibility(View.VISIBLE);

                                }
                            });

                            popupWindow.setFocusable(true);
                            popupWindow.showAtLocation(inflateProfileDetail, Gravity.CENTER, 0, 0);
                        } else {
                            Toast.makeText(activity, R.string.failed_get_details_accounts, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    private void profileRowTable(TableLayout tableLayout, String label, String value){
        TableRow tableRow = (TableRow) View.inflate(tableLayout.getContext(), R.layout.table_layout_row, null);
        TextView tvLabel = (TextView) tableRow.getChildAt(0);
        TextView tvValue = (TextView) tableRow.getChildAt(1);
        tvLabel.setText(label);
        tvValue.setText(value);
        tableLayout.addView(tableRow);
    }
}
