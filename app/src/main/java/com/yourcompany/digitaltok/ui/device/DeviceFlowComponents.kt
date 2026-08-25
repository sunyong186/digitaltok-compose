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
import com.yourcompany.digitaltok.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlowTopAppBar(title: String, onBackClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = DtTextBlack
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = BackArrowIcon,
                    contentDescription = "Back",
                    tint = DtTextBlack
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = DtWhite
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
            .background(DtWhite)
    ) {
        FlowTopAppBar(title = "기기연결", onBackClick = onBackClick)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(30.dp))
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
                color = DtTextBlack
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "NFC를 통해 전자 잉크 디링을 연결하세요",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = DtTextGray2
            )
            Spacer(modifier = Modifier.height(36.dp))
            Button(
                onClick = onProceedClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DtPointBlue,
                    contentColor = DtWhite
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
                    color = DtLightGrayBg,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                )
                .padding(horizontal = 35.dp, vertical = 30.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Image(
                    painter = painterResource(id = R.drawable.ic_alert_circle),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp).padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("NFC 활성화 확인", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = DtTextBlack)
                    Spacer(Modifier.height(4.dp))
                    Text("설정 > 연결에서 NFC가 켜져있는지 확인하세요", fontSize = 14.sp, color = DtTextGray2)
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
                    Text("올바른 위치", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = DtTextBlack)
                    Spacer(Modifier.height(4.dp))
                    Text("휴대폰 뒷면을 그립톡 중앙에 가까이 대주세요", fontSize = 14.sp, color = DtTextGray2)
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
            .background(DtWhite)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                // Mock NFC tagging for emulator
                onMockTag("ERROR_FAIL")
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FlowTopAppBar(title = "기기 연결", onBackClick = onBackClick)

        Spacer(modifier = Modifier.weight(1f))
        
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(25.dp).background(DtTextGray3, CircleShape))
            Spacer(modifier = Modifier.width(19.dp))
            Box(modifier = Modifier.size(25.dp).background(DtTextGray3, CircleShape))
            Spacer(modifier = Modifier.width(19.dp))
            Box(modifier = Modifier.size(25.dp).background(DtTextGray3, CircleShape))
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "연결 중..",
            fontSize = 26.sp,
            fontWeight = FontWeight.SemiBold,
            color = DtTextGray1
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "휴대폰을 디링에\n연결 중 입니다",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = DtTextGray2,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.weight(1.5f))
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
            .background(DtWhite)
            .padding(horizontal = 24.dp)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))
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
            color = DtPointBlue
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "디링이 성공적으로\n연결되었습니다",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = DtTextGray50,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onNavigateToDecorate,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DtPointBlue),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("이미지 or 템플릿 추가하기", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = DtWhite)
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = onNavigateToHome,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DtLightGrayBg),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("홈으로 돌아가기", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = DtTextGray2)
        }
        Spacer(modifier = Modifier.height(20.dp))
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
            .background(DtWhite)
    ) {
        FlowTopAppBar(title = "", onBackClick = onBackClick)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))
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
                color = DtFavoriteRed
            )
            Spacer(modifier = Modifier.height(11.dp))
            Text(
                text = "기기를 찾을 수\n없습니다",
                fontSize = 18.sp,
                color = DtTextGray2,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(0.5f))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DtLightGrayBg, RoundedCornerShape(12.dp))
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_alert_circle),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("해결 방법", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DtTextGray1)
                }
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "•  NFC 기능이 활성화되어 있는지 확인하세요\n•  휴대폰 케이스를 제거하고 다시 시도하세요\n•  디링을 휴대폰 뒷면 중앙에 가까이 대세요",
                    fontSize = 14.sp,
                    color = DtTextGray2,
                    lineHeight = 22.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onRetryClick,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DtPointBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("다시 시도", fontSize = 16.sp, color = DtWhite)
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = onNavigateToHelp,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DtLightGrayBg),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("고객지원 문의하기", fontSize = 16.sp, color = DtTextGray2)
            }
            Spacer(modifier = Modifier.height(20.dp))
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
