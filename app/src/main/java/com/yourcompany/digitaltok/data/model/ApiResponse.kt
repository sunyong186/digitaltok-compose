package com.yourcompany.digitaltok.data.model

import com.google.gson.annotations.SerializedName

/**
 * 서버에서 오는 모든 응답을 감싸는 제네릭 데이터 클래스.
 * @param <T> 응답의 'result' 필드에 들어갈 실제 데이터의 타입
 */
data class ApiResponse<T>(
    @SerializedName("isSuccess")
    val isSuccess: Boolean,
    @SerializedName("code")
    val code: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("result")
    val result: T?
)
