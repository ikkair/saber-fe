package com.desti.saber;

import android.app.Activity;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.desti.saber.LayoutHelper.SingleArchiveBankList.OnClickArchiveBankBeneficiary;
import com.desti.saber.LayoutHelper.SingleArchiveBankList.OnClickSwitchSaved;
import com.desti.saber.LayoutHelper.SingleArchiveBankList.SingleArchiveBeneficiaryBankList;
import com.desti.saber.LayoutHelper.SingleArchiveBankList.SingleStoredBankNameToggle;
import com.desti.saber.utils.ImageSetterFromStream;

import java.util.Arrays;
import java.util.List;

public class BankTransferActivity extends AppCompatActivity {

    private TextView transferActivityActionLabel;
    private TextView transferActivityMenuLabel;
    private TextView transferActivityNavLabel;
    private ImageSetterFromStream imageSetter;
    private EditText fieldInputVaOrAcNumber;
    private boolean storeInfBeneficiaryBank;
    private TextView transferActivityLabel;
    private ImageView navActivityBankIcon;
    private int beneficiaryBankNameId;
    private ViewGroup bankActivityNav;
    private String accountOrVaNumber;
    private int navActivityBankIconId;
    private Spinner bankNameListField;
    private String trackingMenuHist;
    private Button nexTrfActionBtn;
    private String beneficiaryBankName;
    private String beneficiaryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_transfer);
        ViewGroup backTopButton = findViewById(R.id.backTopButton);

        nexTrfActionBtn = findViewById(R.id.nexTrfAction);
        imageSetter = new ImageSetterFromStream(this);
        bankActivityNav = findViewById(R.id.bankActivityNav);
        navActivityBankIconId = R.id.transferActivityNavBankIcon;
        bankNameListField = findViewById(R.id.bankNameListField);
        navActivityBankIcon = findViewById(navActivityBankIconId);
        transferActivityLabel = findViewById(R.id.transferActivityLabel);
        fieldInputVaOrAcNumber = findViewById(R.id.fieldInputVaOrAcNumber);
        transferActivityNavLabel = findViewById(R.id.transferActivityNavLabel);
        transferActivityMenuLabel = findViewById(R.id.transferActivityMenuLabel);
        transferActivityActionLabel = findViewById(R.id.transferActivityActionLabel);

        setLatestBankTransferTrx();
        backTopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

    //section for set transfer
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
                        R.string.warn_info_empyt_bank_data,
                        Toast.LENGTH_LONG
                    ).show();
                }else{
                    String bankInquiryPattern = accountOrVaNumber+"|"+beneficiaryBankNameId;
                    List<String> beneficiaryBankMock = Arrays.asList(
                        getResources().getStringArray(R.array.beneficiary_bank_mock)
                    );

                    if(beneficiaryBankMock.contains(bankInquiryPattern)){
                        String[] beneficiaryNameMock = getResources().getStringArray(
                            R.array.beneficiary_name_mock
                        );
                        beneficiaryBankName = beneficiaryNameMock[beneficiaryBankMock.indexOf(bankInquiryPattern)];

                        closeFloatingKeyboard();
                        doTransferBalance();
                    }else{
                        Toast.makeText(
                            getApplicationContext(),
                            R.string.bank_data_not_found,
                            Toast.LENGTH_LONG
                        ).show();
                    }
                }
            }
        });
    }

    private void doTransferBalance(){
        disabledProp();
        //
    }

    private ViewGroup getArchiveBankList(){
        ViewGroup bankArchiveList = findViewById(R.id.bankArchiveList);
        bankArchiveList.removeAllViews();
        return bankArchiveList;
    }

    private void disabledProp(){
        bankActivityNav.setOnClickListener(null);
        nexTrfActionBtn.setVisibility(View.GONE);
        bankNameListField.setVisibility(View.GONE);
        navActivityBankIcon.setVisibility(View.GONE);
        fieldInputVaOrAcNumber.setVisibility(View.GONE);
        transferActivityActionLabel.setVisibility(View.GONE);
    }

    private void closeFloatingKeyboard(){
        if(getCurrentFocus().getWindowToken() != null){
            InputMethodManager methodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            methodManager.hideSoftInputFromWindow(
                getCurrentFocus().getWindowToken(),
                0
            );
        }
    }
}