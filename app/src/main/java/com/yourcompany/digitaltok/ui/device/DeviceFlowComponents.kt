package com.yourcompany.digitaltok.ui.device

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yourcompany.digitaltok.R
import com.yourcompany.digitaltok.ui.components.BackArrowIcon

private object FlowColors {
    val TextBlack = Color(0xFF121212)
    val TextGray1 = Color(0xFFA0A0A0)
    val TextGray2 = Color(0xFF6B6B6B)
    val Main100 = Color(0xFF3AADFF)
    val Error = Color(0xFFFF5252)
    val Gray6A = Color(0xFF6A6A6A)
    val Gray4A = Color(0xFF4A4A4A)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlowTopAppBar(title: String, onBackClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = FlowColors.TextBlack
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = BackArrowIcon,
                    contentDescription = "Back",
                    tint = FlowColors.TextBlack
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.White
        )
    )
}

@Composable
fun DeviceConnectContent(
    onBackClick: () -> Unit,
    onProceedClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        FlowTopAppBar(title = "기기연결", onBackClick = onBackClick)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(73.dp))
            Image(
                painter = painterResource(id = R.drawable.ic_phone),
                contentDescription = null,
                modifier = Modifier.size(75.dp, 135.dp)
            )
            Spacer(modifier = Modifier.height(36.dp))
            Text(
                text = "DiRing 연결",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = FlowColors.TextBlack
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "NFC를 통해 전자 잉크 디링을 연결하세요",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = FlowColors.TextGray2
            )
            Spacer(modifier = Modifier.height(36.dp))
            Button(
                onClick = onProceedClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FlowColors.Main100,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("NFC 연결 시작", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        // Bottom Info Box
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xFFF7F8FA),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                )
                .padding(horizontal = 35.dp, vertical = 50.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Image(
                    painter = painterResource(id = R.drawable.ic_alert_circle),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp).padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("NFC 활성화 확인", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("설정 > 연결에서 NFC가 켜져있는지 확인하세요", fontSize = 14.sp, color = FlowColors.Gray6A)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.Top) {
                Image(
                    painter = painterResource(id = R.drawable.ic_alert_circle),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp).padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("올바른 위치", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("휴대폰 뒷면을 그립톡 중앙에 가까이 대주세요", fontSize = 14.sp, color = FlowColors.Gray6A)
                }
            }
        }
    }
}

@Composable
fun DeviceSearchingContent(
    onBackClick: () -> Unit,
    onMockTag: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                // Mock NFC tagging for emulator
                onMockTag("MOCK_UID_1234")
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FlowTopAppBar(title = "기기 연결", onBackClick = onBackClick)

        Spacer(modifier = Modifier.height(150.dp))
        
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(25.dp).background(Color(0xFF767676), CircleShape))
            Spacer(modifier = Modifier.width(19.dp))
            Box(modifier = Modifier.size(25.dp).background(Color(0xFF767676), CircleShape))
            Spacer(modifier = Modifier.width(19.dp))
            Box(modifier = Modifier.size(25.dp).background(Color(0xFF767676), CircleShape))
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "연결 중..",
            fontSize = 26.sp,
            fontWeight = FontWeight.SemiBold,
            color = FlowColors.TextGray1
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "휴대폰을 디링에\n연결 중 입니다",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = FlowColors.TextGray2,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun DeviceSuccessContent(
    onNavigateToDecorate: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp)) // padding from top

        Spacer(modifier = Modifier.height(200.dp))
        Image(
            painter = painterResource(id = R.drawable.ic_blue_check_circle),
            contentDescription = null,
            modifier = Modifier.size(88.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "연결 성공",
            fontSize = 26.sp,
            fontWeight = FontWeight.SemiBold,
            color = FlowColors.Main100
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "디링이 성공적으로\n연결되었습니다",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = FlowColors.Gray4A,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onNavigateToDecorate,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = FlowColors.Main100),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("이미지 or 템플릿 추가하기", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.White)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = onNavigateToHome,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF4F4F4)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("홈으로 돌아가기", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = FlowColors.TextGray2)
        }
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun DeviceFailureContent(
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    onNavigateToHelp: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        FlowTopAppBar(title = "", onBackClick = onBackClick)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(150.dp))
            Image(
                painter = painterResource(id = R.drawable.ic_x_circle),
                contentDescription = null,
                modifier = Modifier.size(88.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "연결 실패",
                fontSize = 26.sp,
                fontWeight = FontWeight.SemiBold,
                color = FlowColors.Error
            )
            Spacer(modifier = Modifier.height(11.dp))
            Text(
                text = "기기를 찾을 수\n없습니다",
                fontSize = 18.sp,
                color = FlowColors.TextGray2,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(62.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF9F9F9), RoundedCornerShape(12.dp))
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_alert_circle),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("해결 방법", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = FlowColors.TextGray1)
                }
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "•  NFC 기능이 활성화되어 있는지 확인하세요\n•  휴대폰 케이스를 제거하고 다시 시도하세요\n•  디링을 휴대폰 뒷면 중앙에 가까이 대세요",
                    fontSize = 14.sp,
                    color = FlowColors.TextGray2,
                    lineHeight = 22.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onRetryClick,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FlowColors.Main100),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("다시 시도", fontSize = 16.sp, color = Color.White)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onNavigateToHelp,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF4F4F4)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("고객지원 문의하기", fontSize = 16.sp, color = FlowColors.TextGray2)
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}


@androidx.compose.ui.tooling.preview.Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun PreviewDeviceConnectContent() {
    DeviceConnectContent(onBackClick = {}, onProceedClick = {})
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun PreviewDeviceSearchingContent() {
    DeviceSearchingContent(onBackClick = {}, onMockTag = {})
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun PreviewDeviceSuccessContent() {
    DeviceSuccessContent(onNavigateToDecorate = {}, onNavigateToHome = {})
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun PreviewDeviceFailureContent() {
    DeviceFailureContent(onBackClick = {}, onRetryClick = {}, onNavigateToHelp = {})
}
