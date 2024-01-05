package com.desti.saber.configs;
import android.content.Context;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public class OkHttpHandler {

    private final OkHttpClient client;

    public OkHttpHandler() {
        this.client = new OkHttpClient();
    }

    public void requestAsync(Context context, Request request, final MyCallback callback) {
        // Asynchronous request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response){
                callback.onSuccess(context, response);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                // Handle network failures
                callback.onFailure(e);
            }
        });
    }



    // Callback interface for asynchronous responses
    public interface MyCallback {
        void onSuccess(Context context, Response response);

        void onFailure(Exception e);
    }
}

