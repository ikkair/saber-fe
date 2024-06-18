package com.desti.saber.utils.dto;

public class MainHistoricalDataDTO {
    private String activityTitleGroup;
    private String activityDate;
    private String activityTitleDetail;
    private String totalBalance;
    private int activityStatus;
    private String activityDesc;


    public String getActivityTitleGroup() {
        return activityTitleGroup;
    }

    public void setActivityTitleGroup(String activityTitleGroup) {
        this.activityTitleGroup = activityTitleGroup;
    }

    public String getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(String activityDate) {
        this.activityDate = activityDate;
    }

    public String getActivityTitleDetail() {
        return activityTitleDetail;
    }

    public void setActivityTitleDetail(String activityTitleDetail) {
        this.activityTitleDetail = activityTitleDetail;
    }

    public String getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(String totalBalance) {
        this.totalBalance = totalBalance;
    }

    public int getActivityStatus() {
        return activityStatus;
    }

    public void setActivityStatus(int activityStatus) {
        this.activityStatus = activityStatus;
    }

    public String getActivityDesc() {
        return activityDesc;
    }

    public void setActivityDesc(String activityDesc) {
        this.activityDesc = activityDesc;
    }
}
