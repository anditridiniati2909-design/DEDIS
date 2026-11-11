package com.example.dedis.data.network

import android.content.Context
import com.example.dedis.util.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * AuthInterceptor — Menyisipkan token otorisasi Bearer ke setiap request Retrofit
 * dan menangani respons 401 Unauthorized secara terpusat.
 *
 * Keamanan:
 * - Mengambil token dari SessionManager (EncryptedSharedPreferences).
 * - Tidak mencetak token atau header otorisasi ke logcat.
 */
class AuthInterceptor(
    private val context: Context,
    private val onSessionExpired: (() -> Unit)? = null
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val sessionManager = SessionManager.getInstance(context)
        val token = sessionManager.getAuthToken()

        val requestBuilder = originalRequest.newBuilder()
            .header("Accept", "application/json")

        if (!token.isNullOrBlank()) {
            requestBuilder.header("Authorization", "Bearer $token")
        }

        val response = chain.proceed(requestBuilder.build())

        // Tangani jika token kedaluwarsa atau ditolak server (401)
        if (response.code == 401) {
            sessionManager.handleSessionExpired()
            onSessionExpired?.invoke()
        }

        return response
    }
}

