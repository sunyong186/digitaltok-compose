package com.yourcompany.digitaltok.ui.auth

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.border
import com.yourcompany.digitaltok.data.repository.AuthRepository
import com.yourcompany.digitaltok.ui.components.BackArrowIcon
import com.yourcompany.digitaltok.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Preview(showBackground = true)
@Composable
fun PasswordResetScreenPreview() {
    PasswordResetScreen(
        onBackClick = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordResetScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }

    var email by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf("") }

    val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
    val buttonBlue = DtMain100
    val buttonDisabled = DtBorderGray

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DtWhite)
            .statusBarsPadding()
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = BackArrowIcon,
                    contentDescription = "Back"
                )
            }
            Text(
                text = "이메일로 로그인",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DtTextBlack,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 48.dp) // Offset for the back button
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "비밀번호 찾기",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "가입하신 이메일 주소를 입력하시면 비밀번호\n재설정 링크를 보내드립니다",
                fontSize = 14.sp,
                color = DtTextGray1,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    statusMessage = ""
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                placeholder = { Text("example@mail.com", fontSize = 14.sp, color = DtTextGray1) },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = DtLightGrayBg,
                    unfocusedContainerColor = DtLightGrayBg,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                )
            )

            if (statusMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = statusMessage,
                    color = DtTextBlack,
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Spacer(modifier = Modifier.height(140.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DtBorderGray, RoundedCornerShape(12.dp))
                    .background(DtLightGrayBg, RoundedCornerShape(12.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            tint = DtTextGray1,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("참고사항", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DtTextBlack)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("• 이메일 도착까지 최대 5분이 소요됩니다", fontSize = 13.sp, color = DtTextGray1)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("• 스팸함을 확인해주세요", fontSize = 13.sp, color = DtTextGray1)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("• 링크는 24시간 동안 유효합니다", fontSize = 13.sp, color = DtTextGray1)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val trimmedEmail = email.trim()
                    if (!Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
                        Toast.makeText(context, "이메일 형식을 확인해주세요.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    scope.launch {
                        isSending = true
                        try {
                            val res = withContext(Dispatchers.IO) {
                                authRepository.resetPassword(trimmedEmail)
                            }
                            if (res.isSuccessful) {
                                val body = res.body()
                                if (body?.isSuccess == true) {
                                    val msg = body.result ?: body.message ?: "재설정 요청이 완료됐어요."
                                    statusMessage = msg
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                } else {
                                    val msg = body?.message ?: "요청 실패"
                                    statusMessage = msg
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                val msg = "요청 실패 (HTTP ${res.code()})"
                                statusMessage = msg
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            val msg = "네트워크 오류: ${e.message}"
                            statusMessage = msg
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        } finally {
                            isSending = false
                        }
                    }
                },
                enabled = isEmailValid && !isSending,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonBlue,
                    disabledContainerColor = buttonDisabled,
                    contentColor = DtWhite
                )
            ) {
                Text(if (isSending) "전송 중..." else "재설정 링크 보내기")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onBackClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DtLightGrayBg,
                    contentColor = DtTextGray1
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text("돌아가기")
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
