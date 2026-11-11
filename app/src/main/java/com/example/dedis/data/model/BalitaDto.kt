package com.example.dedis.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) untuk Balita dari API Laravel /api/v1/children
 */
data class ChildDto(
    @SerializedName("id")
    val id: Long,

    @SerializedName("card_code")
    val cardCode: String? = null,

    @SerializedName("name")
    val name: String,

    @SerializedName("gender")
    val gender: String, // "L" atau "P"

    @SerializedName("birth_date")
    val birthDate: String? = null,

    @SerializedName("age_in_months")
    val ageInMonths: Int = 0,

    @SerializedName("age_label")
    val ageLabel: String = "",

    @SerializedName("mother_name")
    val motherName: String = "",

    @SerializedName("hamlet")
    val hamlet: String? = null,

    @SerializedName("dusun")
    val dusun: String? = null,

    @SerializedName("is_mbg_recipient")
    val isMbgRecipient: Boolean = false,

    @SerializedName("latest_measurement")
    val latestMeasurement: MeasurementDto? = null,

    @SerializedName("measurements")
    val measurements: List<MeasurementDto>? = null
) {
    val initial: String
        get() = name.trim().firstOrNull()?.uppercase() ?: "?"

    val subtitleLabel: String
        get() = "${ageInMonths} bln • Ibu $motherName"

    val genderLabel: String
        get() = if (gender.equals("L", ignoreCase = true)) "Laki-laki" else "Perempuan"

    val statusBadge: String
        get() = when (latestMeasurement?.statusHfa?.lowercase()) {
            "normal" -> "Normal"
            "pendek" -> "Perlu perhatian"
            "sangat_pendek" -> "Risiko tinggi"
            else -> if (latestMeasurement != null) "Normal" else "Belum diukur"
        }
}

/**
 * Data Transfer Object (DTO) untuk Pengukuran Balita
 */
data class MeasurementDto(
    @SerializedName("id")
    val id: Long,

    @SerializedName("child_id")
    val childId: Long,

    @SerializedName("measurement_date")
    val measurementDate: String? = null,

    @SerializedName("age_in_months")
    val ageInMonths: Int = 0,

    @SerializedName("weight_kg")
    val weightKg: Float = 0f,

    @SerializedName("height_cm")
    val heightCm: Float = 0f,

    @SerializedName("head_circumference_cm")
    val headCircumferenceCm: Float? = null,

    @SerializedName("measurement_method")
    val measurementMethod: String? = null, // "standing" / "recumbent"

    @SerializedName("z_score_hfa")
    val zScoreHfa: Float? = null,

    @SerializedName("z_score_wfa")
    val zScoreWfa: Float? = null,

    @SerializedName("z_score_wfh")
    val zScoreWfh: Float? = null,

    @SerializedName("status_hfa")
    val statusHfa: String? = null,

    @SerializedName("status_wfa")
    val statusWfa: String? = null,

    @SerializedName("status_wfh")
    val statusWfh: String? = null,

    @SerializedName("needs_attention")
    val needsAttention: Boolean = false,

    @SerializedName("attention_reason")
    val attentionReason: String? = null
)

/**
 * Request payload untuk mencatat pengukuran baru (POST /api/v1/children/{id}/measurements)
 */
data class StoreMeasurementRequestDto(
    @SerializedName("weight_kg")
    val weightKg: Float,

    @SerializedName("height_cm")
    val heightCm: Float,

    @SerializedName("measurement_method")
    val measurementMethod: String, // "standing" atau "recumbent"

    @SerializedName("measurement_date")
    val measurementDate: String, // "YYYY-MM-DD"

    @SerializedName("head_circumference_cm")
    val headCircumferenceCm: Float? = null
)

/**
 * Response dari backend setelah mencatat pengukuran
 */
data class StoreMeasurementResponseDto(
    @SerializedName("message")
    val message: String? = null,

    @SerializedName("measurement")
    val measurement: MeasurementDto,

    @SerializedName("warnings")
    val warnings: List<Any>? = null
)

