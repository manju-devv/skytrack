package com.example.myapplication.api

import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.util.Log

object ApiClient {
    private const val BASE_URL = "https://aerodatabox.p.rapidapi.com/"

    private fun headersInterceptor(apiKey: String) = Interceptor { chain ->
        val req = chain.request().newBuilder()
            .addHeader("x-rapidapi-host", "aerodatabox.p.rapidapi.com")
            .addHeader("x-rapidapi-key", apiKey)
            .build()
        chain.proceed(req)
    }

    // ⭐ Interceptor to log FINAL URL
    private val urlLogger = Interceptor { chain ->
        val request = chain.request()
        Log.d("API_URL", "➡️ URL Called: ${request.url}")
        chain.proceed(request)
    }

    fun create(apiKey: String): AeroDataBoxService {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(headersInterceptor(apiKey)) // Headers
            .addInterceptor(urlLogger)                 // ⭐ URL logger
            .addInterceptor(logging)                   // Body logger
            .build()

        val gson = GsonBuilder().create()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        return retrofit.create(AeroDataBoxService::class.java)
    }
}
