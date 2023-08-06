package com.desti.saber.utils.dto;

public class ResponseGlobalJsonDTO {
    private String status;
    private Integer statusCode;
    private DataLogInDTO[] data;
    private String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public DataLogInDTO[] getData() {
        return data;
    }

    public void setData(DataLogInDTO[] data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
