package com.yourcompany.digitaltok.data.repository

import com.yourcompany.digitaltok.data.model.PriorityTemplate
import com.yourcompany.digitaltok.data.model.PriorityTemplateDetail
import com.yourcompany.digitaltok.data.model.SubwayGenerateRequest
import com.yourcompany.digitaltok.data.model.SubwayTemplateDetail
import com.yourcompany.digitaltok.data.model.SubwayTemplateResponse
import com.yourcompany.digitaltok.data.network.RetrofitClient

class TemplateRepository {

    private val apiService = RetrofitClient.apiService

    suspend fun getPriorityTemplates(): Result<List<PriorityTemplate>> = try {
        val response = apiService.getPriorityTemplates()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null && body.isSuccess && body.result != null) {
                Result.success(body.result.items)
            } else {
                val errorMessage = body?.message ?: "Unknown error"
                Result.failure(Exception(errorMessage))
            }
        } else {
            val errorBody = response.errorBody()?.string() ?: "Unknown HTTP error"
            Result.failure(Exception("API Error ${response.code()}: $errorBody"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getPriorityTemplateDetail(templateId: Int): Result<PriorityTemplateDetail> = try {
        val response = apiService.getPriorityTemplateDetail(templateId)
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null && body.isSuccess && body.result != null) {
                Result.success(body.result)
            } else {
                val errorMessage = body?.message ?: "Unknown error"
                Result.failure(Exception(errorMessage))
            }
        } else {
            val errorBody = response.errorBody()?.string() ?: "Unknown HTTP error"
            Result.failure(Exception("API Error ${response.code()}: $errorBody"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun generateSubwayTemplate(request: SubwayGenerateRequest): Result<String> {
        return try {
            val response = apiService.generateSubwayTemplate(request)
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse != null && apiResponse.isSuccess) {
                    apiResponse.result?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("API success but result data is missing for subway template generation."))
                } else {
                    Result.failure(Exception(apiResponse?.message ?: "Unknown API error during subway template generation."))
                }
            } else {
                Result.failure(Exception("Failed to generate subway template: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSubwayTemplates(): Result<SubwayTemplateResponse> {
        return try {
            val response = apiService.getSubwayTemplates()
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse != null && apiResponse.isSuccess) {
                    apiResponse.result?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("API success but result data is missing for subway templates."))
                } else {
                    Result.failure(Exception(apiResponse?.message ?: "Unknown API error getting subway templates."))
                }
            } else {
                Result.failure(Exception("Failed to fetch subway templates: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSubwayTemplateDetail(templateId: Int): Result<SubwayTemplateDetail> {
        return try {
            val response = apiService.getSubwayTemplateDetail(templateId)
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse != null && apiResponse.isSuccess) {
                    apiResponse.result?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("API success but result data is missing for subway template detail."))
                } else {
                    Result.failure(Exception(apiResponse?.message ?: "Unknown API error getting subway template detail."))
                }
            } else {
                Result.failure(Exception("Failed to fetch subway template detail: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchSubwayTemplates(keyword: String): Result<SubwayTemplateResponse> {
        return try {
            val response = apiService.searchSubwayTemplates(keyword)
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse != null && apiResponse.isSuccess) {
                    apiResponse.result?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("API success but result data is missing for subway search."))
                } else {
                    Result.failure(Exception(apiResponse?.message ?: "Unknown API error during subway search."))
                }
            } else {
                Result.failure(Exception("Failed to search subway templates: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
