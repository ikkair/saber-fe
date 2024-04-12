package com.desti.saber.LayoutHelper.ManualImageChoser;

import android.graphics.Bitmap;
import android.view.ViewGroup;

public interface SuccessSetImage {
    void success(Bitmap bitmap, String bitmapLoc, ViewGroup parentFileChooser);
}
