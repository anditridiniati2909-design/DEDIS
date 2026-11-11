package com.example.dedis.data.repository

import com.example.dedis.data.model.ChildDto
import com.example.dedis.data.model.MeasurementDto

/**
 * Kontrak Repository per domain data (sesuai arsitektur Fase 1).
 * Layar UI hanya boleh mengonsumsi Repository, tidak boleh meracik data sendiri.
 */

interface BalitaRepository {
    suspend fun getBalitaList(): Result<List<ChildDto>>
    suspend fun getBalitaDetail(childId: Long): Result<ChildDto>
}

interface AkunRepository {
    suspend fun login(phoneOrEmail: String, pinOrPass: String): Result<Boolean>
    suspend fun getProfile(): Result<Any>
}

interface PengukuranRepository {
    suspend fun getMeasurements(childId: Long): Result<List<MeasurementDto>>
    suspend fun saveMeasurement(
        childId: Long,
        weightKg: Float,
        heightCm: Float,
        measurementMethod: String,
        measurementDate: String,
        headCircumferenceCm: Float? = null
    ): Result<MeasurementDto>
}

interface JadwalRepository {
    suspend fun getUpcomingSchedules(): Result<List<Any>>
    suspend fun confirmAttendance(scheduleId: Long): Result<Boolean>
}

interface TindakLanjutRepository {
    suspend fun saveFollowUp(childId: Long, counselingType: String, controlDate: String, personInCharge: String): Result<Boolean>
}

interface PesanRepository {
    suspend fun getConversations(): Result<List<Any>>
    suspend fun getMessages(partnerId: String): Result<List<Any>>
    suspend fun sendMessage(partnerId: String, content: String): Result<Any>
}

interface NotifikasiRepository {
    suspend fun getPreferences(): Result<Any>
    suspend fun updatePreferences(preferences: Any): Result<Boolean>
}

