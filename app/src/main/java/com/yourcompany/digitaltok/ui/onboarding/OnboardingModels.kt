package com.yourcompany.digitaltok.ui.onboarding

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class OnboardingImageLayer(
    val resId: Int,
    val size: Dp,
    val offsetX: Dp = 0.dp,
    val offsetY: Dp = 0.dp
)

data class OnboardingPageData(
    val title: String,
    val subtitle: String,

    // 1장짜리 이미지(예: 1번)
    val singleImageRes: Int? = null,
    val singleImageSize: Dp = 160.dp,

    // 겹치기 이미지(예: 2번, 4번)
    val layers: List<OnboardingImageLayer> = emptyList(),

    // 3번 지하철 카드
    val showSubwayCards: Boolean = false
)
