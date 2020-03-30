package com.msc.mscdictionary.API;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIRetrofit {
    private static Retrofit apiRetrofit = null;
    private static String urlRoot = "http://192.168.43.13/server/";

    public static Retrofit getAPI(){
        Gson gson = new GsonBuilder().setLenient().create();
        apiRetrofit = new Retrofit.Builder()
                .baseUrl(urlRoot)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return apiRetrofit;
    }
}
