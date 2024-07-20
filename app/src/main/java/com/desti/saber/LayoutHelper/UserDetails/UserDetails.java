package com.desti.saber.LayoutHelper.UserDetails;

import com.desti.saber.utils.GetUserDetailsCallback;
import com.desti.saber.utils.constant.PathUrl;
import com.desti.saber.utils.dto.ResponseGlobalJsonDTO;
import com.desti.saber.utils.dto.UserDetailsDTO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class    UserDetails {

    private String userId;

    public UserDetails(String userId) {
        this.userId = userId;
    }

    public void get(GetUserDetailsCallback callback){
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().get().url(PathUrl.ROOT_PATH_USER + "/" + userId).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.failure(call, e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    TypeToken<ResponseGlobalJsonDTO<UserDetailsDTO>> jsonDTOTypeToken = new TypeToken<ResponseGlobalJsonDTO<UserDetailsDTO>>() {
                    };
                    ResponseGlobalJsonDTO<UserDetailsDTO> globalJsonDTO = null;
                    globalJsonDTO = new Gson().fromJson(response.body().string(), jsonDTOTypeToken);
                    UserDetailsDTO[] userDetailsDTOS = globalJsonDTO.getData();

                    if (userDetailsDTOS.length > 0) {
                        callback.onSuccess(userDetailsDTOS[0]);
                    }else{
                        callback.onSuccess(null);
                    }
                }
            }
        });
    }

}
