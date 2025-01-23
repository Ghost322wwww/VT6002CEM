package com.example.newproject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface GPTService {
    @Headers({
            "Authorization: Bearer YOUR_API_KEY",  // <-- 這裡填入你的 GPT API 金鑰
            "Content-Type: application/json"
    })
    @POST("https://api.openai.com/v1/completions")
    Call<GPTResponse> sendMessage(@Body GPTRequest request);
}
