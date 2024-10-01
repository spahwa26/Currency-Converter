package com.app.currencyconverter.di

import com.app.currencyconverter.BuildConfig.DEBUG
import com.app.currencyconverter.data.models.Secrets
import com.app.currencyconverter.data.network.CurrencyApi
import com.app.currencyconverter.utils.Constants.AUTHORIZATION
import com.app.currencyconverter.utils.Constants.NUM_30
import com.app.currencyconverter.utils.Constants.TOKEN
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {


    @Provides
    @Singleton
    fun provideTokenInterceptor(secrets: Secrets) = Interceptor { chain ->
        val newRequest = chain.request().newBuilder()
        newRequest.addHeader(AUTHORIZATION, TOKEN + secrets.apiKey)
        chain.proceed(newRequest.build())
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(tokenInterceptor: Interceptor): OkHttpClient {
        val client = OkHttpClient.Builder()
            .readTimeout(NUM_30, TimeUnit.SECONDS)
            .writeTimeout(NUM_30, TimeUnit.SECONDS)
            .connectTimeout(NUM_30, TimeUnit.SECONDS)
            .addInterceptor(tokenInterceptor)


        if (DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            client.addInterceptor(logging)
        }

        return client.build()
    }

    @Provides
    @Singleton
    fun provideApi(client: OkHttpClient, secrets: Secrets): CurrencyApi {
        val builder = Retrofit.Builder()
            .baseUrl(secrets.baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
        builder.client(client)
        return builder.build().create(CurrencyApi::class.java)
    }

}