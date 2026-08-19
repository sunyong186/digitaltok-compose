package com.yourcompany.digitaltok.ui.decorate

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.yourcompany.digitaltok.R
import com.yourcompany.digitaltok.ui.components.BackArrowIcon
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagePreviewScreen(
    title: String = "사진 업로드",
    imageUrl: String? = null,
    imageUri: Uri? = null,
    onBackClick: () -> Unit,
    onTransferSuccess: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var dialogState by remember { mutableStateOf<TransferDialogState?>(null) }

    val main100Color = Color(0xFF36ABFF)
    val infoBoxBgColor = Color(0xFFEFF6FF)

    val startTransfer = {
        dialogState = TransferDialogState.Transferring
        coroutineScope.launch {
            // Mock 전송 처리 (Phase 0/1/2/3 연동: 1.5초 시뮬레이션)
            delay(1500)
            dialogState = TransferDialogState.Success
            delay(2000)
            dialogState = null
            onTransferSuccess()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DecorateColors.TextBlack
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = BackArrowIcon,
                            contentDescription = "뒤로가기",
                            tint = DecorateColors.TextBlack
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .background(Color.White)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 1. 레거시 1:1 정사각형 미리보기 카드 (MaterialCardView: 288dp x 288dp)
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .aspectRatio(1f),
                shape = RoundedCornerShape(4.dp),
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val model = imageUri ?: imageUrl
                    if (model != null) {
                        AsyncImage(
                            model = model,
                            contentDescription = "미리보기 이미지",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.blank_img),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. 디링 미리보기 라벨
            Text(
                text = "디링 미리보기",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = DecorateColors.TextGray2
            )

            Spacer(modifier = Modifier.weight(1f))

            // 3. 안내 정보 박스 (tv_info_box)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = infoBoxBgColor
            ) {
                Text(
                    text = "디링을 핸드폰 뒷면에 밀착시켜 주세요.\n푸른 계열은 DigitalTok에 구현이 불가합니다.",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = main100Color,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4. 디링에 전송하기 버튼 (btn_send_to_diring)
            Button(
                onClick = { startTransfer() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = main100Color
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "디링에 전송하기",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    dialogState?.let { state ->
        NfcTransferDialog(
            state = state,
            onDismissRequest = { dialogState = null },
            onRetryClick = { startTransfer() },
            onSupportClick = {
                dialogState = null
                Toast.makeText(context, "고객 지원으로 연결합니다.", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

// ==================== Previews ====================

@Preview(showBackground = true, name = "1. Photo Upload Preview Screen")
@Composable
fun ImagePreviewScreenPhotoPreview() {
    ImagePreviewScreen(
        title = "사진 업로드",
        imageUrl = null,
        onBackClick = {},
        onTransferSuccess = {}
    )
}

@Preview(showBackground = true, name = "2. Template Preview Screen")
@Composable
fun ImagePreviewScreenTemplatePreview() {
    ImagePreviewScreen(
        title = "지하철 템플릿 업로드",
        imageUrl = null,
        onBackClick = {},
        onTransferSuccess = {}
    )
}
