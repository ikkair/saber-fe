package com.desti.saber.utils.dto;

public class WithdrawReqDTO {
    private long amount;
    private String trfDesc;
    private int accNumber;
    private String beneficiaryName;
    private String bankDest;

    public String getBankDest() {
        return bankDest;
    }

    public void setBankDest(String bankDest) {
        this.bankDest = bankDest;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getTrfDesc() {
        return trfDesc;
    }

    public void setTrfDesc(String trfDesc) {
        this.trfDesc = trfDesc;
    }

    public int getAccNumber() {
        return accNumber;
    }

    public void setAccNumber(int accNumber) {
        this.accNumber = accNumber;
    }
}
