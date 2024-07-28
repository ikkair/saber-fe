package com.desti.saber.utils.dto;

public class PickupProgress extends PickupDetailDto{
    String helper_pickup_id;
    String pickup_id;
    String courier_id;
    String start_pickup;
    String finish_pickup;
    String pickup_progress;
    String pickup_evidence;

    public String getHelper_pickup_id() {
        return helper_pickup_id;
    }

    public void setHelper_pickup_id(String helper_pickup_id) {
        this.helper_pickup_id = helper_pickup_id;
    }

    public String getPickup_id() {
        return pickup_id;
    }

    public void setPickup_id(String pickup_id) {
        this.pickup_id = pickup_id;
    }

    public String getCourier_id() {
        return courier_id;
    }

    public void setCourier_id(String courier_id) {
        this.courier_id = courier_id;
    }

    public String getStart_pickup() {
        return start_pickup;
    }

    public void setStart_pickup(String start_pickup) {
        this.start_pickup = start_pickup;
    }

    public String getFinish_pickup() {
        return finish_pickup;
    }

    public void setFinish_pickup(String finish_pickup) {
        this.finish_pickup = finish_pickup;
    }

    public String getPickup_progress() {
        return pickup_progress;
    }

    public void setPickup_progress(String pickup_progress) {
        this.pickup_progress = pickup_progress;
    }

    public String getPickup_evidence() {
        return pickup_evidence;
    }

    public void setPickup_evidence(String pickup_evidence) {
        this.pickup_evidence = pickup_evidence;
    }

}
