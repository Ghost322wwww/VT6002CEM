package com.example.newproject;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GPTClient {
    private static Retrofit retrofit;

    public static GPTService getGPTService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.openai.com/v1/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(GPTService.class);
    }
}
