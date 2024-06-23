package com.desti.saber.LayoutHelper.CustomFileChooser;

import android.view.View;
import android.view.ViewGroup;

import java.io.File;

public interface ObjectOnClick {
    void onFileClick(File file, ViewGroup parentFileChooser);
    void onDirectoryClick(File file,  ViewGroup parentFileChooser);
    void onCancelPressed(View view);
    void onDirectoryHoldPress(File file,  ViewGroup parentFileChooser, String absolutePathLocation);
}
