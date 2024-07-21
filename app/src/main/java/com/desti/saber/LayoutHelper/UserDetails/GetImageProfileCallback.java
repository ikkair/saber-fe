package com.desti.saber.LayoutHelper.UserDetails;

import android.graphics.Bitmap;
import okhttp3.Response;

import java.io.InputStream;

public interface GetImageProfileCallback {
    public void ioNetworkFail(Exception e);
    public void success(Bitmap bitmap);
    public void endPointFaultResponse(Response response);
}
