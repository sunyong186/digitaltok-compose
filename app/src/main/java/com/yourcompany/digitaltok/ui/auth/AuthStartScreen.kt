package com.yourcompany.digitaltok.ui.auth

import android.content.Context
import android.content.Intent
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.fontResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yourcompany.digitaltok.R
import com.yourcompany.digitaltok.data.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AuthStartScreen(
    modifier: Modifier = Modifier,
    onLoginSuccess: () -> Unit = {},
    onSignupClick: () -> Unit = {},
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var pwVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val pretendard = FontFamily(
        Font(R.font.pretendard_regular, FontWeight.Normal),
        Font(R.font.pretendard_medium, FontWeight.Medium),
        Font(R.font.pretendard_semibold, FontWeight.SemiBold),
        Font(R.font.pretendard_bold, FontWeight.Bold)
    )

    val isLoginEnabled =
        email.trim().isNotEmpty() &&
                password.isNotEmpty() &&
                Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() &&
                !isLoading

    val authRepository = remember { AuthRepository() }
    val noRippleInteraction = remember { MutableInteractionSource() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // 로고 (48 x 75)
        Image(
            painter = painterResource(id = R.drawable.diringlogo),
            contentDescription = null,
            modifier = Modifier
                .padding(0.70747.dp)
                .width(48.dp)
                .height(75.dp)
        )

        Spacer(Modifier.height(37.dp))

        // DiRing 텍스트 (42sp)
        Text(
            text = "DiRing",
            style = TextStyle(
                fontSize = 36.sp,
                lineHeight = 40.sp,
                fontFamily = pretendard,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111111),
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .width(127.dp)
                .height(42.dp),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(13.dp))

        // 서브 텍스트 (20sp)
        Text(
            text = "내 마음대로 꾸미는 나만의 키링",
            style = TextStyle(
                fontSize = 18.sp,
                lineHeight = 20.sp,
                fontFamily = pretendard,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF505050),
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .width(242.dp)
                .height(20.dp),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(48.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            placeholder = {
                Text("이메일을 입력하세요", fontSize = 14.sp)
            },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF4F4F4),
                unfocusedContainerColor = Color(0xFFF4F4F4),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            )
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            placeholder = {
                Text("비밀번호를 입력하세요", fontSize = 14.sp)
            },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            visualTransformation =
                if (pwVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
            trailingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.eye_closed),
                    contentDescription = null,
                    modifier = Modifier
                        .size(22.dp)
                        .clickable { pwVisible = !pwVisible }
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF4F4F4),
                unfocusedContainerColor = Color(0xFFF4F4F4),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            )
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                val e = email.trim()
                val p = password

                scope.launch {
                    isLoading = true
                    try {
                        val response = withContext(Dispatchers.IO) {
                            authRepository.login(e, p)
                        }

                        if (!response.isSuccessful) {
                            Toast.makeText(context, "로그인 실패", Toast.LENGTH_SHORT).show()
                            return@launch
                        }

                        val body = response.body()
                        if (body?.isSuccess != true || body.result == null) {
                            Toast.makeText(context, body?.message ?: "로그인 실패", Toast.LENGTH_SHORT).show()
                            return@launch
                        }

                        saveAuth(context,
                            body.result.accessToken,
                            body.result.refreshToken,
                            true
                        )

                        Toast.makeText(context, "로그인 성공!", Toast.LENGTH_SHORT).show()
                        onLoginSuccess()

                    } catch (e: Exception) {
                        Toast.makeText(context, "네트워크 오류", Toast.LENGTH_SHORT).show()
                    } finally {
                        isLoading = false
                    }
                }
            },
            enabled = isLoginEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isLoginEnabled) Color(0xFF36ABFF) else Color(0xFFE9E9E9),
                contentColor = if (isLoginEnabled) Color.White else Color.Gray
            )
        ) {
            Text(if (isLoading) "로그인 중..." else "로그인")
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onSignupClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF111111))
        ) {
            Text("회원가입", color = Color.White)
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = "비밀번호를 잊으셨나요?",
            fontSize = 12.sp,
            color = Color(0xFF767676),
            modifier = Modifier.clickable(
                interactionSource = noRippleInteraction,
                indication = null
            ) {
                context.startActivity(
                    Intent(context, PasswordResetActivity::class.java)
                )
            }
        )
    }
}

private fun saveAuth(
    context: Context,
    accessToken: String,
    refreshToken: String,
    autoLogin: Boolean
) {
    val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    prefs.edit()
        .putString("accessToken", accessToken)
        .putString("refreshToken", refreshToken)
        .putBoolean("autoLogin", autoLogin)
        .apply()
}
