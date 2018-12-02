package com.youcmt.youdmcapp;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Stanislav Ostrovskii on 11/2/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 * To avoid creating a new Retrofit client object for every network call.
 */

public class RetrofitClient {
    private static final String BASE_API_URL = "https://youcmt.com/api/";
    private static Retrofit sRetrofit;

    private synchronized static Retrofit getRetrofitClient()
    {
        if(sRetrofit==null)
        {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();
            sRetrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(BASE_API_URL)
                    .client(client)
                    .build();
        }
        return sRetrofit;
    }

    public static ApiEndPoint getApiEndpoint()
    {
        return getRetrofitClient().create(ApiEndPoint.class);
    }

}
