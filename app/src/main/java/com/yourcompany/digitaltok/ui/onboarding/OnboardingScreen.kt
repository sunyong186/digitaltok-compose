package com.yourcompany.digitaltok.ui.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yourcompany.digitaltok.R
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    onFinish: () -> Unit = {}
) {
    val pages = listOf(

        OnboardingPageData(
            title = "디링에 오신 것을\n환영합니다",
            subtitle = "스마트한 소통을 시작해보세요",
            singleImageRes = R.drawable.ic_onboard_chat,
            singleImageSize = 180.dp
        ),


        OnboardingPageData(
            title = "전자 잉크\n디스플레이 ‘디링’",
            subtitle = "배터리 필요 없는 디스플레이로\n개성있는 메시지를 표현하세요",
            singleImageRes = R.drawable.img_onboard_eink_dring,
            singleImageSize = 250.dp,
            layers = emptyList()
        ),


        OnboardingPageData(
            title = "대중교통에서 편리하게",
            subtitle = "하차 정보나 배려 메시지를\n손쉽게 전달할 수 있어요",
            showSubwayCards = true
        ),


        OnboardingPageData(
            title = "사진을 추가해보세요",
            subtitle = "앨범 속 사진으로\n디링을 꾸며보세요",
            layers = listOf(
                OnboardingImageLayer(
                    resId = R.drawable.img_onboard_photo_left,
                    size = 160.dp,
                    offsetX = (-58).dp,
                    offsetY = 18.dp
                ),
                OnboardingImageLayer(
                    resId = R.drawable.img_onboard_photo_right,
                    size = 160.dp,
                    offsetX = 58.dp,
                    offsetY = 18.dp
                ),
                OnboardingImageLayer(
                    resId = R.drawable.img_onboard_photo_front,
                    size = 195.dp,
                    offsetX = 0.dp,
                    offsetY = 48.dp
                )
            )
        )
    )

    val stationCards = listOf(
        R.drawable.img_station_239,
        R.drawable.img_station_203,
        R.drawable.img_station_222,
        R.drawable.img_station_216,
        R.drawable.img_station_221,
        R.drawable.img_station_219
    )

    val pagerState = rememberPagerState { pages.size }
    val scope = rememberCoroutineScope()

    val stationListState = rememberLazyListState()
    val currentStationIndex by remember { derivedStateOf { stationListState.centerItemIndex() } }
    val lastStationIndex = stationCards.lastIndex

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            val page = pages[pageIndex]
            val isStationPage = page.showSubwayCards

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(120.dp))

                Text(
                    text = page.title,
                    style = TextStyle(
                        fontSize = 24.sp,
                        lineHeight = 31.2.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF121212),
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(14.dp))

                Text(
                    text = page.subtitle,
                    style = TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 19.6.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF505050),
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )


                Spacer(Modifier.height(44.dp))


                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        page.showSubwayCards -> {
                            SubwayCarousel(
                                cards = stationCards,
                                listState = stationListState,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        page.layers.isNotEmpty() -> {
                            OnboardingLayeredArtwork(layers = page.layers)
                        }

                        page.singleImageRes != null -> {
                            Image(
                                painter = painterResource(id = page.singleImageRes),
                                contentDescription = null,
                                modifier = Modifier.size(page.singleImageSize)
                            )
                        }
                    }
                }


                Spacer(Modifier.weight(0.35f))

                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    OnboardingIndicator(total = pages.size, current = pageIndex)
                }

                Spacer(Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OnboardingButton(
                        text = "이전으로",
                        containerColor = Color(0xFFE0E0E0),
                        textColor = Color(0xFF9E9E9E),
                        enabled = pageIndex != 0 || (isStationPage && currentStationIndex > 0),
                        modifier = Modifier.weight(1f)
                    ) {
                        scope.launch {
                            if (isStationPage && currentStationIndex > 0) {
                                stationListState.animateScrollToItem(currentStationIndex - 1)
                            } else {
                                if (pageIndex > 0) pagerState.animateScrollToPage(pageIndex - 1)
                            }
                        }
                    }

                    OnboardingButton(
                        text = "다음으로",
                        containerColor = Color(0xFF36ABFF),
                        textColor = Color.White,
                        enabled = true,
                        modifier = Modifier.weight(1f)
                    ) {
                        scope.launch {
                            if (isStationPage && currentStationIndex < lastStationIndex) {
                                stationListState.animateScrollToItem(currentStationIndex + 1)
                            } else {
                                if (pageIndex == pages.lastIndex) onFinish()
                                else pagerState.animateScrollToPage(pageIndex + 1)
                            }
                        }
                    }
                }


                Spacer(Modifier.height(18.dp))
            }
        }
    }
}

@Composable
private fun OnboardingLayeredArtwork(layers: List<OnboardingImageLayer>) {
    Box(contentAlignment = Alignment.Center) {
        layers.forEach { layer ->
            Image(
                painter = painterResource(id = layer.resId),
                contentDescription = null,
                modifier = Modifier
                    .size(layer.size)
                    .offset(x = layer.offsetX, y = layer.offsetY)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SubwayCarousel(
    cards: List<Int>,
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    val fling = rememberSnapFlingBehavior(lazyListState = listState)

    LazyRow(
        state = listState,
        flingBehavior = fling,
        contentPadding = PaddingValues(horizontal = 64.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        items(items = cards, key = { it }) { resId ->
            val (scale, alpha, saturation) = itemTransformByCenter(listState, resId)

            val matrix = remember(saturation) {
                ColorMatrix().apply { setToSaturation(saturation) }
            }

            Image(
                painter = painterResource(id = resId),
                contentDescription = null,
                colorFilter = ColorFilter.colorMatrix(matrix),
                modifier = Modifier
                    .size(153.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        this.alpha = alpha
                    }
            )
        }
    }
}

private fun itemTransformByCenter(
    listState: LazyListState,
    key: Int
): Triple<Float, Float, Float> {
    val layout = listState.layoutInfo
    val visible = layout.visibleItemsInfo
    if (visible.isEmpty()) return Triple(1f, 1f, 1f)

    val viewportCenter = (layout.viewportStartOffset + layout.viewportEndOffset) / 2
    val item = visible.firstOrNull { it.key == key } ?: return Triple(0.92f, 0.55f, 0f)

    val itemCenter = item.offset + item.size / 2
    val distancePx = abs(itemCenter - viewportCenter).toFloat()
    val maxDistance = (layout.viewportEndOffset - layout.viewportStartOffset).toFloat() / 2f

    val t = (1f - (distancePx / maxDistance)).coerceIn(0f, 1f)

    val scale = lerp(0.92f, 1.00f, t)
    val alpha = lerp(0.55f, 1.00f, t)
    val saturation = lerp(0.00f, 1.00f, t)

    return Triple(scale, alpha, saturation)
}

private fun lerp(a: Float, b: Float, t: Float): Float = a + (b - a) * t

private fun LazyListState.centerItemIndex(): Int {
    val layout = layoutInfo
    val visible = layout.visibleItemsInfo
    if (visible.isEmpty()) return 0

    val viewportCenter = (layout.viewportStartOffset + layout.viewportEndOffset) / 2
    val closest = visible.minByOrNull { item ->
        val itemCenter = item.offset + item.size / 2
        kotlin.math.abs(itemCenter - viewportCenter)
    }
    return closest?.index ?: 0
}
