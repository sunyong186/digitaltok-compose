package com.yourcompany.digitaltok.data.repository

import com.yourcompany.digitaltok.data.model.PriorityTemplate
import com.yourcompany.digitaltok.data.model.PriorityTemplateDetail
import com.yourcompany.digitaltok.data.model.SubwayGenerateRequest
import com.yourcompany.digitaltok.data.model.SubwayTemplateDetail
import com.yourcompany.digitaltok.data.model.SubwayTemplateResponse
import com.yourcompany.digitaltok.data.network.RetrofitClient

class TemplateRepository {

    private val apiService = RetrofitClient.apiService

    suspend fun getPriorityTemplates(): Result<List<PriorityTemplate>> {
        kotlinx.coroutines.delay(500)
        return Result.success(
            listOf(
                PriorityTemplate(
                    templateId = 1,
                    priorityType = "PREGNANT",
                    templateImageUrl = "android.resource://com.yourcompany.digitaltok/drawable/pregnant_badge"
                )
            )
        )
    }

    suspend fun getPriorityTemplateDetail(templateId: Int): Result<PriorityTemplateDetail> {
        kotlinx.coroutines.delay(500)
        return Result.success(
            PriorityTemplateDetail(
                templateId = templateId,
                priorityType = "PREGNANT",
                templateImageUrl = "android.resource://com.yourcompany.digitaltok/drawable/pregnant_badge",
                templateDataUrl = "android.resource://com.yourcompany.digitaltok/drawable/pregnant_badge"
            )
        )
    }

    suspend fun generateSubwayTemplate(request: SubwayGenerateRequest): Result<String> {
        kotlinx.coroutines.delay(500)
        return Result.success("android.resource://com.yourcompany.digitaltok/drawable/jongno3")
    }

    suspend fun getSubwayTemplates(): Result<SubwayTemplateResponse> {
        kotlinx.coroutines.delay(500)
        return Result.success(
            SubwayTemplateResponse(
                count = 1,
                items = listOf(
                    com.yourcompany.digitaltok.data.model.SubwayTemplateItem(
                        templateId = 1,
                        stationName = "종로3가",
                        lineName = "1호선",
                        templateImageUrl = "android.resource://com.yourcompany.digitaltok/drawable/jongno3"
                    )
                )
            )
        )
    }

    suspend fun getSubwayTemplateDetail(templateId: Int): Result<SubwayTemplateDetail> {
        kotlinx.coroutines.delay(500)
        return Result.success(
            SubwayTemplateDetail(
                templateId = templateId,
                stationName = "종로3가",
                stationNameEng = "Jongno 3(sam)-ga",
                lineName = "1호선",
                templateImageUrl = "android.resource://com.yourcompany.digitaltok/drawable/jongno3",
                templateDataUrl = "android.resource://com.yourcompany.digitaltok/drawable/jongno3"
            )
        )
    }

    suspend fun searchSubwayTemplates(keyword: String): Result<SubwayTemplateResponse> {
        kotlinx.coroutines.delay(500)
        return Result.success(
            SubwayTemplateResponse(
                count = 1,
                items = listOf(
                    com.yourcompany.digitaltok.data.model.SubwayTemplateItem(
                        templateId = 1,
                        stationName = "종로3가",
                        lineName = "1호선",
                        templateImageUrl = "android.resource://com.yourcompany.digitaltok/drawable/jongno3"
                    )
                )
            )
        )
    }
}
