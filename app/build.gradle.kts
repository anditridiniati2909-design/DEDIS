plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.dedis"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.dedis"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            // BASE_URL default emulator (10.0.2.2 mengarah ke localhost mesin host)
            // Untuk HP fisik: ganti dengan IP lokal Wi-Fi laptop (contoh: "http://192.168.1.10:8000/api/v1/")
            buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8000/api/v1/\"")
        }
        release {
            buildConfigField("String", "BASE_URL", "\"https://api.posyandu-anggrek4.id/api/v1/\"")
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // CameraX & ML Kit Barcode Scanning (Scan QR Real-Time)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.mlkit.barcode.scanning)

    // ZXing – generate QR code gambar (offline, tidak butuh kamera)
    implementation(libs.zxing.core)

    // WorkManager – jadwal notifikasi lokal (H-3 / H-1)
    implementation(libs.androidx.work.runtime)

    // EncryptedSharedPreferences – penyimpanan sesi aman berbasis Android Keystore
    implementation(libs.androidx.security.crypto)

    // Jaringan & API REST Laravel (Retrofit + OkHttp + Gson)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
