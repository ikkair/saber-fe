package com.desti.saber;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.desti.saber.LayoutHelper.ProgressBar.ProgressBarHelper;
import com.desti.saber.LayoutHelper.SingleArchiveBankList.OnClickArchiveBankBeneficiary;
import com.desti.saber.LayoutHelper.SingleArchiveBankList.OnClickSwitchSaved;
import com.desti.saber.LayoutHelper.SingleArchiveBankList.SingleArchiveBeneficiaryBankList;
import com.desti.saber.LayoutHelper.SingleArchiveBankList.SingleStoredBankNameToggle;
import com.desti.saber.LayoutHelper.UserDetails.UserDetails;
import com.desti.saber.LayoutHelper.WindowPopUp.CustomWindowPopUp;
import com.desti.saber.LayoutHelper.WindowPopUp.OnClickPopUpBtn;
import com.desti.saber.utils.GetUserDetailsCallback;
import com.desti.saber.utils.IDRFormatCurr;
import com.desti.saber.utils.ImageSetterFromStream;
import com.desti.saber.utils.constant.PathUrl;
import com.desti.saber.utils.constant.PropsConstantUtil;
import com.desti.saber.utils.constant.UserDetailKeys;
import com.desti.saber.utils.dto.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BankTransferActivity extends AppCompatActivity {

    private TextView transferActivityActionLabel;
    private TextView transferActivityMenuLabel;
    private TextView transferActivityNavLabel;
    private ImageSetterFromStream imageSetter;
    private EditText fieldInputVaOrAcNumber;
    private boolean storeInfBeneficiaryBank;
    private TextView transferActivityLabel;
    private ImageView navActivityBankIcon;
    private ViewGroup rootBottomDetailTrf;
    private ViewGroup finalDoTrfLayout;
    private String beneficiaryBankName;
    private int beneficiaryBankNameId;
    private Spinner bankNameListField;
    private int navActivityBankIconId;
    private ViewGroup bankActivityNav;
    private ViewGroup rootTrfActivity;
    private String accountOrVaNumber;
    private List<String>  trackingMenuHist;
    private String beneficiaryName;
    private Button nextTrfActionBtn;
    private long transferAmount;
    private Bitmap bankIcons;
    private long availableBalance;
    private String token;
    private UserDetails userDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_transfer);
        ViewGroup backTopButton = findViewById(R.id.backTopButton);
        SharedPreferences loginInfo = getSharedPreferences(UserDetailKeys.SHARED_PREF_LOGIN_KEY, Context.MODE_PRIVATE);

        trackingMenuHist = new ArrayList<>();
        nextTrfActionBtn = findViewById(R.id.nexTrfAction);
        imageSetter = new ImageSetterFromStream(this);
        rootTrfActivity = findViewById(R.id.rootTrfActivity);
        bankActivityNav = findViewById(R.id.bankActivityNav);
        navActivityBankIconId = R.id.transferActivityNavBankIcon;
        bankNameListField = findViewById(R.id.bankNameListField);
        navActivityBankIcon = findViewById(navActivityBankIconId);
        finalDoTrfLayout = findViewById(R.id.rootTrfActivityDoTrf);
        rootBottomDetailTrf = findViewById(R.id.rootBottomDetailTrf);
        transferActivityLabel = findViewById(R.id.transferActivityLabel);
        fieldInputVaOrAcNumber = findViewById(R.id.fieldInputVaOrAcNumber);
        transferActivityNavLabel = findViewById(R.id.transferActivityNavLabel);
        transferActivityMenuLabel = findViewById(R.id.transferActivityMenuLabel);
        transferActivityActionLabel = findViewById(R.id.transferActivityActionLabel);
        userDetails = new UserDetails(loginInfo.getString(UserDetailKeys.USER_ID_KEY, null));
        token = loginInfo.getString(UserDetailKeys.TOKEN_KEY, null);

        trackingMenuHist.add("rootDir");
        setLatestBankTransferTrx();

        //Set Back Trace Menu at below
        backTopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(trackingMenuHist != null){

                    int trackingMenuIndex = (trackingMenuHist.size()-1);
                    trackingMenuIndex = (trackingMenuIndex < 0) ? 0 : trackingMenuIndex;
                    String finalTrace = trackingMenuHist.get(trackingMenuIndex);

                    switch (finalTrace){
                        case "rootDir" :
                            Intent intent = new Intent();
                            intent.setClass(getApplicationContext(), WithdrawActivity.class);
                            intent.putExtra(UserDetailKeys.PICK_LOCATION_KEY, getIntent().getStringExtra(UserDetailKeys.PICK_LOCATION_KEY));
                            startActivity(intent);
                            finish();
                        break;

                        case "setLatestBankTransferTrx" :
                            setLatestBankTransferTrx();
                        break;

                        case "setTrxBankTransfer" :
                            setTrxBankTransfer();
                        break;

                        case "doTransferTrxTo" :
                            doTransferTrxTo();
                        break;

                        default: return;
                    }

                    trackingMenuHist.remove(trackingMenuIndex);
                }
            }
        });

        transferActivityActionLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Cooming Soon", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //section for set latest transfer trx
    private void setLatestBankTransferTrx(){
        disabledProp();

        navActivityBankIcon.setVisibility(View.VISIBLE);
        transferActivityMenuLabel.setText(R.string.withdraw_label);
        transferActivityLabel.setText(R.string.new_trf_bank_label);
        transferActivityNavLabel.setText(R.string.trf_to_bank_label);
        imageSetter.setAsImageDrawable("trf_to_bank_icon.png", navActivityBankIconId);
        ViewGroup archiveBankList = getArchiveBankList();

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //for set bac trace menu
                bankActivityNav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setTrxBankTransfer();
                        trackingMenuHist.add("setLatestBankTransferTrx");
                    }
                });

                //section for call endpoint and looping the latest bank trx hist
                for(int i =0; i < 100; i++){
                    int test = i;
                    SingleArchiveBeneficiaryBankList bankList = new SingleArchiveBeneficiaryBankList(getApplicationContext());
                    bankList.setSingleListBankArchive(null, "Sonia Mantep " + test,
                        new OnClickArchiveBankBeneficiary() {
                            @Override
                            public void onClick() {
                                Toast.makeText(getApplicationContext(), "Burung Kang Ibing Ada " + test, Toast.LENGTH_SHORT).show();
                            }
                        }
                    );
                    archiveBankList.addView(bankList);
                }
            }
        });
    }

    //section for call endpoint and looping bank name beneficiary stored
    private void setTrxBankTransfer(){
        disabledProp();

        navActivityBankIcon.setVisibility(View.VISIBLE);
        transferActivityActionLabel.setVisibility(View.VISIBLE);

        transferActivityMenuLabel.setText(R.string.trf_bank_label);
        transferActivityLabel.setText(R.string.account_number_archived);
        transferActivityNavLabel.setText(R.string.add_new_account_number);

        imageSetter.setAsImageDrawable("new_account_number_icon.png", navActivityBankIconId);
        ViewGroup archiveBankList = getArchiveBankList();

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //set back trace manu at below
                bankActivityNav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        trackingMenuHist.add("setTrxBankTransfer");
                        doTransferTrxTo();
                    }
                });

                //section for call endpoint and looping bank name beneficiary stored
                String[] beneficiaryBankMock = getResources().getStringArray(R.array.beneficiary_name_mock);

                for(int i =0; i < beneficiaryBankMock.length; i++){
                    SingleArchiveBeneficiaryBankList bankList = new SingleArchiveBeneficiaryBankList(getApplicationContext());
                    int finalI = i;
                    bankList.setSingleListBankArchive(null, beneficiaryBankMock[i],
                        new OnClickArchiveBankBeneficiary() {
                            @Override
                            public void onClick() {
                                trackingMenuHist.add("setTrxBankTransfer");
                                setTransferTrxTo(finalI);
                                doTransferTrxTo();
                            }
                        }
                    );
                    archiveBankList.addView(bankList);
                }
            }
        });
    }

    //Section For Inquiry valid bank name, stored bank info and va or account number
    private void doTransferTrxTo(){
        disabledProp();

        nextTrfActionBtn.setVisibility(View.VISIBLE);
        bankNameListField.setVisibility(View.VISIBLE);
        fieldInputVaOrAcNumber.setVisibility(View.VISIBLE);

        transferActivityNavLabel.setText(R.string.bank_name_label);
        transferActivityMenuLabel.setText(R.string.trf_to_bank_label);
        transferActivityLabel.setText(R.string.account_or_Va_number);

        SingleStoredBankNameToggle storedBankNameToggle = new SingleStoredBankNameToggle(getApplicationContext());
        ViewGroup archiveBankList = getArchiveBankList();

        storedBankNameToggle.setSwitchButton(new OnClickSwitchSaved() {
            @Override
            public void onClick(boolean switchButtonStatus) {
                storeInfBeneficiaryBank = switchButtonStatus;
            }
        });
        archiveBankList.addView(storedBankNameToggle);

        bankNameListField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i > 0){
                    beneficiaryBankNameId = i;
                    beneficiaryBankName = adapterView.getSelectedItem().toString();
                }else{
                    beneficiaryBankNameId = 0;
                    beneficiaryBankName = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        fieldInputVaOrAcNumber.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                EditText newView = ((EditText) view);
                String getText = newView.getText().toString();

                if(newView.getText().length() > 0 && !getText.equals(" ")){
                    accountOrVaNumber = getText;
                }else{
                    accountOrVaNumber = null;
                }
                return false;
            }
        });

        //section for mock hardcode to db
        nextTrfActionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(accountOrVaNumber == null || beneficiaryBankNameId <= 0){
                    Toast.makeText(
                        getApplicationContext(),
                        R.string.warn_info_empty_bank_data,
                        Toast.LENGTH_LONG
                    ).show();
                }else{
                    trackingMenuHist.add("doTransferTrxTo");

                    //start Section For Inquiry valid bank name, stored bank info and va or account number
                    String bankInquiryPattern = accountOrVaNumber+"|"+beneficiaryBankNameId;
                    List<String> beneficiaryBankMock = Arrays.asList(
                        getResources().getStringArray(R.array.beneficiary_bank_mock)
                    );

                    if(beneficiaryBankMock.contains(bankInquiryPattern)){
                        String[] beneficiaryNameMock = getResources().getStringArray(
                            R.array.beneficiary_name_mock
                        );
                        beneficiaryName = beneficiaryNameMock[beneficiaryBankMock.indexOf(bankInquiryPattern)];

                        try {
                            String bankIconName = String.format(
                                PropsConstantUtil.BANK_ICON_NAME_PATTER,
                                beneficiaryBankNameId
                            );
                            bankIcons = BitmapFactory.decodeStream(getAssets().open(bankIconName));
                        }catch (IOException ex){
                            bankIcons = null;
                            System.out.println("Bank Icon Not Available");
                        }

                        //call this for successfuly inquiry
                        closeFloatingKeyboard();
                        doTransferBalance();
                    }else{
                        Toast.makeText(
                            getApplicationContext(),
                            R.string.bank_data_not_found,
                            Toast.LENGTH_LONG
                        ).show();
                    }
                    //end Section For Inquiry valid bank name and va or account number
                }
            }
        });
    }

    private void setTransferTrxTo(int accountNumberIndex){
        String accountNumberAndBank = getResources().getStringArray(R.array.beneficiary_bank_mock)[accountNumberIndex];
        String[] accountNumAndBankCode = accountNumberAndBank.split("\\|");

        accountOrVaNumber = accountNumAndBankCode[0];
        beneficiaryBankNameId = Integer.parseInt(accountNumAndBankCode[1]);
        fieldInputVaOrAcNumber.setText(accountOrVaNumber);
        bankNameListField.setSelection(beneficiaryBankNameId);
    }

    //section for do Transfer Trx and get balance
    private void doTransferBalance(){
        setNexTrfActionBtnWith("half");
        rootTrfActivity.setVisibility(View.GONE);
        finalDoTrfLayout.setVisibility(View.VISIBLE);
        transferActivityMenuLabel.setText(R.string.trf_to_label);

        ViewGroup doLayoutTrf = (ViewGroup) getLayoutInflater().inflate(
            R.layout.do_bank_transfer_layout, finalDoTrfLayout
        );
        EditText trfDesc = finalDoTrfLayout.findViewById(R.id.trfDesc);
        EditText transferAmountEt = doLayoutTrf.findViewById(R.id.amountTransferEt);
        String addPrefixAsterisk = "***" + accountOrVaNumber.substring(3);
        TextView beneficiaryNameTv = doLayoutTrf.findViewById(R.id.beneficiaryNameTv);
        ImageView bankIconImageView = doLayoutTrf.findViewById(R.id.bankIconsFinalTrf);
        TextView grandTotalAmountTfrBottomTv = findViewById(R.id.grandTotalTrfAmountTv);
        TextView accountOrVaNumberTv = doLayoutTrf.findViewById(R.id.accountOrVaNumberTv);
        TextView descTrfLengthCounting = doLayoutTrf.findViewById(R.id.descTrfLengthCounting);
        TextView bankNameBeneficiaryTv = doLayoutTrf.findViewById(R.id.bankNameBeneficiaryTv);
        TextView balanceReadyToTransferTv = doLayoutTrf.findViewById(R.id.balanceReadyToTransferTv);

        beneficiaryNameTv.setText(beneficiaryName);
        bankIconImageView.setImageBitmap(bankIcons);
        accountOrVaNumberTv.setText(addPrefixAsterisk);
        bankNameBeneficiaryTv.setText(beneficiaryBankName);
        userDetails.get(new GetUserDetailsCallback() {
            @Override
            public void failure(Call call, IOException e) {
                BankTransferActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"Pemidahan Dana Tidak Dapatdilakukan, Periksa Internet", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onSuccess(UserDetailsDTO userDetailsDTO) {
                BankTransferActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        availableBalance = Long.parseLong(userDetailsDTO.getBalance());
                        balanceReadyToTransferTv.setText(IDRFormatCurr.currFormat(availableBalance));
                        nextTrfActionBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(transferAmount <= 0 || transferAmount > availableBalance || trfDesc.getText().length() > 100){
                                    Toast.makeText(getApplicationContext(), R.string.reached_limit_amount, Toast.LENGTH_SHORT).show();
                                }else{
                                    DetailTrfDto detailTrfDto = new DetailTrfDto();
                                    WithdrawReqDTO withdrawReqDTO = new WithdrawReqDTO();
                                    DataHistoricalReqDTO dataHistoricalReqDTO = new DataHistoricalReqDTO();

                                    detailTrfDto.setTransferAmount(transferAmount);
                                    detailTrfDto.setBeneficiaryName(beneficiaryName);
                                    detailTrfDto.setSaveBankInfo(storeInfBeneficiaryBank);
                                    detailTrfDto.setUserFullName(userDetailsDTO.getName());
                                    detailTrfDto.setBeneficiaryBankId(beneficiaryBankNameId);
                                    detailTrfDto.setBeneficiaryBankName(beneficiaryBankName);
                                    detailTrfDto.setAccountOrVaNumberBeneficiary(accountOrVaNumber);
                                    detailTrfDto.setTransferDescription(trfDesc.getText().toString());
                                    detailTrfDto.setTransactionDate(new Date(System.currentTimeMillis()).toString());
                                    detailTrfDto.setUserId(userDetailsDTO.getId());

                                    withdrawReqDTO.setAmount(transferAmount);
                                    withdrawReqDTO.setBankDest(beneficiaryBankName);
                                    withdrawReqDTO.setBeneficiaryName(beneficiaryName);
                                    withdrawReqDTO.setTrfDesc(detailTrfDto.getTransferDescription());
                                    withdrawReqDTO.setAccNumber(Integer.parseInt(accountOrVaNumber));

                                    //section for sending data
                                    Gson gson = new Gson();
                                    String jsonDataSending = gson.toJson(withdrawReqDTO);
                                    OkHttpClient okHttpClient = new OkHttpClient();
                                    RequestBody requestBody = RequestBody.create(jsonDataSending.getBytes());
                                    Request request = new Request.Builder()
                                    .header("Content-Type", "application/json")
                                    .header("Authorization", "Bearer " + token)
                                    .url(PathUrl.ROOT_PATH_WITHDRAW).post(requestBody).build();

                                    ProgressBarHelper.onProgress( view, true);
                                    okHttpClient.newCall(request).enqueue(new Callback() {
                                        @Override
                                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                            BankTransferActivity.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ProgressBarHelper.onProgress( view, false);
                                                    Toast.makeText(getApplicationContext(),"Silahkan Periksa Sambungan Internet, Pemindahan Dana Gagal", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }

                                        @Override
                                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                            BankTransferActivity.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ProgressBarHelper.onProgress( view, false);
                                                    if(response.isSuccessful()) {
                                                        CustomWindowPopUp popUp = new CustomWindowPopUp(getLayoutInflater(), getResources());
                                                        popUp.setMessages(R.string.success_trf_notification);
                                                        popUp.setLabelRightButton(R.string.show_detail);
                                                        popUp.setLabelLeftButton(R.string.back_to_dashboard);
                                                        popUp.showPopUp(new OnClickPopUpBtn() {
                                                            @Override
                                                            public void onClickLeftButton() {
                                                                finish();
                                                            }

                                                            @Override
                                                            public void onClickRightButton() {
                                                                if(response.body() != null){
                                                                    try {
                                                                        String resString = response.body().string();
                                                                        TypeToken<ResponseGlobalJsonDTO<WithdrawResponseDTO>> jsonDTOTypeToken = new TypeToken<ResponseGlobalJsonDTO<WithdrawResponseDTO>>(){};
                                                                        ResponseGlobalJsonDTO<WithdrawResponseDTO> globalJsonDTO = gson.fromJson(resString, jsonDTOTypeToken);
                                                                        WithdrawResponseDTO[]  withdrawResponseDTOS = globalJsonDTO.getData();

                                                                        if(withdrawResponseDTOS != null){
                                                                            detailTrfDto.setRefnumber(withdrawResponseDTOS[0].getRefnumber());
                                                                        }

                                                                        Intent intent = new Intent(getApplicationContext(), DetailBankTransfer.class);
                                                                        intent.putExtra("detailsData", gson.toJson(detailTrfDto));
                                                                        startActivity(intent);
                                                                    } catch (Exception e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            }
                                                        });
                                                    } else {
                                                        assert request.body() != null;
                                                        Toast.makeText(getApplicationContext(),"Silahkan Periksa Riwayat Transaksi, Pemindahan Dana Gagal", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
            }
        });
        transferAmountEt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                EditText fieldTrfAmount = (EditText) view;
                Editable gteEditableText = fieldTrfAmount.getText();

                if(gteEditableText.length() <= 0){
                    transferAmount = 0L;
                }else{
                    String finalAmountString = gteEditableText.toString();
                    Pattern pattern = Pattern.compile("^[0-9]{1,19}$");
                    Matcher matcher = pattern.matcher(finalAmountString);

                    if(matcher.matches()){
                        try{
                            transferAmount =  Long.parseLong(finalAmountString);
                            grandTotalAmountTfrBottomTv.setText(finalAmountString);
                            return false;
                        }catch (Exception e){
                            transferAmount = 0L;
                            grandTotalAmountTfrBottomTv.setText("0");
                            return false;
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), R.string.invalid_amount, Toast.LENGTH_SHORT).show();
                    }
                }
                grandTotalAmountTfrBottomTv.setText("0");
                return false;
            }
        });

        trfDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() > 50){
                    descTrfLengthCounting.setTextColor(getResources().getColor(R.color.orange));
                }else{
                    descTrfLengthCounting.setTextColor(getResources().getColor(R.color.calmBlack));
                }
                descTrfLengthCounting.setText(charSequence.length() + "/100");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private ViewGroup getArchiveBankList(){
        ViewGroup bankArchiveList = findViewById(R.id.bankArchiveList);
        bankArchiveList.removeAllViews();
        return bankArchiveList;
    }

    private void disabledProp(){
        finalDoTrfLayout.removeAllViews();
        bankActivityNav.setOnClickListener(null);
        nextTrfActionBtn.setVisibility(View.GONE);
        finalDoTrfLayout.setVisibility(View.GONE);
        bankNameListField.setVisibility(View.GONE);
        rootTrfActivity.setVisibility(View.VISIBLE);
        rootBottomDetailTrf.setVisibility(View.GONE);
        navActivityBankIcon.setVisibility(View.GONE);
        fieldInputVaOrAcNumber.setVisibility(View.GONE);
        transferActivityActionLabel.setVisibility(View.GONE);
        setNexTrfActionBtnWith(null);
    }

    private void closeFloatingKeyboard(){
        if(getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null){
            InputMethodManager methodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            methodManager.hideSoftInputFromWindow(
                getCurrentFocus().getWindowToken(),
                0
            );
        }
    }

    private void setNexTrfActionBtnWith(String mode){
        int paramsDef = LinearLayout.LayoutParams.WRAP_CONTENT;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, paramsDef
        );

        if(mode != null && mode.equals("half")){
            layoutParams = new LinearLayout.LayoutParams(paramsDef, paramsDef);
            rootBottomDetailTrf.setVisibility(View.VISIBLE);
        }else{
            rootBottomDetailTrf.setVisibility(View.GONE);
        }

        nextTrfActionBtn.setLayoutParams(layoutParams);
    }
}