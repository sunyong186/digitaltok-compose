package com.yourcompany.digitaltok.data.repository

import com.yourcompany.digitaltok.data.model.*
import com.yourcompany.digitaltok.data.network.RetrofitClient
import okhttp3.MultipartBody
import okhttp3.ResponseBody

class ImageRepository {
    private val apiService = RetrofitClient.apiService
    private val publicApiService = RetrofitClient.publicApiService

    suspend fun uploadImage(
        imageName: String,
        imageFile: MultipartBody.Part
    ): Result<ImageUploadResult> {
        kotlinx.coroutines.delay(500)
        return Result.success(
            ImageUploadResult(
                image = ImageDetails(
                    imageId = 1,
                    originalUrl = "https://picsum.photos/250/122?random=1",
                    previewUrl = "https://picsum.photos/250/122?random=1",
                    einkDataUrl = "https://picsum.photos/250/122?random=1",
                    category = "USER_UPLOAD",
                    imageName = imageName,
                    createdAt = "2024-01-01",
                    deletedAt = null,
                    subwayTemplateId = 0
                ),
                imageMapping = ImageMapping(
                    userImageId = 1,
                    userId = 1,
                    imageId = 1,
                    isFavorite = false,
                    savedAt = "2024-01-01",
                    lastUsedAt = "2024-01-01"
                )
            )
        )
    }

    suspend fun updateFavoriteStatus(imageId: String, isFavorite: Boolean): Result<Unit> {
        kotlinx.coroutines.delay(500)
        return Result.success(Unit)
    }

    suspend fun getRecentImages(): Result<RecentImagesResponse> {
        kotlinx.coroutines.delay(500)
        return Result.success(
            RecentImagesResponse(
                count = 2,
                items = listOf(
                    RecentImage(1, "https://picsum.photos/250/122?random=2", "종로3가 이미지 1", false, "2024-01-01"),
                    RecentImage(2, "https://picsum.photos/250/122?random=3", "종로3가 이미지 2", true, "2024-01-02")
                )
            )
        )
    }

    suspend fun getImagePreview(imageId: Int): Result<ImagePreview> {
        kotlinx.coroutines.delay(500)
        return Result.success(ImagePreview(imageId = imageId, previewUrl = "https://picsum.photos/250/122?random=4", updatedAt = "2024-01-01"))
    }

    suspend fun getImageBinaryInfo(imageId: Int): Result<ImageBinaryInfo> {
        kotlinx.coroutines.delay(500)
        return Result.success(
            ImageBinaryInfo(
                imageId = imageId,
                einkDataUrl = "https://picsum.photos/250/122?random=5",
                lastUsedAt = "2024-01-01",
                meta = ImageMeta(
                    width = 250,
                    height = 122,
                    bpp = 1,
                    palette = "BW",
                    packing = "LSB",
                    scan = "HORIZONTAL",
                    payloadBytes = 4000,
                    hasHeader = true
                )
            )
        )
    }

    suspend fun downloadImageBinary(url: String): Result<ResponseBody> {
        return try {
            val response = publicApiService.downloadImageBinary(url)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Download failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
