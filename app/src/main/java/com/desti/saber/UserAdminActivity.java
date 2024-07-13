package com.desti.saber;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.desti.saber.LayoutHelper.ProgressBar.ProgressBarHelper;
import com.desti.saber.LayoutHelper.ReportDownload.PDFCreator;
import com.desti.saber.LayoutHelper.ReportDownload.ReportDownload;
import com.desti.saber.LayoutHelper.UserAccountDetails.UserAccountDetails;
import com.desti.saber.LayoutHelper.UserDetails.UserDetails;
import com.desti.saber.configs.OkHttpHandler;
import com.desti.saber.utils.IDRFormatCurr;
import com.desti.saber.utils.ImageSetterFromStream;
import com.desti.saber.utils.constant.PathUrl;
import com.desti.saber.utils.constant.UserDetailKeys;
import com.desti.saber.utils.dto.ResponseGlobalJsonDTO;
import com.desti.saber.utils.dto.WithdrawResponseDTO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import okhttp3.*;

import java.util.HashMap;
import java.util.UUID;

public class UserAdminActivity {

    private String token;
    private String username;
    private String userId;
    private final Activity activity;
    private final ViewGroup viewGroup;
    private SharedPreferences loginInfo;
    private final UserDetails userDetails;
    private LinearLayout rootViewContainer;
    private final ReportDownload reportDownload;
    private ImageSetterFromStream imageSetterFromStream;
    private TextView noDataText;
    private TextView greetingText;


    public UserAdminActivity(Activity activity, ViewGroup viewGroup, UserDetails userDetails) {
        this.activity = activity;
        this.viewGroup = viewGroup;
        this.userDetails = userDetails;
        this.reportDownload = new ReportDownload(activity);
    }

    protected void onCreate() {
        activity.getLayoutInflater().inflate(R.layout.activity_user_admin, viewGroup);
        rootViewContainer = activity.findViewById(R.id.rootAdminContainerMenu);
        imageSetterFromStream = new ImageSetterFromStream(activity);
        noDataText = activity.findViewById(R.id.noData);
        greetingText = activity.findViewById(R.id.adminGreetingText);
        token = loginInfo.getString(UserDetailKeys.TOKEN_KEY, null);
        userId = loginInfo.getString(UserDetailKeys.USER_ID_KEY, null);
        username = loginInfo.getString(UserDetailKeys.USERNAME_KEY, null);


        Button trashPickupData = activity.findViewById(R.id.requestPickupTrash);
        Button registerUserBtn = activity.findViewById(R.id.registerNewUserBtn);
        Button trashInData = activity.findViewById(R.id.trashDataBtn);
        String greeting = "Hai.. ".concat(username.toUpperCase())
        .concat(" Selamat Datang Kembali, Dimenu SABER ADMIN");
        Button userDataBtn = activity.findViewById(R.id.userDataBtn);
        Button cashOut = activity.findViewById(R.id.cashOutDataBtn);
        Button profileBtn = activity.findViewById(R.id.profileBtn);
        MainDashboard mainDashboard = new MainDashboard();

        greetingText.setText(greeting);

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

        cashOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cashOutMenuOnClicked(v);
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
                requestPayload.put("token", token);

                registerActivity.onClickedButtonSignUpRegister(v, activity, requestPayload);
            }
        });
    }

    private void cashOutMenuOnClicked(View v){
        clearMenu();

        OkHttpHandler okHttpHandler = new OkHttpHandler();
        Request.Builder request = new Request.Builder().url(PathUrl.ROOT_PATH_WITHDRAW).get();

        request.header("Authorization", "Bearer " + token);
        ProgressBarHelper.onProgress(v, true);

        okHttpHandler.requestAsync(activity, request.build(), new OkHttpHandler.MyCallback() {
            @Override
            public void onSuccess(Context context, Response response) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProgressBarHelper.onProgress(v, false);
                        if(response.isSuccessful() && response.body() != null){
                            try {
                                TypeToken<ResponseGlobalJsonDTO<WithdrawResponseDTO>> dtoTypeToken = new TypeToken<ResponseGlobalJsonDTO<WithdrawResponseDTO>>(){};
                                ResponseGlobalJsonDTO<WithdrawResponseDTO> globalJsonDTO = new Gson().fromJson(response.body().string(), dtoTypeToken);

                                if(globalJsonDTO.getData().length > 0){
                                    View inflateCashOutContainer = activity.getLayoutInflater().inflate(R.layout.cash_out_container, rootViewContainer);
                                    ViewGroup cashOutContainer = (ViewGroup) inflateCashOutContainer.findViewById(R.id.cashOutContainer);

                                    for(WithdrawResponseDTO withdrawResponseDTO : globalJsonDTO.getData()){
                                        View detailsRootView = activity.getLayoutInflater().inflate(R.layout.single_cash_out_detail, cashOutContainer, false);
                                        TextView trxCashOutUserId = detailsRootView.findViewById(R.id.trxCashOutUserId);
                                        TextView trxCashOutUserName = detailsRootView.findViewById(R.id.trxCashOutUserName);
                                        TextView trxCashOutUserEmail = detailsRootView.findViewById(R.id.trxCashOutUserEmail);

                                        trxCashOutUserId.setText(withdrawResponseDTO.getUserid());
                                        trxCashOutUserName.setText(withdrawResponseDTO.getUsername());
                                        trxCashOutUserEmail.setText(withdrawResponseDTO.getUseremail());

                                        cashOutContainer.addView(detailsRootView);
                                    }

                                    reportDownload.setPdfName("Cash Out Transaction");
                                    reportDownload.setUserName(username);
                                    reportDownload.setUserId(userId);
                                    reportDownload.startReport(new PDFCreator() {
                                        @Override
                                        public void createReportPDF(Document document) throws Exception {
                                            String[] headerLists = {
                                                "USER NAME",
                                                "USER EMAIL",
                                                "AMOUNT",
                                                "REF NUMBER",
                                                "TRANSACTION DATE"
                                            };
                                            PdfPTable pdfPTable = new PdfPTable(headerLists.length);

                                            for (String singleHeaderName  : headerLists){
                                                pdfPTable.addCell(headerTable(singleHeaderName));
                                            }

                                            for (WithdrawResponseDTO singleWithdraw : (WithdrawResponseDTO[]) globalJsonDTO.getData()) {
                                                long amount = (singleWithdraw.getAmount() != null) ? Long.valueOf(singleWithdraw.getAmount()) : 0L;
                                                String finalAmount = IDRFormatCurr.currFormat(amount);


                                                pdfPTable.addCell(valueTable(singleWithdraw.getUsername()));
                                                pdfPTable.addCell(valueTable(singleWithdraw.getUseremail()));
                                                pdfPTable.addCell(valueTable(finalAmount));
                                                pdfPTable.addCell(valueTable(singleWithdraw.getRefnumber()));
                                                pdfPTable.addCell(valueTable(singleWithdraw.getTransactiondate()));

                                            }

                                            document.add(pdfPTable);
                                        }
                                    });
                                } else {
                                    setNoDataText("Tidak Ada Data Dana Keluar");
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        } else {
                            setNoDataText("Terjadi Kesalahan");
                        }
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    private void clearMenu(){
        TextView noDataTextTransit = noDataText;
        reportDownload.closeReport();
        rootViewContainer.removeAllViews();
        noDataTextTransit.setVisibility(View.GONE);
        rootViewContainer.addView(noDataTextTransit);
        noDataText = noDataTextTransit;
    }

    private void setNoDataText(String message){
        noDataText.setText(message);
        noDataText.setVisibility(View.VISIBLE);
    }

    public SharedPreferences getLoginInfo() {
        return loginInfo;
    }

    public void setLoginInfo(SharedPreferences loginInfo) {
        this.loginInfo = loginInfo;
    }

    private PdfPCell headerTable(String headerName){
        return reportDownload.addCustomCell(headerName, Element.ALIGN_CENTER, 14);
    }

    private PdfPCell valueTable(String valueName){
        return reportDownload.addCustomCell(valueName, Element.ALIGN_LEFT, 12);
    }
}