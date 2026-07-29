package com.yourcompany.digitaltok.data.model

import com.google.gson.annotations.SerializedName


// GET /templates/priority API 응답의 `items` 배열에 포함된 개별 템플릿 정보

data class PriorityTemplate(
    @SerializedName("templateId")
    val templateId: Int,
    @SerializedName("priorityType")
    val priorityType: String,
    @SerializedName("templateImageUrl")
    val templateImageUrl: String?
)


// GET /templates/priority API의 `result` 필드에 해당하는 데이터 클래스

data class PriorityTemplateResponse(
    @SerializedName("count")
    val count: Int,
    @SerializedName("items")
    val items: List<PriorityTemplate>
)


data class PriorityTemplateDetail(
    @SerializedName("templateId")
    val templateId: Int,
    @SerializedName("priority_type") // 목록 API와 달리, 상세 API는 priority_type 이라는 키를 사용
    val priorityType: String,
    @SerializedName("templateImageUrl")
    val templateImageUrl: String,
    @SerializedName("templateDataUrl")
    val templateDataUrl: String
)
