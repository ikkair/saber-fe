package com.desti.saber.utils.dto;

import com.desti.saber.utils.constant.ActivityStatusDetail;

public class WithdrawResponseDTO {
    private String transactionId;
    private String transactionStatus;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionStatus() {
        return (transactionStatus != null) ? ActivityStatusDetail.values()[Integer.parseInt(transactionStatus)].toString() : "-";
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }
}
