package com.desti.saber.LayoutHelper.WindowPopUp;

import android.content.res.Resources;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.appcompat.widget.AppCompatButton;
import com.desti.saber.R;

public class CustomWindowPopUp {

    private final Resources resources;
    private String messages;
    private String labelRightButton;
    private String labelLeftButton;
    private final  PopupWindow popupWindow;
    private final View layoutPopUp;

    public CustomWindowPopUp(LayoutInflater inflate, Resources resources) {
        this.resources = resources;
        this.layoutPopUp = inflate.inflate(R.layout.popup_notif_fullscreen,null,false);
        this.popupWindow = new PopupWindow(layoutPopUp, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
    }

    public void showPopUp(OnClickPopUpBtn onClickPopUpBtn){
        AppCompatButton buttonLeft = layoutPopUp.findViewById(R.id.leftPopUpBtn);
        AppCompatButton buttonRight = layoutPopUp.findViewById(R.id.rightPopUpBtn);
        TextView messagesTv = layoutPopUp.findViewById(R.id.PopUpTitle);

        messagesTv.setText(getMessages());
        buttonLeft.setText(getLabelLeftButton());
        buttonRight.setText(getLabelRightButton());

        buttonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickPopUpBtn.onClickLeftButton();
            }
        });

        buttonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickPopUpBtn.onClickRightButton();
            }
        });

        popupWindow.showAtLocation(layoutPopUp, Gravity.CENTER, 0, 0);
    }

    public void finishPopUp(){
        popupWindow.dismiss();
    }

    public void setMessages(String messages){
        this.messages = messages;
    }

    public void setMessages(int stringId){
        this.messages = getResource(stringId);
    }

    public String getMessages() {
        return (messages == null) ? "No Messages" : messages;
    }

    public String getLabelRightButton() {
        return (labelRightButton == null) ? "Cancel" : labelRightButton;
    }

    public void setLabelRightButton(String labelRightButton) {
        this.labelRightButton = labelRightButton;
    }

    public void setLabelRightButton(int stringId) {
        this.labelRightButton = getResource(stringId);
    }

    public String getLabelLeftButton() {
        return (labelLeftButton == null) ? "Ok" : labelLeftButton;
    }

    public void setLabelLeftButton(String labelLeftButton) {
        this.labelLeftButton = labelLeftButton;
    }

    public void setLabelLeftButton(int stringId) {
        this.labelLeftButton = getResource(stringId);
    }

    private String getResource(int stringId){
        return resources.getString(stringId);
    }
}
