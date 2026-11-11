package com.example.dedis.data.repository

import com.example.dedis.data.model.ChildDto
import com.example.dedis.data.network.BalitaApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * BalitaRepositoryImpl — Implementasi BalitaRepository yang menghubungkan aplikasi ke REST API Laravel.
 */
class BalitaRepositoryImpl(
    private val apiService: BalitaApiService
) : BalitaRepository {

    override suspend fun getBalitaList(): Result<List<ChildDto>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getChildren()
            if (response.isSuccessful) {
                val list = response.body() ?: emptyList()
                Result.success(list)
            } else {
                Result.failure(Exception("Gagal memuat data dari server (${response.code()}): ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Tidak dapat terhubung ke server. Periksa koneksi backend Anda: ${e.localizedMessage}"))
        }
    }

    override suspend fun getBalitaDetail(childId: Long): Result<ChildDto> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getChildDetail(childId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Data balita tidak ditemukan (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Gagal memuat detail balita: ${e.localizedMessage}"))
        }
    }
}

