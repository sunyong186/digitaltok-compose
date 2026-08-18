package com.yourcompany.digitaltok.ui.decorate

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yourcompany.digitaltok.R
import com.yourcompany.digitaltok.ui.MainUiViewModel
import com.yourcompany.digitaltok.ui.MainViewModel
import java.io.File

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

    // 이미지 피커 & 크롭 관련 상태
    var showImagePickerSheet by remember { mutableStateOf(false) }
    var cropTargetUri by remember { mutableStateOf<Uri?>(null) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }

    // 갤러리 피커 Launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            cropTargetUri = uri
        }
    }

    // 카메라 촬영 Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && pendingCameraUri != null) {
            cropTargetUri = pendingCameraUri
        }
    }

    // 카메라 권한 및 임시파일 생성 후 촬영 준비
    val launchCamera = {
        try {
            val cameraDir = File(context.cacheDir, "camera")
            if (!cameraDir.exists()) cameraDir.mkdirs()
            val tempFile = File(cameraDir, "cam_${System.currentTimeMillis()}.jpg")
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                tempFile
            )
            pendingCameraUri = uri
            cameraLauncher.launch(uri)
        } catch (e: Exception) {
            Toast.makeText(context, "카메라를 실행할 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

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

    // 뒤로가기 제어: 크롭 화면이나 템플릿 서브화면 상태 처리
    if (cropTargetUri != null) {
        BackHandler {
            cropTargetUri = null
        }
        CropScreen(
            imageUri = cropTargetUri!!,
            onCropSuccess = { croppedUri ->
                val newItem = DecorateItem(
                    id = "user_${System.currentTimeMillis()}",
                    title = "내 사진",
                    imageUri = croppedUri
                )
                recentItems = listOf(recentItems.first()) + listOf(newItem) + recentItems.drop(1)
                selectedItemId = newItem.id
                cropTargetUri = null
                Toast.makeText(context, "이미지가 추가되었습니다.", Toast.LENGTH_SHORT).show()
            },
            onCancel = {
                cropTargetUri = null
            }
        )
        return
    }

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
                            showImagePickerSheet = true
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

    if (showImagePickerSheet) {
        ImagePickerBottomSheet(
            onDismissRequest = { showImagePickerSheet = false },
            onCameraClick = {
                launchCamera()
            },
            onGalleryClick = {
                photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        )
    }
}
