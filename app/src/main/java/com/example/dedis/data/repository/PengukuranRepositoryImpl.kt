package com.example.dedis.data.repository

import com.example.dedis.data.model.MeasurementDto
import com.example.dedis.data.model.StoreMeasurementRequestDto
import com.example.dedis.data.network.BalitaApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PengukuranRepositoryImpl(
    private val apiService: BalitaApiService
) : PengukuranRepository {

    override suspend fun getMeasurements(childId: Long): Result<List<MeasurementDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getChildDetail(childId)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!.measurements ?: emptyList())
                } else {
                    Result.failure(Exception("Gagal mengambil data riwayat: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun saveMeasurement(
        childId: Long,
        weightKg: Float,
        heightCm: Float,
        measurementMethod: String,
        measurementDate: String,
        headCircumferenceCm: Float?
    ): Result<MeasurementDto> {
        return withContext(Dispatchers.IO) {
            try {
                val request = StoreMeasurementRequestDto(
                    weightKg = weightKg,
                    heightCm = heightCm,
                    measurementMethod = measurementMethod,
                    measurementDate = measurementDate,
                    headCircumferenceCm = headCircumferenceCm
                )
                val response = apiService.storeMeasurement(childId, request)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!.measurement)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Gagal menyimpan pengukuran: ${response.code()} ${errorBody ?: response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}

