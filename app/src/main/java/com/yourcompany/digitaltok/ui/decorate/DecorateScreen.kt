package com.yourcompany.digitaltok.ui.decorate

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yourcompany.digitaltok.R
import com.yourcompany.digitaltok.ui.MainUiViewModel
import com.yourcompany.digitaltok.ui.MainViewModel

enum class DecorateTab {
    RECENT, TEMPLATE
}

enum class TemplateScreen {
    MENU, SEAT_LIST, STATION_LIST
}

@Composable
fun DecorateScreen(
    mainViewModel: MainViewModel,
    mainUiViewModel: MainUiViewModel,
    decorateViewModel: DecorateViewModel = viewModel()
) {
    val context = LocalContext.current

    var selectedTab by remember { mutableStateOf(DecorateTab.RECENT) }
    var templateScreen by remember { mutableStateOf(TemplateScreen.MENU) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedItemId by remember { mutableStateOf<String?>(null) }

    // 기본 최근 이미지 항목 (기본 추가 슬롯 포함)
    var recentItems by remember {
        mutableStateOf(
            listOf(
                DecorateItem(id = "slot_add", title = "추가", isSlot = true),
                DecorateItem(id = "mock_1", title = "기본 이미지 1"),
                DecorateItem(id = "mock_2", title = "기본 이미지 2")
            )
        )
    }

    // 템플릿 카테고리 목록
    val templateCategories = remember {
        listOf(
            TemplateItem(
                id = "template_transport",
                title = "교통약자 좌석",
                desc = "교통약자 좌석 안내 템플릿",
                thumbRes = R.drawable.blank_img
            ),
            TemplateItem(
                id = "template_station",
                title = "지하철역",
                desc = "지하철 노선별로 정리된 템플릿",
                thumbRes = R.drawable.blank_img
            )
        )
    }

    // 교통약자 좌석 샘플 템플릿 목록
    val seatTemplates = remember {
        listOf(
            TemplateItem(id = "seat_1", title = "임산부 배려석", desc = "분홍색 임산부 배려석 템플릿", thumbRes = R.drawable.blank_img),
            TemplateItem(id = "seat_2", title = "노약자석", desc = "교통약자 우대석 템플릿", thumbRes = R.drawable.blank_img)
        )
    }

    // 지하철역 샘플 템플릿 목록 (검색 필터 적용)
    val allStations = remember {
        listOf(
            TemplateItem(id = "station_1", title = "강남역 (2호선, 신분당선)", desc = "지하철역 템플릿", thumbRes = R.drawable.blank_img),
            TemplateItem(id = "station_2", title = "홍대입구역 (2호선, 공항철도, 경의중앙선)", desc = "지하철역 템플릿", thumbRes = R.drawable.blank_img),
            TemplateItem(id = "station_3", title = "서울역 (1호선, 4호선, KTX)", desc = "지하철역 템플릿", thumbRes = R.drawable.blank_img),
            TemplateItem(id = "station_4", title = "성수역 (2호선)", desc = "지하철역 템플릿", thumbRes = R.drawable.blank_img)
        )
    }

    val filteredStations = remember(searchQuery, allStations) {
        if (searchQuery.isBlank()) {
            allStations
        } else {
            allStations.filter { it.title.contains(searchQuery, ignoreCase = true) }
        }
    }

    // 뒤로가기 제어: 템플릿 하위 리스트 보기 상태이면 MENU로 이동
    BackHandler(enabled = (selectedTab == DecorateTab.TEMPLATE && templateScreen != TemplateScreen.MENU)) {
        templateScreen = TemplateScreen.MENU
    }

    val showBackButton = selectedTab == DecorateTab.TEMPLATE && templateScreen != TemplateScreen.MENU

    Scaffold(
        topBar = {
            DecorateTopAppBar(
                title = "꾸미기",
                showBackButton = showBackButton,
                onBackClick = { templateScreen = TemplateScreen.MENU }
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
        ) {
            DecorateTabs(
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab
                    if (tab == DecorateTab.RECENT) {
                        templateScreen = TemplateScreen.MENU
                    }
                }
            )

            when (selectedTab) {
                DecorateTab.RECENT -> {
                    RecentDecorateContent(
                        items = recentItems.map { item ->
                            item.copy(isSelected = (item.id == selectedItemId))
                        },
                        onItemClick = { item ->
                            selectedItemId = if (selectedItemId == item.id) null else item.id
                        },
                        onFavoriteClick = { id, isFav ->
                            recentItems = recentItems.map { item ->
                                if (item.id == id) item.copy(isFavorite = isFav) else item
                            }
                        },
                        onAddImageClick = {
                            Toast.makeText(context, "이미지 추가/크롭 기능은 Phase 3-2에서 연동됩니다.", Toast.LENGTH_SHORT).show()
                        },
                        onSendClick = {
                            val selected = recentItems.find { it.id == selectedItemId }
                            if (selected != null) {
                                Toast.makeText(context, "'${selected.title}' 선택됨 (미리보기 연동 예정)", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }

                DecorateTab.TEMPLATE -> {
                    when (templateScreen) {
                        TemplateScreen.MENU -> {
                            TemplateMenuContent(
                                templateCategories = templateCategories,
                                onCategoryClick = { category ->
                                    when (category.id) {
                                        "template_transport" -> templateScreen = TemplateScreen.SEAT_LIST
                                        "template_station" -> templateScreen = TemplateScreen.STATION_LIST
                                    }
                                }
                            )
                        }
                        TemplateScreen.SEAT_LIST -> {
                            SeatListContent(
                                seats = seatTemplates,
                                onSeatClick = { seat ->
                                    Toast.makeText(context, "'${seat.title}' 템플릿 선택됨", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                        TemplateScreen.STATION_LIST -> {
                            StationListContent(
                                searchQuery = searchQuery,
                                onSearchQueryChange = { searchQuery = it },
                                stations = filteredStations,
                                onStationClick = { station ->
                                    Toast.makeText(context, "'${station.title}' 템플릿 선택됨", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
