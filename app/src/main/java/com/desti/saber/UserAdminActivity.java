package com.desti.saber;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.desti.saber.LayoutHelper.FailedNotif.FailServerConnectToast;
import com.desti.saber.LayoutHelper.PickupTrashes.PickupOnClicked;
import com.desti.saber.LayoutHelper.PickupTrashes.PickupTrashesList;
import com.desti.saber.LayoutHelper.ProgressBar.ProgressBarHelper;
import com.desti.saber.LayoutHelper.ReportDownload.ClickableReport;
import com.desti.saber.LayoutHelper.ReportDownload.PDFCreator;
import com.desti.saber.LayoutHelper.ReportDownload.ReportDownload;
import com.desti.saber.LayoutHelper.SinglePickupDetail.SinglePickupDetail;
import com.desti.saber.LayoutHelper.UserAccountDetails.UserAccountDetails;
import com.desti.saber.LayoutHelper.UserDetails.UserDetails;
import com.desti.saber.configs.OkHttpHandler;
import com.desti.saber.utils.IDRFormatCurr;
import com.desti.saber.utils.ImageSetterFromStream;
import com.desti.saber.LayoutHelper.UserDetails.GetImageProfileCallback;
import com.desti.saber.utils.constant.PathUrl;
import com.desti.saber.utils.constant.UserDetailKeys;
import com.desti.saber.utils.dto.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import okhttp3.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;
import java.util.List;

public class UserAdminActivity extends CommonObject {

    private String token;
    private String username;
    private String userId;
    private SharedPreferences loginInfo;
    private LinearLayout rootViewContainer;
    private ReportDownload reportDownload;
    private ImageSetterFromStream imageSetterFromStream;
    OkHttpHandler okHttpHandler;
    private TextView noDataText;
    private TextView greetingText;
    private FailServerConnectToast failServerConnectToast;
    private Image defaultImageFault;
    MainDashboard mainDashboard;

    public UserAdminActivity(Activity activity, ViewGroup viewGroup, UserDetails userDetails) {
        super(activity, viewGroup, userDetails);
        this.reportDownload = new ReportDownload(getActivity());
        this.failServerConnectToast = new FailServerConnectToast(getActivity());

        try{
            InputStream defaultImage = getActivity().getAssets().open("def_user_profile.png");
            defaultImageFault = Image.getInstance(imageToByteArray(BitmapFactory.decodeStream(defaultImage)));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void onCreate() {
        getActivity().getLayoutInflater().inflate(R.layout.activity_user_admin, getRootView());
        rootViewContainer = getActivity().findViewById(R.id.rootAdminContainerMenu);
        imageSetterFromStream = new ImageSetterFromStream(getActivity());
        noDataText = getActivity().findViewById(R.id.noData);
        greetingText = getActivity().findViewById(R.id.adminGreetingText);
        okHttpHandler = new OkHttpHandler();
        token = loginInfo.getString(UserDetailKeys.TOKEN_KEY, null);
        userId = loginInfo.getString(UserDetailKeys.USER_ID_KEY, null);
        username = loginInfo.getString(UserDetailKeys.USERNAME_KEY, null);
        reportDownload.setUserName(username);
        reportDownload.setUserId(userId);

        Button trashPickupData = getActivity().findViewById(R.id.pickupTrashRequestDataBtn);
        Button registerUserBtn = getActivity().findViewById(R.id.registerNewUserBtn);
        String greeting = "Hai.. ".concat(username.toUpperCase())
        .concat(" Selamat Datang Kembali, Dimenu SABER ADMIN");
        Button userDataBtn = getActivity().findViewById(R.id.userDataBtn);
        Button cashOut = getActivity().findViewById(R.id.cashOutDataBtn);
        Button profileBtn = getActivity().findViewById(R.id.profileBtn);
        mainDashboard = new MainDashboard();
        mainDashboard.setFailServerConnectToast(failServerConnectToast);

        greetingText.setText(greeting);

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserAccountDetails userAccountDetails = new UserAccountDetails(getUserDetails(), getActivity(), getLoginInfo());
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

        trashPickupData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               trashPickupRequest(v);
            }
        });

        userDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userDataOnClicked(v);
            }
        });
    }

    private void registerMenuOnClicked(View v){
        clearMenu();
        getActivity().getLayoutInflater().inflate(R.layout.admin_register_user_menu, (ViewGroup) rootViewContainer);

        String[] roleList = {"Admin", "User", "Courier"};
        Spinner roleTypeList = getActivity().findViewById(R.id.fieldInputRoleType);
        Button registerUserBtn = getActivity().findViewById(R.id.admRegisterUserBtn);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.trash_list_dialog_dropdown, roleList);

        roleTypeList.setAdapter(arrayAdapter);
        imageSetterFromStream.setAsImageDrawable("user_icon.png", R.id.userInputNameAdmIcon);
        imageSetterFromStream.setAsImageDrawable("email_icon.png", R.id.userInputEmailAdmIcon);
        imageSetterFromStream.setAsImageDrawable("role_icon.png", R.id.roleTypeAdmIcon);

        registerUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity registerActivity = new RegisterActivity();
                EditText fieldUsername = getActivity().findViewById(R.id.fieldInputNameAdmRegister);
                EditText fieldEmail = getActivity().findViewById(R.id.fieldInputEmailAdmRegister);
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

                registerActivity.onClickedButtonSignUpRegister(v, getActivity(), requestPayload);
            }
        });
    }

    private void cashOutMenuOnClicked(View v){
        clearMenu();
        Request.Builder request = new Request.Builder().url(PathUrl.ROOT_PATH_WITHDRAW).get();
        request.header("Authorization", "Bearer " + token);
        ProgressBarHelper.onProgress(v, true);

        okHttpHandler.requestAsync(getActivity(), request.build(), new OkHttpHandler.MyCallback() {
            @Override
            public void onSuccess(Context context, Response response) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProgressBarHelper.onProgress(v, false);
                        if(response.isSuccessful() && response.body() != null){
                            try {
                                String results = response.body().string();
                                TypeToken<ResponseGlobalJsonDTO<WithdrawResponseDTO>> dtoTypeToken = new TypeToken<ResponseGlobalJsonDTO<WithdrawResponseDTO>>(){};
                                ResponseGlobalJsonDTO<WithdrawResponseDTO> globalJsonDTO = new Gson().fromJson(results, dtoTypeToken);

                                if(globalJsonDTO.getData().length > 0){
                                    View inflateCashOutContainer = getActivity().getLayoutInflater().inflate(R.layout.global_list_container, rootViewContainer);
                                    ViewGroup cashOutContainer = (ViewGroup) inflateCashOutContainer.findViewById(R.id.globalListContainer);

                                    reportDownload.setPdfName("Cash Out Transaction");
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
                                                pdfPTable.addCell(valueTable(singleWithdraw.getEmail()));
                                                pdfPTable.addCell(valueTable(finalAmount));
                                                pdfPTable.addCell(valueTable(singleWithdraw.getRefnumber()));
                                                pdfPTable.addCell(valueTable(singleWithdraw.getWithdrawaldate()));
                                            }

                                            document.add(pdfPTable);
                                        }
                                    });

                                    for(WithdrawResponseDTO withdrawResponseDTO : globalJsonDTO.getData()){
                                        View detailsRootView = getActivity().getLayoutInflater().inflate(R.layout.single_cash_out_detail, cashOutContainer, false);
                                        TextView trxCashOutDate = detailsRootView.findViewById(R.id.trxCashOutUDate);
                                        TextView trxCashOutUserId = detailsRootView.findViewById(R.id.trxCashOutUserId);
                                        TextView trxCashOutAmount = detailsRootView.findViewById(R.id.trxCashOutAmount);
                                        TextView trxCashOutUserName = detailsRootView.findViewById(R.id.trxCashOutUserName);
                                        TextView trxCashOutUserEmail = detailsRootView.findViewById(R.id.trxCashOutUserEmail);
                                        TextView trxCashOutRefNumber = detailsRootView.findViewById(R.id.trxCashOutRefNUmber);

                                        trxCashOutAmount.setText(IDRFormatCurr.currFormat(Long.parseLong(withdrawResponseDTO.getAmount())));
                                        trxCashOutUserId.setText(withdrawResponseDTO.getUserid());
                                        trxCashOutUserEmail.setText(withdrawResponseDTO.getEmail());
                                        trxCashOutUserName.setText(withdrawResponseDTO.getUsername());
                                        trxCashOutRefNumber.setText(withdrawResponseDTO.getRefnumber());
                                        trxCashOutDate.setText(withdrawResponseDTO.getWithdrawaldate());
                                        cashOutContainer.addView(detailsRootView);
                                    }
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
                failServerConnectToast.show();
            }
        });
    }

    private void userDataOnClicked(View v){
        clearMenu();

        Request request = new Request.Builder().url(PathUrl.ROOT_PATH_USER).build();
        ViewGroup globalList = (ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.global_list_container, rootViewContainer, true);
        ViewGroup listContainer =  globalList.findViewById(R.id.globalListContainer);

        mainDashboard.setFailServerConnectToast(failServerConnectToast);

        ProgressBarHelper.onProgress(v, true);
        okHttpHandler.requestAsync(v.getContext(), request, new OkHttpHandler.MyCallback() {
            @Override
            public void onSuccess(Context context, Response response) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProgressBarHelper.onProgress(v, false);

                        if(response.isSuccessful() && response.body() != null){
                            try {
                                String responseString = response.body().string();
                                TypeToken<ResponseGlobalJsonDTO<UserDetailsDTO>> globalJsonDTO = new TypeToken<ResponseGlobalJsonDTO<UserDetailsDTO>>(){};
                                ResponseGlobalJsonDTO<UserDetailsDTO> results = new Gson().fromJson(responseString, globalJsonDTO);
                                UserDetailsDTO[] userDetailsDTOS = results.getData();
                                HashMap<String, PdfPTable> pdfUserTable = new HashMap<>();


                                for (UserDetailsDTO userDetailsDTO : userDetailsDTOS){
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String uriGetPhoto = (userDetailsDTO.getPhoto() != null) ? "https://drive.usercontent.google.com/download?id=".concat(userDetailsDTO.getPhoto().replace("/view", "")) : null;
                                            ViewGroup singleLayoutUserDetail = (ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.single_user_detail_layout, listContainer, false);
                                            ImageView imageView = singleLayoutUserDetail.findViewById(R.id.profileImageAdmin);
                                            FrameLayout progressBar = singleLayoutUserDetail.findViewById(R.id.progressBarLoading);

                                            ((TextView) singleLayoutUserDetail.findViewById(R.id.userDetailRole)).setText(userDetailsDTO.getRole());
                                            ((TextView) singleLayoutUserDetail.findViewById(R.id.userDetailName)).setText(userDetailsDTO.getName());
                                            ((TextView) singleLayoutUserDetail.findViewById(R.id.userDetailEmail)).setText(userDetailsDTO.getEmail());
                                            ((TextView) singleLayoutUserDetail.findViewById(R.id.userDetailPhoneNumber)).setText(userDetailsDTO.getPhone());

                                            if(uriGetPhoto != null){
                                                mainDashboard.getImageProfile(
                                                    userDetailsDTO.getPhoto(),
                                                    getActivity(),
                                                    new GetImageProfileCallback() {
                                                        @Override
                                                        public void ioNetworkFail(Exception e) {
                                                            failedResponse();
                                                            e.printStackTrace();
                                                        }

                                                        @Override
                                                        public void success(Bitmap bitmap) {
                                                            getActivity().runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    pdfUserTable.put(userDetailsDTO.getId(), userDetail(userDetailsDTO, bitmap));
                                                                    progressBar.setVisibility(View.GONE);
                                                                    imageView.setImageBitmap(bitmap);
                                                                }
                                                            });
                                                        }

                                                        @Override
                                                        public void endPointFaultResponse(Response response) {
                                                            failedResponse();
                                                        }

                                                        private void failedResponse(){
                                                            getActivity().runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    pdfUserTable.put(userDetailsDTO.getId(), userDetail(userDetailsDTO, null));
                                                                    progressBar.setVisibility(View.GONE);
                                                                    imageSetterFromStream.setAsImageDrawable("def_user_profile.png", imageView);
                                                                }
                                                            });
                                                        }
                                                    }
                                                );
                                            } else {
                                                pdfUserTable.put(userDetailsDTO.getId(), userDetail(userDetailsDTO, null));
                                                progressBar.setVisibility(View.GONE);
                                                imageSetterFromStream.setAsImageDrawable("def_user_profile.png", imageView);
                                            }

                                            listContainer.addView(singleLayoutUserDetail);
                                        }
                                    });
                                }

                                reportDownload.setPdfName("List Member");
                                reportDownload.startReportOnlyClick(new ClickableReport() {
                                    @Override
                                    public void onClicked(View viewButton, ViewGroup rootView) {
                                        if(pdfUserTable.size() != userDetailsDTOS.length){
                                            Toast.makeText(getActivity(), "Report Tersedia Setelah Unduhan Selesai", Toast.LENGTH_LONG).show();
                                        } else{
                                            reportDownload.startReport(new PDFCreator() {
                                                @Override
                                                public void createReportPDF(Document document) throws Exception {
                                                   Object[] pdfKeyTable = pdfUserTable.keySet().toArray();

                                                    for (Object object : pdfKeyTable) {
                                                        String pdfKey = object.toString();
                                                        Font labelFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
                                                        Paragraph trashListParagraph = new Paragraph("Detail Pengguna - ".concat(pdfKey), labelFont);

                                                        trashListParagraph.setAlignment(Element.ALIGN_LEFT);
                                                        trashListParagraph.setSpacingAfter(8f);

                                                        document.add(trashListParagraph);
                                                        document.add(pdfUserTable.get(pdfKey));
                                                        document.add(Chunk.NEWLINE);
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProgressBarHelper.onProgress(v, false);
                        failServerConnectToast.show();
                    }
                });
            }
        });
    }

    private void trashPickupRequest(View v){
        clearMenu();
        baseTrashPickupRequest(v, okHttpHandler, null);
    }

    public void setRootViewContainer(LinearLayout rootViewContainer) {
        this.rootViewContainer = rootViewContainer;
    }

    public void baseTrashPickupRequest(View v, OkHttpHandler okHttpHandler, BaseTrashPickupOnClick baseTrashPickupOnClick){
        Request request = new Request.Builder().get().url(PathUrl.ROOT_PATH_PICKUP).build();

        ProgressBarHelper.onProgress(v, true);
        okHttpHandler.requestAsync(getActivity(), request, new OkHttpHandler.MyCallback() {
            @Override
            public void onSuccess(Context context, Response response) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProgressBarHelper.onProgress(v, false);

                        try{
                            if(response.isSuccessful() && response.body() != null){
                                String responseResults = response.body().string();
                                TypeToken<ResponseGlobalJsonDTO<PickupDetailDto>> dtoTypeToken = new TypeToken<ResponseGlobalJsonDTO<PickupDetailDto>>(){};
                                ResponseGlobalJsonDTO<PickupDetailDto> globalJsonDTO = new Gson().fromJson(responseResults, dtoTypeToken);
                                PickupDetailDto[] pickupDetailDto = globalJsonDTO.getData();

                                if(pickupDetailDto.length > 0){
                                    View inflatePickTrash = getActivity().getLayoutInflater().inflate(R.layout.global_list_container, rootViewContainer, true);
                                    LinearLayout globalContainer =  inflatePickTrash.findViewById(R.id.globalListContainer);
                                    List<PdfPTable> pdfPTableList = new ArrayList<>();
                                    List<String> labelForTableList = new ArrayList<>();

                                    for(PickupDetailDto singlePickupDetailDto : globalJsonDTO.getData()){
                                        if(singlePickupDetailDto.getStatus() != null && !singlePickupDetailDto.getStatus().equalsIgnoreCase("editing")){
                                            SinglePickupDetail singlePickupDetail = new SinglePickupDetail(getActivity());
                                            ViewGroup showPickupDetail = singlePickupDetail.showPickupDetail(globalContainer, singlePickupDetailDto);

                                            pdfPTableList.add(singlePickupDetail.pdfPTablePickupDetail(singlePickupDetailDto));
                                            labelForTableList.add(singlePickupDetailDto.getName().concat(" - ").concat(singlePickupDetailDto.getUser_id()));

                                            showPickupDetail.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            ProgressBarHelper.onProgress(v, true);
                                                            Request newReq = new Request.Builder().url(PathUrl.ROOT_PATH_PICKUP.concat("/").concat(singlePickupDetailDto.getId())).build();

                                                            okHttpHandler.requestAsync(getActivity(), newReq, new OkHttpHandler.MyCallback() {
                                                                @Override
                                                                public void onSuccess(Context context, Response response) {
                                                                    getActivity().runOnUiThread(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            ProgressBarHelper.onProgress(v, false);
                                                                            if(response.isSuccessful() && response.body() != null){
                                                                                try {
                                                                                    String responseResults = response.body().string();
                                                                                    TypeToken<ResponseGlobalJsonDTO<TrashDetailDTO>> typeToken = new  TypeToken<ResponseGlobalJsonDTO<TrashDetailDTO>>(){};
                                                                                    ResponseGlobalJsonDTO<TrashDetailDTO> trashDetailDTOResponse = new Gson().fromJson(responseResults, typeToken);
                                                                                    PickupTrashesList pickupTrashesList = new PickupTrashesList(getActivity());

                                                                                    pickupTrashesList.setReportDownload(reportDownload);
                                                                                    pickupTrashesList.startPopUp(trashDetailDTOResponse.getData(), singlePickupDetailDto, new PickupOnClicked() {
                                                                                        @Override
                                                                                        public void pickOnClicked(View v) {
                                                                                            if (baseTrashPickupOnClick != null) {
                                                                                                baseTrashPickupOnClick.onClickPickupBtn(v, pickupTrashesList, singlePickupDetailDto);
                                                                                            }
                                                                                        }
                                                                                    });
                                                                                    pickupTrashesList.visibilityPickupButton(false);

                                                                                    if(baseTrashPickupOnClick != null){
                                                                                        baseTrashPickupOnClick.pickupPopUpList(pickupTrashesList);
                                                                                    }
                                                                                }catch (Exception e){
                                                                                    e.printStackTrace();
                                                                                }
                                                                            }
                                                                        }
                                                                    });
                                                                }

                                                                @Override
                                                                public void onFailure(Exception e) {
                                                                    getActivity().runOnUiThread(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            failServerConnectToast.show();
                                                                            ProgressBarHelper.onProgress(v, false);
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    }

                                    reportDownload.setPdfName("Trash Request Pickup");
                                    reportDownload.startReport(new PDFCreator() {
                                        @Override
                                        public void createReportPDF(Document document) throws Exception {
                                            for(int i=0; i <  pdfPTableList.size(); i++){
                                                PdfPTable pdfPTable = pdfPTableList.get(i);
                                                Paragraph paragraph = new Paragraph("Detail Pickup ".concat(labelForTableList.get(i)).concat("\n"));

                                                paragraph.setSpacingAfter(10f);
                                                document.add(paragraph);
                                                document.add(pdfPTable);
                                                document.add(Chunk.NEWLINE);
                                            }
                                        }
                                    });
                                } else {
                                    setNoDataText("Tidak ada data permintaan pickup sampah");
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                failServerConnectToast.show();
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
        return reportDownload.addCustomCell(valueName, Element.ALIGN_LEFT, 10);
    }

    private PdfPTable userDetail(UserDetailsDTO userDetailsDTO, Bitmap bitmap){
        PdfPTable userSinglePdf = new PdfPTable(3);
        PdfPCell userSinglePdfCell =  new PdfPCell();

        try{
            Image image = Image.getInstance(imageToByteArray(bitmap));
            userSinglePdfCell.addElement(image);
        } catch (Exception e){
            userSinglePdfCell.addElement(defaultImageFault);
            e.printStackTrace();
        } finally {
            userSinglePdfCell.setRowspan(4);
            userSinglePdf.addCell(userSinglePdfCell);
            userSinglePdf.addCell(valueTable("Nama"));
            userSinglePdf.addCell(valueTable(userDetailsDTO.getName()));
            userSinglePdf.addCell(valueTable("Email"));
            userSinglePdf.addCell(valueTable(userDetailsDTO.getEmail()));
            userSinglePdf.addCell(valueTable("No Hp"));
            userSinglePdf.addCell(valueTable(userDetailsDTO.getPhone()));
            userSinglePdf.addCell(valueTable("Peran Akun"));
            userSinglePdf.addCell(valueTable(userDetailsDTO.getRole()));
        }

        return  userSinglePdf;
    }

    private byte[] imageToByteArray(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

        return  byteArrayOutputStream.toByteArray();
    }
}