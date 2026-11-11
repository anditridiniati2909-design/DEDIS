package com.example.dedis.data.network

import android.content.Context
import com.example.dedis.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * ApiClient — Singleton client Retrofit untuk komunikasi ke backend Laravel DEDIS.
 *
 * Konfigurasi:
 * - BASE_URL diambil terpusat dari BuildConfig.BASE_URL (bisa emulator 10.0.2.2 atau IP Wi-Fi).
 * - LoggingInterceptor menyaring data sensitif (Authorization header disembunyikan).
 */
object ApiClient {

    @Volatile
    private var retrofit: Retrofit? = null

    @Volatile
    private var balitaService: BalitaApiService? = null

    fun getRetrofit(context: Context, onSessionExpired: (() -> Unit)? = null): Retrofit {
        return retrofit ?: synchronized(this) {
            retrofit ?: buildRetrofit(context, onSessionExpired).also { retrofit = it }
        }
    }

    private fun buildRetrofit(context: Context, onSessionExpired: (() -> Unit)? = null): Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            // Level BASIC/HEADERS untuk debug, redacting Authorization header
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
            redactHeader("Authorization")
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context, onSessionExpired))
            .addInterceptor(loggingInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getBalitaApiService(context: Context, onSessionExpired: (() -> Unit)? = null): BalitaApiService {
        return balitaService ?: synchronized(this) {
            balitaService ?: getRetrofit(context, onSessionExpired).create(BalitaApiService::class.java).also {
                balitaService = it
            }
        }
    }
}

