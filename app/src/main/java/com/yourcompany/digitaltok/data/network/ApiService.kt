package com.yourcompany.digitaltok.data.network

import com.yourcompany.digitaltok.data.model.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

/**
 * 서버 API와 통신하기 위한 Retrofit 인터페이스
 *
 * Auth는 "/api/v1/..." 절대경로로 고정 (baseUrl에 /api 포함 여부 때문에 404 나는 것 방지)
 */
interface ApiService {

    // ==========================
    // Auth (인증)
    // ==========================

    @POST("/api/v1/auth/signup")
    suspend fun signup(
        @Body body: SignupRequest
    ): Response<ApiResponse<SignupResult>>

    @POST("/api/v1/auth/login")
    suspend fun login(
        @Body body: LoginRequest
    ): Response<ApiResponse<LoginResult>>

    @POST("/api/v1/auth/duplicate-check")
    suspend fun duplicateCheck(
        @Body body: DuplicateCheckRequest
    ): Response<ApiResponse<String>>

    @POST("/api/v1/auth/password/reset")
    suspend fun passwordReset(
        @Body body: PasswordResetRequest
    ): Response<ApiResponse<String>>

    @HTTP(method = "DELETE", path = "/api/v1/auth/logout", hasBody = true)
    suspend fun logout(
        @Body body: LogoutRequest
    ): Response<ApiResponse<String>>

    // ==========================
    // Devices
    // ==========================
    @POST("/api/v1/devices")
    suspend fun registerDevice(
        @Body request: DeviceRegistrationRequest
    ): Response<ApiResponse<DeviceData>>

    @GET("/api/v1/devices/{nfcUid}")
    suspend fun getDeviceByNfcUid(
        @Path("nfcUid") nfcUid: String
    ): Response<ApiResponse<DeviceData>>

    @DELETE("/api/v1/devices/nfc/{nfcUid}")
    suspend fun deleteDeviceByNfcUid(
        @Path("nfcUid") nfcUid: String
    ): Response<ApiResponse<DeviceData>>

    // ==========================
    // Images
    // ==========================
    @Multipart
    @POST("images")
    suspend fun uploadImage(
        @Query("imageName") imageName: String,
        @Part file: MultipartBody.Part
    ): Response<ApiResponse<ImageUploadResult>>

    @PATCH("images/{imageId}/favorite")
    suspend fun updateFavoriteStatus(
        @Path("imageId") imageId: String,
        @Body payload: Map<String, Boolean>
    ): Response<ApiResponse<Unit>>

    @GET("images/recent")
    suspend fun getRecentImages(): Response<ApiResponse<RecentImagesResponse>>

    @GET("images/{imageId}/preview")
    suspend fun getImagePreview(@Path("imageId") imageId: Int): Response<ApiResponse<ImagePreview>>

    @GET("images/{imageId}/binary")
    suspend fun getImageBinaryInfo(@Path("imageId") imageId: Int): Response<ApiResponse<ImageBinaryInfo>>

    @Streaming
    @GET
    suspend fun downloadImageBinary(@Url url: String): Response<ResponseBody>

    // ==========================
    // Priority
    // ==========================
    @GET("templates/priority")
    suspend fun getPriorityTemplates(): Response<ApiResponse<PriorityTemplateResponse>>

    @GET("templates/priority/{templateId}")
    suspend fun getPriorityTemplateDetail(
        @Path("templateId") templateId: Int
    ): Response<ApiResponse<PriorityTemplateDetail>>

    // ==========================
    // Subway
    // ==========================
    @POST("templates/subway/generate")
    suspend fun generateSubwayTemplate(
        @Body body: SubwayGenerateRequest
    ): Response<ApiResponse<String>>

    @GET("templates/subway")
    suspend fun getSubwayTemplates(): Response<ApiResponse<SubwayTemplateResponse>>

    @GET("templates/subway/{templateId}")
    suspend fun getSubwayTemplateDetail(
        @Path("templateId") templateId: Int
    ): Response<ApiResponse<SubwayTemplateDetail>>

    @GET("templates/subway/search")
    suspend fun searchSubwayTemplates(
        @Query("keyword") keyword: String
    ): Response<ApiResponse<SubwayTemplateResponse>>
}
