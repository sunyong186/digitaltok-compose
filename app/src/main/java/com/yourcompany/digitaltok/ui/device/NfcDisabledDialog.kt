package com.yourcompany.digitaltok.ui.device

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.yourcompany.digitaltok.R
import com.yourcompany.digitaltok.ui.theme.*

@Composable
fun NfcDisabledDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = DtWhite,
            shadowElevation = 10.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_phone),
                    contentDescription = null,
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "NFC가 켜져 있지 않습니다.",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = DtTextBlack
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "DiRing을 핸드폰 뒷면에 밀착시켜 주세요",
                    fontSize = 14.sp,
                    color = DtTextGray2
                )
                Spacer(modifier = Modifier.height(25.dp))
                Button(
                    onClick = {
                        onDismiss()
                        val intent = Intent(Settings.ACTION_NFC_SETTINGS)
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DtPointBlue
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("확인", color = DtWhite, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DtLightGrayBg
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("취소", color = DtTextGray2, fontSize = 16.sp)
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun PreviewNfcDisabledDialog() {
    NfcDisabledDialog(onDismiss = {})
}
