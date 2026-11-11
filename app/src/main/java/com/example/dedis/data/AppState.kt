package com.example.dedis.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * AppState — State bersama aplikasi (Fase 1).
 * Mengelola pilihan balita/anak aktif antar-layar dan sesi runtime.
 */
object AppState {
    // Sisi Orang Tua: anak yang sedang dipilih pada chip (default anak pertama)
    var selectedChildIdOrangTua by mutableStateOf<Long?>(1L)
    var selectedChildNameOrangTua by mutableStateOf("Aisyah Putri")

    // Sisi Kader: balita yang sedang dipilih dari Daftar Balita atau Hasil Scan QR
    var selectedBalitaIdKader by mutableStateOf<Long?>(null)
    var selectedBalitaNameKader by mutableStateOf<String?>(null)

    // Hasil pengukuran terbaru setelah kader menyimpan pengukuran (Fase 2)
    var latestMeasurementResult by mutableStateOf<com.example.dedis.data.model.MeasurementDto?>(null)
    var latestMeasurementChild by mutableStateOf<com.example.dedis.data.model.ChildDto?>(null)

    // Flag penyegaran data otomatis saat kembali ke Daftar / Detail balita
    var shouldRefreshBalitaList by mutableStateOf(false)
    var shouldRefreshBalitaDetail by mutableStateOf(false)
}

