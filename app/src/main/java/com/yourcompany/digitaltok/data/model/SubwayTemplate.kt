package com.yourcompany.digitaltok.data.model

import com.google.gson.annotations.SerializedName

/**
 * 지하철 템플릿 생성을 요청할 때 사용하는 데이터 클래스
 * POST /api/v1/templates/subway/generate
 */
data class SubwayGenerateRequest(
    @SerializedName("stationName")
    val stationName: String,
    @SerializedName("stationNameEng")
    val stationNameEng: String,
    @SerializedName("lineName")
    val lineName: String
)

/**
 * 전체 지하철역 목록 조회 API의 응답을 감싸는 데이터 클래스
 * GET /api/v1/templates/subway
 */
data class SubwayTemplateResponse(
    @SerializedName("count")
    val count: Int,
    @SerializedName("items")
    val items: List<SubwayTemplateItem>
)

/**
 * 지하철역 목록의 개별 아이템
 */
data class SubwayTemplateItem(
    @SerializedName("templateId")
    val templateId: Int,
    @SerializedName("stationName")
    val stationName: String,
    @SerializedName("lineName")
    val lineName: String,
    @SerializedName("templateImageUrl")
    val templateImageUrl: String
)

/**
 * 단일 지하철역 템플릿 상세 정보
 * GET /api/v1/templates/subway/{templateId}
 */
data class SubwayTemplateDetail(
    @SerializedName("templateId")
    val templateId: Int,
    @SerializedName("stationName")
    val stationName: String,
    @SerializedName("stationNameEng")
    val stationNameEng: String,
    @SerializedName("lineName")
    val lineName: String,
    @SerializedName("templateImageUrl")
    val templateImageUrl: String,
    @SerializedName("templateDataUrl")
    val templateDataUrl: String
)
