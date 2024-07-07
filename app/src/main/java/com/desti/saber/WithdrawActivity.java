package com.desti.saber;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.StrictMode;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.desti.saber.LayoutHelper.FailedNotif.FailServerConnectToast;
import com.desti.saber.LayoutHelper.ProgressBar.ProgressBarHelper;
import com.desti.saber.LayoutHelper.ReportDownload.PDFCreator;
import com.desti.saber.LayoutHelper.ReportDownload.ReportDownload;
import com.desti.saber.LayoutHelper.SingleTrxListLayout.OnClickActionSingleListActivity;
import com.desti.saber.LayoutHelper.SingleTrxListLayout.ParentSingleListViewGroup;
import com.desti.saber.LayoutHelper.UserDetails.UserDetails;
import com.desti.saber.utils.GetUserDetailsCallback;
import com.desti.saber.utils.IDRFormatCurr;
import com.desti.saber.utils.ImageSetterFromStream;
import com.desti.saber.utils.constant.ActivityStatusDetail;
import com.desti.saber.utils.constant.GetImageProfileCallback;
import com.desti.saber.utils.constant.PathUrl;
import com.desti.saber.utils.constant.UserDetailKeys;
import com.desti.saber.utils.dto.DataHistoricalResDTO;
import com.desti.saber.utils.dto.ResponseGlobalJsonDTO;
import com.desti.saber.utils.dto.UserDetailsDTO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.PdfPTable;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class WithdrawActivity extends AppCompatActivity {

    private MainDashboard mainDashboard;
    private UserDetails userDetails;
    private FailServerConnectToast failServerConnectToast;
    private ImageSetterFromStream imageSetterFromStream;
    private SharedPreferences loginInfo;
    private LinearLayout balanceLayout;
    private View histTrxList;
    private ReportDownload reportDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_withdraw);

        mainDashboard = new MainDashboard();
        loginInfo = getSharedPreferences("LoginInfo", Context.MODE_PRIVATE);
        imageSetterFromStream = new ImageSetterFromStream(this);
        reportDownload = new ReportDownload(this);
        userDetails = new UserDetails(loginInfo.getString(UserDetailKeys.USER_ID_KEY, null));


        ImageView profileImage = findViewById(R.id.profileImage);
        Button balanceNavButton  = findViewById(R.id.balanceNavBtn);
        Button historyTrxNavButton = findViewById(R.id.historyTransactionNavBtn);
        String getLocPickup = getIntent().getStringExtra(UserDetailKeys.PICK_LOCATION_KEY);

        imageSetterFromStream.setAsImageBackground("balance_bg.png", R.id.balanceWrapper);
        imageSetterFromStream.setAsImageDrawable("def_user_profile.png", profileImage);
        balanceLayout = findViewById(R.id.balanceLayout);
        mainDashboard.getImageProfile(
            loginInfo.getString("photo", null),
            this,
            new OkHttpClient(),
            new GetImageProfileCallback() {
                @Override
                public void fail(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void success(Bitmap bitmap) {
                    profileImage.setImageBitmap(bitmap);
                }
            }
        );

        balanceNavButton.setOnClickListener(new View.OnClickListener() {
            //initial Set Balance Value
            @Override
            public void onClick(View v) {
                setBalanceLayout();
            }
        });

        historyTrxNavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setHistoryTrx((Activity) getWindow().getContext());
            }
        });

        setUserNameTittle(loginInfo.getString(UserDetailKeys.USERNAME_KEY, "Invalid Username"));
        setPinPointLocTitle((getLocPickup == null) ? "Lokasi Tidak Diset" : getLocPickup);
        setBalanceLayout();
    }

    // Section For Calling endpoint and Set List Trx History
    private void setHistoryTrx(Activity activity){
        balanceLayout.setVisibility(View.GONE);
        histTrxList = findViewById(R.id.historyTransactionLayout);
        removeTrxLayout();

        histTrxList = getLayoutInflater().inflate(R.layout.history_transaction_layout, findViewById(R.id.rootWithDrawActivity));
        ViewGroup rootParentTrxList = histTrxList.findViewById(R.id.trxListContainer);
        ProgressBar progressBar = histTrxList.findViewById(R.id.progressBar);
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .header(
            "Authorization",
            "Bearer " + loginInfo.getString(UserDetailKeys.TOKEN_KEY, "")
                ).get()
                .url(PathUrl.ENP_USER_ACTIVITY)
                .build();

        progressBar.setVisibility(View.VISIBLE);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        try {
                            if(response.isSuccessful() && response.body() != null){
                                String bodyResults = response.body().string();
                                TypeToken<ResponseGlobalJsonDTO<DataHistoricalResDTO>>  typeToken = new TypeToken<ResponseGlobalJsonDTO<DataHistoricalResDTO>>(){};
                                ResponseGlobalJsonDTO<DataHistoricalResDTO> globalJsonDTO = new Gson().fromJson(bodyResults, typeToken);
                                DataHistoricalResDTO[] dataHistoricalResDTOS = globalJsonDTO.getData();

                                if(dataHistoricalResDTOS.length > 0){
                                    reportDownload.setUserName(loginInfo.getString(UserDetailKeys.USERNAME_KEY, ""));
                                    reportDownload.setUserId(loginInfo.getString(UserDetailKeys.USER_ID_KEY, ""));
                                    reportDownload.setPdfName("Historical_Transaction");
                                    reportDownload.startReport(new PDFCreator() {
                                        @Override
                                        public void createReportPDF(Document document) throws Exception{
                                            PdfPTable pdfPTable = new PdfPTable(5);
                                            List<String> headerTable = new ArrayList<>();
                                            int alignLeft = Element.ALIGN_LEFT;

                                            headerTable.add("AKTIVITAS");
                                            headerTable.add("DETAIL AKTIVTIAS");
                                            headerTable.add("DESKRIPSI AKTIVITAS");
                                            headerTable.add("TANGGAL AKTIVITAS");
                                            headerTable.add("STATUS AKTIVITAS");

                                            for(String header : headerTable){
                                                pdfPTable.addCell(reportDownload.addCustomCell(header, Element.ALIGN_CENTER, 9));
                                            }

                                            for(DataHistoricalResDTO historicalResDTO : dataHistoricalResDTOS){
                                                pdfPTable.addCell(reportDownload.addCustomCell(historicalResDTO.getActivityTitleGroup(), alignLeft, 9));
                                                pdfPTable.addCell(reportDownload.addCustomCell(historicalResDTO.getActivityTitleDetail(), alignLeft, 9));
                                                pdfPTable.addCell(reportDownload.addCustomCell(historicalResDTO.getActivityDesc(), alignLeft, 9));
                                                pdfPTable.addCell(reportDownload.addCustomCell(historicalResDTO.getActivityDate(), alignLeft, 9));
                                                pdfPTable.addCell(reportDownload.addCustomCell(ActivityStatusDetail.values()[historicalResDTO.getActivityStatus()].toString(), alignLeft, 9));
                                            }

                                            document.add(pdfPTable);
                                        }
                                    });
                                }

                                for(int i = 0; i < dataHistoricalResDTOS.length; i++){
                                    DataHistoricalResDTO historicalDTO = dataHistoricalResDTOS[i];
                                    ParentSingleListViewGroup parentSingleList = new ParentSingleListViewGroup(activity);
                                    parentSingleList.setUserName(historicalDTO.getActivityTitleGroup());
                                    parentSingleList.setDate(historicalDTO.getActivityDate());
                                    parentSingleList.setActivity(historicalDTO.getActivityTitleDetail(), historicalDTO.getTotalBalance());
                                    parentSingleList.setDescription(historicalDTO.getActivityDesc());
                                    parentSingleList.setActivityStatus(String.valueOf(ActivityStatusDetail.values()[historicalDTO.getActivityStatus()]), new OnClickActionSingleListActivity() {
                                        @Override
                                        public void onClick(View view) {
                                            ProgressBarHelper.onProgress(  view, true);
                                            Request request = new Request.Builder()
                                            .header(
                                            "Authorization",
                                            "Bearer " + loginInfo.getString(UserDetailKeys.TOKEN_KEY, "")
                                            ).delete()
                                            .url(PathUrl.ENP_USER_ACTIVITY.concat("?actId=").concat(historicalDTO.getId()))
                                            .build();
                                            okHttpClient.newCall(request).enqueue(new Callback() {
                                                @Override
                                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                                    WithdrawActivity.this.runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            ProgressBarHelper.onProgress(  view, false);
                                                            Toast.makeText(getApplicationContext(), "Gagal Melakukan Penghapusan Histroy, periksa Internet", Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                                    WithdrawActivity.this.runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            String message = "Berhasil Menghapus History Dengan ID : " + historicalDTO.getId();
                                                            if(response.isSuccessful()){
                                                                rootParentTrxList.removeView(parentSingleList);
                                                            }else{
                                                                ProgressBarHelper.onProgress(  view, false);
                                                                message = "Gagal Melakukan Penghapusan Histroy";
                                                            }
                                                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    });

                                    rootParentTrxList.addView(parentSingleList);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void setBalanceLayout(){
        reportDownload.closeReport();
        removeTrxLayout();

        balanceLayout.setVisibility(View.VISIBLE);
        Button withdrawButton = findViewById(R.id.withdrawBtn);
        withdrawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BankTransferActivity.class);
                startActivity(intent);
                finish();
            }
        });

        this.setBalanceValue(0L);
        userDetails.get(new GetUserDetailsCallback(){
            @Override
            public void failure(Call call, IOException e) {
                e.printStackTrace();
                failServerConnectToast.show();
            }

            @Override
            public void onSuccess(UserDetailsDTO userDetailsDTO) {
                if(userDetailsDTO != null){
                    setBalanceValue(Long.parseLong(userDetailsDTO.getBalance()));
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
                ((TextView) findViewById(R.id.userNameLabelWithdraw)).setText(newUserName);
            }
        });
    }

    private void setPinPointLocTitle(String location){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(location != null){
                    int maxLocLength = 25;
                    String newLocation = (location.length() > maxLocLength) ? location.substring(0, maxLocLength) + "..." : location;
                    ((TextView) findViewById(R.id.pinPointLocTitle)).setText(newLocation);
                }
            }
        });
    }

    private void setBalanceValue(Long balance){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView balanceTextView = findViewById(R.id.balanceTitle);
                if(balance == null){
                    balanceTextView.setText(0);
                }else{
                    balanceTextView.setText(IDRFormatCurr.currFormat(balance));
                }
            }
        });
    }

    private void removeTrxLayout(){
        ViewGroup layout = findViewById(R.id.historyTransactionLayout);
        if(layout != null){
            ViewGroup viewGroup = findViewById(R.id.rootWithDrawActivity);
            viewGroup.removeView(layout);
        }
    }
}