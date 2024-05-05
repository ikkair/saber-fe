package com.desti.saber.utils.constant;

import android.graphics.Bitmap;
import okhttp3.Call;

import java.io.IOException;

public interface GetImageProfileCallback {
    public void fail(Call call, IOException e);
    public void success(Bitmap bitmap);
}
