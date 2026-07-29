package com.yourcompany.digitaltok.data.model

import com.google.gson.annotations.SerializedName

/**
 * 서버에 장치 등록을 요청할 때 보내는 데이터 모델 (Swagger 기준)
 */
data class DeviceRegistrationRequest(
    @SerializedName("nfcUid")
    val nfcUid: String
)

/**
 * 장치 등록 성공 시, ApiResponse의 result 필드에 담겨 오는 실제 데이터 모델 (Swagger 기준)
 */
data class DeviceData(
    @SerializedName("deviceId")
    val deviceId: Int,

    @SerializedName("registeredAt")
    val registeredAt: String,

    @SerializedName("unregisteredAt")
    val unregisteredAt: String?,

    @SerializedName("status")
    val status: String
)
