package com.desti.saber.utils.dto;

import androidx.annotation.NonNull;
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
    private String referenceNumber;


    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
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
        return "Nama Bank\t: " + getBeneficiaryBankName() + "\n" +
        "No Rekening\t: " + getAccountOrVaNumberBeneficiary() +"\n" +
        "Nama\t: " + getBeneficiaryName() + "\n" +
        "Tanggal Transaksi\t: " + getTransactionDate() + "\n" +
        "No Transaksi\t: " + getReferenceNumber() + "\n" +
        "Jumlah Transfer\t: " + getTransferAmount() + "\n" +
        "Deskripsi\t: " + getTransferDescription();
    }
}
