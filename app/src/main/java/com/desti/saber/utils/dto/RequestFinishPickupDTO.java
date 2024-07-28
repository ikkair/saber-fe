package com.desti.saber.utils.dto;

import java.util.List;

public class RequestFinishPickupDTO {
    private String hPickupId;
    private String pickupId;
    private List<String> acceptPickup;
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPickupId() {
        return pickupId;
    }

    public void setPickupId(String pickupId) {
        this.pickupId = pickupId;
    }

    public String gethPickupId() {
        return hPickupId;
    }

    public void sethPickupId(String hPickupId) {
        this.hPickupId = hPickupId;
    }

    public List<String> getAcceptPickup() {
        return acceptPickup;
    }

    public void setAcceptPickup(List<String> acceptPickup) {
        this.acceptPickup = acceptPickup;
    }
}
