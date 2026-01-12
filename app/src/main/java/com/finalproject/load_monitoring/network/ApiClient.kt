package com.finalproject.load_monitoring.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    //    private const val BASE_URL = "http://YOUR_IP:8080/"
    private const val BASE_URL = "http://192.168.1.125:8080/"


    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val passengerApi: PassengerApi by lazy { // by lazy says: "Don't create the object now - only create it the first time someone needs it"
        retrofit.create(PassengerApi::class.java) // Retrofit creates a runtime implementation of PassengerApi

    }
}

