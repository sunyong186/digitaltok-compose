package com.yourcompany.digitaltok.data.model

import com.google.gson.annotations.SerializedName

data class UserMeResult(
    @SerializedName("userId") val userId: Long,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("email") val email: String
)
