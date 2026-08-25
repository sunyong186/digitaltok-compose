package com.yourcompany.digitaltok.ui.setting

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.digitaltok.ui.theme.FaqItem
import com.yourcompany.digitaltok.MainActivity
import com.yourcompany.digitaltok.R
import com.yourcompany.digitaltok.data.network.AccountApiService
import com.yourcompany.digitaltok.data.network.RetrofitClient
import com.yourcompany.digitaltok.data.repository.AccountRepository
import com.yourcompany.digitaltok.data.repository.PrefsAuthLocalStore
import com.yourcompany.digitaltok.data.repository.UserRepository
import com.yourcompany.digitaltok.ui.MainUiViewModel
import com.yourcompany.digitaltok.ui.MainViewModel
import com.yourcompany.digitaltok.ui.components.BackArrowIcon
import com.yourcompany.digitaltok.ui.components.ChangeEmailDialogContent
import com.yourcompany.digitaltok.ui.components.LogoutDialogContent
import com.yourcompany.digitaltok.ui.components.WithdrawDialogContent
import com.yourcompany.digitaltok.ui.theme.*
import kotlinx.coroutines.launch

enum class SettingsSubScreen {
    MAIN, PROFILE_EDIT, FAQ, SUPPORT
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    mainViewModel: MainViewModel,
    mainUiViewModel: MainUiViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var subScreen by remember { mutableStateOf(SettingsSubScreen.MAIN) }

    // SharedPreferences 캐시 & 서버 유저 데이터
    val prefsName = "auth_prefs"
    val prefs = remember { context.getSharedPreferences(prefsName, Context.MODE_PRIVATE) }
    var nickname by remember { mutableStateOf(prefs.getString("nickname", "") ?: "") }
    var email by remember { mutableStateOf(prefs.getString("email", "") ?: "") }

    val userRepository = remember { UserRepository(context.applicationContext) }
    val localStore = remember { PrefsAuthLocalStore(context.applicationContext) }
    val accountRepository = remember {
        val accountApi = RetrofitClient.create(AccountApiService::class.java)
        AccountRepository(accountApi, localStore)
    }

    // 서버 프로필 동기화
    val loadProfile = {
        coroutineScope.launch {
            userRepository.getMyProfile()
                .onSuccess { me ->
                    nickname = me.nickname
                    email = me.email
                    prefs.edit()
                        .putLong("userId", me.userId)
                        .putString("nickname", me.nickname)
                        .putString("email", me.email)
                        .apply()
                }
                .onFailure {
                    // 실패 시 캐시된 값 유지
                }
        }
    }

    LaunchedEffect(Unit) {
        loadProfile()
    }

    // 뒤로가기 핸들링
    BackHandler(enabled = subScreen != SettingsSubScreen.MAIN) {
        subScreen = SettingsSubScreen.MAIN
    }

    val moveToSplash = {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(intent)
    }

    val topBarTitle = when (subScreen) {
        SettingsSubScreen.MAIN -> "설정"
        SettingsSubScreen.PROFILE_EDIT -> "프로필 편집"
        SettingsSubScreen.FAQ -> "자주 묻는 질문"
        SettingsSubScreen.SUPPORT -> "고객 지원"
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = topBarTitle,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DtTextBlack
                    )
                },
                navigationIcon = {
                    if (subScreen != SettingsSubScreen.MAIN) {
                        IconButton(onClick = { subScreen = SettingsSubScreen.MAIN }) {
                            Icon(
                                imageVector = BackArrowIcon,
                                contentDescription = "뒤로가기",
                                tint = DtTextBlack
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = if (subScreen == SettingsSubScreen.SUPPORT) Color.White else DtLightGrayBg
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .background(if (subScreen == SettingsSubScreen.SUPPORT) Color.White else DtLightGrayBg)
        ) {
            when (subScreen) {
                SettingsSubScreen.MAIN -> SettingsMainContent(
                    nickname = nickname,
                    email = email,
                    onEditProfileClick = { subScreen = SettingsSubScreen.PROFILE_EDIT },
                    onFaqClick = { subScreen = SettingsSubScreen.FAQ },
                    onSupportClick = { subScreen = SettingsSubScreen.SUPPORT }
                )

                SettingsSubScreen.PROFILE_EDIT -> ProfileEditContent(
                    nickname = nickname,
                    email = email,
                    accountRepository = accountRepository,
                    localStore = localStore,
                    onProfileUpdated = { loadProfile() },
                    onLogoutSuccess = moveToSplash,
                    onWithdrawSuccess = moveToSplash
                )

                SettingsSubScreen.FAQ -> FaqContent(
                    onSupportClick = { subScreen = SettingsSubScreen.SUPPORT }
                )

                SettingsSubScreen.SUPPORT -> SupportContent()
            }
        }
    }
}

// ==================== 1. 메인 설정 뷰 (fragment_help.xml 1:1 이식) ====================

@Composable
private fun SettingsMainContent(
    nickname: String,
    email: String,
    onEditProfileClick: () -> Unit,
    onFaqClick: () -> Unit,
    onSupportClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DtLightGrayBg)
    ) {
        // ------------------ 섹션 1) 프로필 카드 (bannerProfile) ------------------
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 17.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 77x77 원본 프로필 아바타 (rectangle + vector)
                    Box(
                        modifier = Modifier.size(77.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.rectangle),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                        Image(
                            painter = painterResource(id = R.drawable.vector),
                            contentDescription = null,
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(26.dp))

                    Column {
                        val displayName = when {
                            nickname.isBlank() -> "정재원님"
                            nickname.endsWith("님") -> nickname
                            else -> "${nickname}님"
                        }
                        val displayEmail = if (email.isNotBlank()) email else "jaewon12@gmail.com"

                        Text(
                            text = displayName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DtTextBlack
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = displayEmail,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = DtTextGray3
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 가로 채움 "프로필 편집" 파란색 버튼 (btnEditProfile)
                Button(
                    onClick = onEditProfileClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DtPointBlue
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "프로필 편집",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // ------------------ 섹션 2) 도움말 섹션 (helpSection) ------------------
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 28.dp)
            ) {
                Text(
                    text = "도움말",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DtTextGray1
                )

                Spacer(modifier = Modifier.height(25.dp))

                // Row 1 : 자주 묻는 질문 (rowFaq)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onFaqClick() },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "자주 묻는 질문",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = DtTextBlack
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = DtTextGray1,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Row 2 : 고객 지원 (rowSupport)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSupportClick() },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "고객 지원",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = DtTextBlack
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = DtTextGray1,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // ------------------ 섹션 3) 정보 섹션 (infoSection) ------------------
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 28.dp)
            ) {
                Text(
                    text = "정보",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DtTextGray1
                )

                Spacer(modifier = Modifier.height(25.dp))

                // 앱 버전 (boxVersion)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "앱 버전",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = DtTextBlack
                    )
                    Text(
                        text = "DT v 1.12.6",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = DtTextGray1
                    )
                }
            }
        }
    }
}

// ==================== 2. 프로필 편집 뷰 (fragment_profile_edit.xml 1:1 이식) ====================

@Composable
private fun ProfileEditContent(
    nickname: String,
    email: String,
    accountRepository: AccountRepository,
    localStore: PrefsAuthLocalStore,
    onProfileUpdated: () -> Unit,
    onLogoutSuccess: () -> Unit,
    onWithdrawSuccess: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showWithdrawDialog by remember { mutableStateOf(false) }
    var showChangeEmailDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DtLightGrayBg)
    ) {
        // ------------------ 섹션 1) 프로필 / 이름 (sectionProfile) ------------------
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 22.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 77x77 원본 프로필 아바타 (rectangle + vector)
                    Box(
                        modifier = Modifier.size(77.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.rectangle),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                        Image(
                            painter = painterResource(id = R.drawable.vector),
                            contentDescription = null,
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(26.dp))

                    Column {
                        Text(
                            text = "이름",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = DtTextGray3
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (nickname.isNotBlank()) nickname else "정재원",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = DtTextBlack
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 파란 안내 박스 (tvInfoBox)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = DtInfoSolid,
                    border = BorderStroke(1.dp, DtMain100.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "이름은 자동생성되며, 이메일은 로그인 및 알림 수신에 사용됩니다.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = DtPointBlue,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // ------------------ 섹션 2) 이메일 (sectionEmail) ------------------
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 28.dp)
            ) {
                Text(
                    text = "이메일",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DtTextGray1
                )

                Spacer(modifier = Modifier.height(19.dp))

                // 회색 이메일 박스 (boxEmail: 50dp height, rounded 8dp)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = DtLightGrayBg
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (email.isNotBlank()) email else "jaewon12@gmail.com",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = DtTextGray3
                        )
                        // 원본 edit_image 드로어블 사용
                        Image(
                            painter = painterResource(id = R.drawable.edit_image),
                            contentDescription = "이메일 변경",
                            modifier = Modifier
                                .size(18.dp)
                                .clickable { showChangeEmailDialog = true }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // ------------------ 섹션 3) 계정 관리 (sectionAccount) ------------------
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 28.dp)
            ) {
                Text(
                    text = "계정관리",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DtTextGray1
                )

                Spacer(modifier = Modifier.height(25.dp))

                // Row 1 : 로그아웃 (rowLogout: 빨간색 텍스트 & 빨간색 화살표)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showLogoutDialog = true },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "로그아웃",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = DtFavoriteRed
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = DtFavoriteRed,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Row 2 : 회원 탈퇴 (tvWithdraw: 검은색 텍스트 & 회색 화살표)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showWithdrawDialog = true },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "회원 탈퇴",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = DtTextBlack
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = DtTextGray1,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }

    // 1. 이메일 변경 다이얼로그
    if (showChangeEmailDialog) {
        Dialog(onDismissRequest = { showChangeEmailDialog = false }) {
            ChangeEmailDialogContent(
                onConfirm = { password, newEmail ->
                    if (password.isBlank() || newEmail.isBlank()) {
                        Toast.makeText(context, "정보를 모두 입력해 주세요.", Toast.LENGTH_SHORT).show()
                        return@ChangeEmailDialogContent
                    }
                    coroutineScope.launch {
                        accountRepository.changeEmail(password, newEmail)
                            .onSuccess {
                                showChangeEmailDialog = false
                                Toast.makeText(context, "이메일이 변경되었습니다.", Toast.LENGTH_SHORT).show()
                                onProfileUpdated()
                            }
                            .onFailure { e ->
                                Toast.makeText(context, e.message ?: "이메일 변경 실패", Toast.LENGTH_SHORT).show()
                            }
                    }
                },
                onDismiss = { showChangeEmailDialog = false }
            )
        }
    }

    // 2. 로그아웃 다이얼로그
    if (showLogoutDialog) {
        Dialog(onDismissRequest = { showLogoutDialog = false }) {
            LogoutDialogContent(
                onConfirm = {
                    showLogoutDialog = false
                    coroutineScope.launch {
                        accountRepository.logout()
                            .onSuccess { onLogoutSuccess() }
                            .onFailure {
                                localStore.clearAuth()
                                onLogoutSuccess()
                            }
                    }
                },
                onDismiss = { showLogoutDialog = false }
            )
        }
    }

    // 3. 회원탈퇴 다이얼로그
    if (showWithdrawDialog) {
        Dialog(onDismissRequest = { showWithdrawDialog = false }) {
            WithdrawDialogContent(
                onConfirm = {
                    showWithdrawDialog = false
                    coroutineScope.launch {
                        accountRepository.withdraw()
                            .onSuccess { onWithdrawSuccess() }
                            .onFailure { e ->
                                Toast.makeText(context, e.message ?: "회원탈퇴 실패", Toast.LENGTH_SHORT).show()
                            }
                    }
                },
                onDismiss = { showWithdrawDialog = false }
            )
        }
    }
}

// ==================== 3. FAQ 뷰 ====================

@Composable
private fun FaqContent(
    onSupportClick: () -> Unit
) {
    val faqList = remember {
        listOf(
            FaqItem(
                "디링은 어떻게 사용하나요?",
                "앱에서 이미지나 템플릿을 선택한 후 NFC로 디링에 전송하면 자동으로 디스플레이에 표시됩니다."
            ),
            FaqItem(
                "디링과 연결이 안 돼요",
                "휴대폰 NFC가 켜져 있는지 확인하고, 디링을 휴대폰 뒷면 NFC 센서 부위에 밀착해 주세요."
            ),
            FaqItem(
                "이미지가 표시되지 않아요",
                "지원하는 이미지 형식(JPG, PNG)인지 확인하고 크기를 재조정해 보세요."
            ),
            FaqItem(
                "여러 개의 디링을 연결할 수 있나요?",
                "현재는 단일 디링 연결을 지원하며, 추후 업데이트 예정입니다."
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 20.dp)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(faqList) { item ->
                FaqItemRow(item = item)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        Button(
            onClick = onSupportClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DtPointBlue
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "1:1 문의하기",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun FaqItemRow(item: FaqItem) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(12.dp),
        color = DtLightGrayBg
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.question,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DtTextBlack,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = DtTextGray3
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = DtBorderGray)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = item.answer,
                        fontSize = 14.sp,
                        color = DtTextGray3,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

// ==================== 4. 고객 지원 뷰 (fragment_support.xml 1:1 리소스 반영) ====================

@Composable
private fun SupportContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(49.dp))

        // 헤드셋 아이콘 (ivHeadset 88dp x 88dp)
        Icon(
            imageVector = Icons.Default.Headset,
            contentDescription = null,
            tint = DtPointBlue,
            modifier = Modifier.size(88.dp)
        )

        Spacer(modifier = Modifier.height(23.dp))

        // 타이틀 ("무엇을 도와드릴까요?")
        Text(
            text = "무엇을 도와드릴까요?",
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            color = DtTextBlack
        )

        Spacer(modifier = Modifier.height(18.dp))

        // 설명 ("친절한 고객 지원팀이 도와\n드리겠습니다")
        Text(
            text = "친절한 고객 지원팀이 도와\n드리겠습니다",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = DtTextGray3,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(41.dp))

        // 카드 1: 이메일 (cardEmail -> R.drawable.icon_email_wrap 48x48 사용)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = BorderStroke(1.dp, DtBorderGray)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 원본 icon_email_wrap 드로어블 리소스 직접 사용
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = DtPointBlue,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "이메일",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = DtTextBlack
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "support@diring.com",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = DtTextGray3
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 카드 2: 카카오톡 (cardKakao -> R.drawable.kakao 48x48 사용)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = BorderStroke(1.dp, DtBorderGray)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 원본 kakao 드로어블 리소스 직접 사용
                Image(
                    painter = painterResource(id = R.drawable.kakao),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "카카오톡",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = DtTextBlack
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "@DiRing",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = DtTextGray3
                    )
                }
            }
        }
    }
}

// ==================== Previews ====================

@Preview(showBackground = true, name = "1. Settings Main Screen")
@Composable
fun SettingsMainContentPreview() {
    SettingsMainContent(
        nickname = "정재원",
        email = "jaewon12@gmail.com",
        onEditProfileClick = {},
        onFaqClick = {},
        onSupportClick = {}
    )
}

@Preview(showBackground = true, name = "2. Profile Edit Screen")
@Composable
fun ProfileEditContentPreview() {
    val context = LocalContext.current
    val localStore = remember { PrefsAuthLocalStore(context.applicationContext) }
    val accountApi = RetrofitClient.create(AccountApiService::class.java)
    val repository = remember { AccountRepository(accountApi, localStore) }

    ProfileEditContent(
        nickname = "정재원",
        email = "jaewon12@gmail.com",
        accountRepository = repository,
        localStore = localStore,
        onProfileUpdated = {},
        onLogoutSuccess = {},
        onWithdrawSuccess = {}
    )
}

@Preview(showBackground = true, name = "3. FAQ Screen")
@Composable
fun FaqContentPreview() {
    FaqContent(onSupportClick = {})
}

@Preview(showBackground = true, name = "4. Support Screen")
@Composable
fun SupportContentPreview() {
    SupportContent()
}
