package com.desti.saber.utils.dto;

import org.jetbrains.annotations.NotNull;

public class DetailTrfDto {
    private String userFullName;
    private long transferAmount;
    private boolean saveBankInfo;
    private int beneficiaryBankId;
    private String beneficiaryBankName;
    private String accountOrVaNumberBeneficiary;
    private String transferDescription;
    private String transactionDate;
    private String beneficiaryName;
    private String refnumber;
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public String getRefnumber() {
        return refnumber;
    }

    public void setRefnumber(String refnumber) {
        this.refnumber = refnumber;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public long getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(long transferAmount) {
        this.transferAmount = transferAmount;
    }

    public boolean isSaveBankInfo() {
        return saveBankInfo;
    }

    public void setSaveBankInfo(boolean saveBankInfo) {
        this.saveBankInfo = saveBankInfo;
    }

    public int getBeneficiaryBankId() {
        return beneficiaryBankId;
    }

    public void setBeneficiaryBankId(int beneficiaryBankId) {
        this.beneficiaryBankId = beneficiaryBankId;
    }

    public String getBeneficiaryBankName() {
        return beneficiaryBankName;
    }

    public void setBeneficiaryBankName(String beneficiaryBankName) {
        this.beneficiaryBankName = beneficiaryBankName;
    }

    public String getAccountOrVaNumberBeneficiary() {
        return accountOrVaNumberBeneficiary;
    }

    public void setAccountOrVaNumberBeneficiary(String accountOrVaNumberBeneficiary) {
        this.accountOrVaNumberBeneficiary = accountOrVaNumberBeneficiary;
    }

    public String getTransferDescription() {
        return (transferDescription == null) ? "-" : transferDescription;
    }

    public void setTransferDescription(String transferDescription) {
        this.transferDescription = transferDescription;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }


    @NotNull
    public String toString() {
        return
        "Nama Pemilik Rekening  : " + getBeneficiaryName() + "\n" +
        "Tanggal Transaksi            : " + getTransactionDate() + "\n" +
        "Jumlah Transfer               : " + getTransferAmount() + "\n" +
        "Nama Bank                        : " + getBeneficiaryBankName() + "\n" +
        "No Rekening                      : " + getAccountOrVaNumberBeneficiary() + "\n" +
        "Ref Number                       : " + getRefnumber() + "\n" +
        "Deskripsi                            : " + getTransferDescription();
    }
}
