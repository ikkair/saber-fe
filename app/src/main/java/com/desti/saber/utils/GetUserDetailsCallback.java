package com.desti.saber.utils;

import com.desti.saber.utils.dto.UserDetailsDTO;
import okhttp3.Call;

import java.io.IOException;

public interface GetUserDetailsCallback {
    public void failure(Call call, IOException e);
    public void onSuccess(UserDetailsDTO userDetailsDTO);
}
