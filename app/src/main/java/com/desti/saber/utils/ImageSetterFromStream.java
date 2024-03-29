package com.desti.saber.utils;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.io.InputStream;

public class ImageSetterFromStream {

    private final Activity activity;
    private final AssetManager asm;

    public ImageSetterFromStream(Activity activity) {
        this.activity = activity;
        this.asm = activity.getAssets();
    }

    public void setAsImageDrawable(String imageName, int imageViewComponentId){
        try{
            InputStream ism = asm.open(imageName);
            ImageView imv = activity.findViewById(imageViewComponentId);
            imv.setImageDrawable(Drawable.createFromStream(ism, null));
        }catch (Exception err){
            System.out.println(err.getLocalizedMessage());
        }
    }

    public void setAsImageDrawable(String imageName, ImageView imageView){
        try{
            InputStream ism = asm.open(imageName);
            imageView.setImageDrawable(Drawable.createFromStream(ism, null));
        }catch (Exception err){
            System.out.println(err.getLocalizedMessage());
        }
    }

    public void setAsImageBackground(String imageName, int viewGroupComponent){
        try{
            InputStream ism = asm.open(imageName);
            ViewGroup vgp = (ViewGroup) activity.findViewById(viewGroupComponent);
            vgp.setBackground(Drawable.createFromStream(ism, null));
        }catch (Exception err){
            System.out.println(err.getLocalizedMessage());
        }
    }

    public void setAsImageBackground(String imageName, Object viewGroupComponent){
        try{

            InputStream ism = asm.open(imageName);
            Drawable imgDraw = Drawable.createFromStream(ism, null);
            if(viewGroupComponent instanceof Button){
                ((Button) viewGroupComponent).setBackground(imgDraw);
            }else if (viewGroupComponent instanceof ViewGroup){
                ((ViewGroup) viewGroupComponent).setBackground(imgDraw);
            }
        }catch (Exception err){
            System.out.println(err.getLocalizedMessage());
        }
    }
}
