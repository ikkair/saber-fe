package com.desti.saber;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.desti.saber.LayoutHelper.SingleArchiveBankList.OnClickArchiveBankBeneficiary;
import com.desti.saber.LayoutHelper.SingleArchiveBankList.OnClickSwitchSaved;
import com.desti.saber.LayoutHelper.SingleArchiveBankList.SingleArchiveBeneficiaryBankList;
import com.desti.saber.LayoutHelper.SingleArchiveBankList.SingleStoredBankNameToggle;
import com.desti.saber.LayoutHelper.WindowPopUp.CustomWindowPopUp;
import com.desti.saber.LayoutHelper.WindowPopUp.OnClickPopUpBtn;
import com.desti.saber.utils.ImageSetterFromStream;
import com.desti.saber.utils.constant.PropsConstantUtil;
import com.desti.saber.utils.dto.DetailTrfDto;
import com.google.gson.Gson;

import java.io.IOException;
import java.sql.Date;
import java.util.Arrays;
import java.util.HashMap;
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
    private String trackingMenuHist;
    private String beneficiaryName;
    private Button nexTrfActionBtn;
    private long transferAmount;
    private Bitmap bankIcons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_transfer);
        ViewGroup backTopButton = findViewById(R.id.backTopButton);

        nexTrfActionBtn = findViewById(R.id.nexTrfAction);
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

        setLatestBankTransferTrx();
        backTopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(trackingMenuHist != null){
                    switch (trackingMenuHist){
                        case "setLatestBankTransferTrx" :
                            setLatestBankTransferTrx();
                        break;

                        case "setTrxBankTransfer" :
                            setTrxBankTransfer();
                        break;

                        case "setTrxTransferTo" :
                            setTrxTransferTo();
                        break;
                    }
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
                bankActivityNav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setTrxBankTransfer();
                        trackingMenuHist = "setLatestBankTransferTrx";
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
                bankActivityNav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        trackingMenuHist = "setTrxBankTransfer";
                        setTrxTransferTo();
                    }
                });

                //section for call endpoint and looping bank name beneficiary stored
                for(int i =0; i < 100; i++){
                    int test = i;
                    SingleArchiveBeneficiaryBankList bankList = new SingleArchiveBeneficiaryBankList(getApplicationContext());
                    bankList.setSingleListBankArchive(null, "Sonia Cakep " + test,
                        new OnClickArchiveBankBeneficiary() {
                            @Override
                            public void onClick() {
                                Toast.makeText(getApplicationContext(), "Burung Gajah Gede " + test, Toast.LENGTH_SHORT).show();
                            }
                        }
                    );
                    archiveBankList.addView(bankList);
                }
            }
        });
    }

    //Section For Inquiry valid bank name, stored bank info and va or account number
    private void setTrxTransferTo(){
        disabledProp();

        nexTrfActionBtn.setVisibility(View.VISIBLE);
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

        nexTrfActionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(accountOrVaNumber == null || beneficiaryBankNameId <= 0){
                    Toast.makeText(
                        getApplicationContext(),
                        R.string.warn_info_empty_bank_data,
                        Toast.LENGTH_LONG
                    ).show();
                }else{

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

        //user balance available, set at below
        long availableBalance = 1000000L;
        balanceReadyToTransferTv.setText(String.valueOf(availableBalance));

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

        //section for transfer sending
        nexTrfActionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(transferAmount <= 0 || transferAmount > availableBalance || trfDesc.getText().length() > 100){
                    Toast.makeText(getApplicationContext(), R.string.reached_limit_amount, Toast.LENGTH_SHORT).show();
                }else{
                    DetailTrfDto detailTrfDto = new DetailTrfDto();
                    detailTrfDto.setUserFullName("User Login");
                    detailTrfDto.setTransferAmount(transferAmount);
                    detailTrfDto.setBeneficiaryName(beneficiaryName);
                    detailTrfDto.setSaveBankInfo(storeInfBeneficiaryBank);
                    detailTrfDto.setBeneficiaryBankId(beneficiaryBankNameId);
                    detailTrfDto.setBeneficiaryBankName(beneficiaryBankName);
                    detailTrfDto.setAccountOrVaNumberBeneficiary(accountOrVaNumber);
                    detailTrfDto.setTransferDescription(trfDesc.getText().toString());
                    detailTrfDto.setTransactionDate(new Date(System.currentTimeMillis()).toString());


                    //section for sending data
                    String jsonDataSending = new Gson().toJson(detailTrfDto);
                    System.out.println(jsonDataSending);

                    //Start show popUp if trf success
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
                            Intent intent = new Intent(getApplicationContext(), DetailBankTrasnfer.class);
                            intent.putExtra("detailsData", jsonDataSending);
                            startActivity(intent);
                        }
                    });
                    //end show popUp
                }
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
        nexTrfActionBtn.setVisibility(View.GONE);
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

        nexTrfActionBtn.setLayoutParams(layoutParams);
    }
}