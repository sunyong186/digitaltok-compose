package com.yourcompany.digitaltok.ui.decorate

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.yourcompany.digitaltok.R
import com.yourcompany.digitaltok.ui.theme.*

sealed class TransferDialogState {
    object Transferring : TransferDialogState()
    object Success : TransferDialogState()
    data class Fail(val message: String? = null) : TransferDialogState()
}

@Composable
fun NfcTransferDialog(
    state: TransferDialogState,
    onDismissRequest: () -> Unit,
    onRetryClick: () -> Unit,
    onSupportClick: () -> Unit
) {
    Dialog(
        onDismissRequest = {
            if (state !is TransferDialogState.Transferring) {
                onDismissRequest()
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = state !is TransferDialogState.Transferring,
            dismissOnClickOutside = state !is TransferDialogState.Transferring
        )
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            when (state) {
                is TransferDialogState.Transferring -> TransferringContent()
                is TransferDialogState.Success -> SuccessContent()
                is TransferDialogState.Fail -> FailContent(
                    message = state.message,
                    onRetryClick = onRetryClick,
                    onSupportClick = onSupportClick
                )
            }
        }
    }
}

@Composable
private fun TransferringContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(64.dp),
            color = DtPointBlue,
            strokeWidth = 4.dp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "전송 중",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = DtTextBlack
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "디링을 핸드폰 NFC 센서에 밀착시켜 주세요",
            fontSize = 15.sp,
            color = DtTextGray2,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SuccessContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "전송 완료",
            tint = DtPointBlue,
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "전송 완료",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = DtTextBlack
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "성공적으로 이미지가 전송되었습니다",
            fontSize = 15.sp,
            color = DtTextGray2,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun FailContent(
    message: String?,
    onRetryClick: () -> Unit,
    onSupportClick: () -> Unit
) {
    val errorColor = DtFavoriteRed
    val warningBoxBg = DtWithdrawBg

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = "전송 실패",
            tint = errorColor,
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "전송 실패",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = errorColor
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message ?: "이미지 전송에 실패했습니다",
            fontSize = 15.sp,
            color = DtTextGray2,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 해결 방법 가이드 박스
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(warningBoxBg)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = errorColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "해결 방법",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = errorColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "• 핸드폰 뒷면에 장치를 밀착시켜 주세요",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = errorColor
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "• 네트워크 연결을 확인해 주세요",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = errorColor
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onRetryClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DtPointBlue
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                text = "다시 시도",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onSupportClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = DtTextGray2
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                text = "고객 지원 문의하기",
                fontSize = 15.sp,
                color = DtTextGray2
            )
        }
    }
}

// ==================== Previews ====================

@Preview(name = "NFC Transferring Dialog")
@Composable
fun NfcTransferringDialogPreview() {
    NfcTransferDialog(
        state = TransferDialogState.Transferring,
        onDismissRequest = {},
        onRetryClick = {},
        onSupportClick = {}
    )
}

@Preview(name = "NFC Success Dialog")
@Composable
fun NfcSuccessDialogPreview() {
    NfcTransferDialog(
        state = TransferDialogState.Success,
        onDismissRequest = {},
        onRetryClick = {},
        onSupportClick = {}
    )
}

@Preview(name = "NFC Fail Dialog")
@Composable
fun NfcFailDialogPreview() {
    NfcTransferDialog(
        state = TransferDialogState.Fail("네트워크가 불안정합니다."),
        onDismissRequest = {},
        onRetryClick = {},
        onSupportClick = {}
    )
}
