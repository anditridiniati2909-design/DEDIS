package com.example.dedis.util

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.work.WorkManager

enum class SplashDestination {
    BERANDA_ORANG_TUA,
    BERANDA_KADER,
    ONBOARDING,
    ROLE_SELECTION
}

/**
 * SessionManager — Pengelola sesi terpusat dengan EncryptedSharedPreferences (Android Keystore).
 *
 * Aturan keamanan:
 * - Token disimpan terenkripsi (AES256_GCM).
 * - TIDAK PERNAH menyimpan kata sandi.
 * - TIDAK PERNAH mencetak token ke logcat.
 * - Offline-first: verifikasi lokal instan tanpa menunggu koneksi internet.
 */
class SessionManager(private val context: Context) {

    private val masterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val prefs: SharedPreferences by lazy {
        try {
            EncryptedSharedPreferences.create(
                context,
                "dedis_secure_session",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (_: Exception) {
            // Fallback jika terjadi kendala Keystore pada perangkat tertentu
            context.getSharedPreferences("dedis_session_fallback", Context.MODE_PRIVATE)
        }
    }

    companion object {
        const val ROLE_ORANG_TUA = "orang_tua"
        const val ROLE_KADER = "kader"

        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_PHONE = "user_phone"
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        private const val KEY_SESSION_EXPIRED_FLAG = "session_expired_flag"

        @Volatile
        private var INSTANCE: SessionManager? = null

        fun getInstance(context: Context): SessionManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SessionManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    /** Simpan sesi setelah login/aktivasi berhasil */
    fun saveSession(token: String, role: String, userName: String = "", phone: String = "") {
        prefs.edit()
            .putString(KEY_AUTH_TOKEN, token)
            .putString(KEY_USER_ROLE, role)
            .putString(KEY_USER_NAME, userName)
            .putString(KEY_USER_PHONE, phone)
            .putBoolean(KEY_SESSION_EXPIRED_FLAG, false)
            .apply()
    }

    fun isLoggedIn(): Boolean {
        val token = prefs.getString(KEY_AUTH_TOKEN, null)
        return !token.isNullOrBlank()
    }

    fun getUserRole(): String? = prefs.getString(KEY_USER_ROLE, null)

    fun getUserName(): String = prefs.getString(KEY_USER_NAME, "") ?: ""

    fun getUserPhone(): String = prefs.getString(KEY_USER_PHONE, "") ?: ""

    fun getAuthToken(): String? = prefs.getString(KEY_AUTH_TOKEN, null)

    /** Menandai Onboarding sudah pernah diselesaikan (tidak akan muncul lagi) */
    fun setOnboardingCompleted() {
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, true).apply()
    }

    fun isOnboardingCompleted(): Boolean = prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)

    /**
     * Menentukan rute tujuan awal saat Splash Screen dibuka (Satu tempat terpusat).
     */
    fun determineInitialDestination(): SplashDestination {
        if (isLoggedIn()) {
            return when (getUserRole()) {
                ROLE_KADER -> SplashDestination.BERANDA_KADER
                else -> SplashDestination.BERANDA_ORANG_TUA
            }
        }

        return if (!isOnboardingCompleted()) {
            SplashDestination.ONBOARDING
        } else {
            SplashDestination.ROLE_SELECTION
        }
    }

    /**
     * Dipanggil saat respons 401 Unauthorized (token ditolak server).
     */
    fun handleSessionExpired(): String {
        prefs.edit()
            .remove(KEY_AUTH_TOKEN)
            .remove(KEY_USER_ROLE)
            .putBoolean(KEY_SESSION_EXPIRED_FLAG, true)
            .apply()
        return "Sesi berakhir, silakan masuk lagi."
    }

    fun consumeSessionExpiredNotice(): Boolean {
        val wasExpired = prefs.getBoolean(KEY_SESSION_EXPIRED_FLAG, false)
        if (wasExpired) {
            prefs.edit().putBoolean(KEY_SESSION_EXPIRED_FLAG, false).apply()
        }
        return wasExpired
    }

    /**
     * Logout Terpusat:
     * 1. Hapus token, peran, dan profil yang tersimpan
     * 2. Batalkan antrean pengingat WorkManager
     * 3. Lepas token FCM
     * 4. Mempertahankan penanda onboarding agar tidak berulang
     */
    fun logout() {
        prefs.edit()
            .remove(KEY_AUTH_TOKEN)
            .remove(KEY_USER_ROLE)
            .remove(KEY_USER_NAME)
            .remove(KEY_USER_PHONE)
            .putBoolean(KEY_SESSION_EXPIRED_FLAG, false)
            .apply()

        // Batalkan seluruh pengingat posyandu
        try {
            WorkManager.getInstance(context).cancelAllWork()
        } catch (_: Exception) {}

        // Lepas registrasi token push notification
        PushNotificationHelper.unregisterToken()
    }
}

