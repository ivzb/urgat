package com.ivzb.urgat.di

import com.ivzb.urgat.data.marketPrice.YahooFinanceApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .writeTimeout(5, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideYahooFinanceApi(
        okHttpClient: OkHttpClient,
        json: Json,
    ): YahooFinanceApi {
        val contentType = "application/json; charset=utf-8".toMediaType()

        val yahooFinanceClient = okHttpClient
            .newBuilder()
            .addInterceptor(
                Interceptor { chain ->
                    val request = chain.request().newBuilder()
                        .header("User-Agent", "Mozilla/5.0")
                        .build()
                    chain.proceed(request)
                }
            )
            .build()

        return Retrofit.Builder()
            .baseUrl(YahooFinanceApi.BASE_URL)
            .client(yahooFinanceClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(YahooFinanceApi::class.java)
    }
}
