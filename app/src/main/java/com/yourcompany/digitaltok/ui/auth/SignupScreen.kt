package com.yourcompany.digitaltok.ui.auth

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yourcompany.digitaltok.R
import com.yourcompany.digitaltok.data.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Preview(showBackground = true)
@Composable
fun SignupScreenPreview() {
    SignupScreen(
        onBackClick = {},
        onSignupSuccess = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(
    onBackClick: () -> Unit,
    onSignupSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordConfirm by remember { mutableStateOf("") }

    var pwVisible by remember { mutableStateOf(false) }
    var pwConfirmVisible by remember { mutableStateOf(false) }

    var isEmailChecked by remember { mutableStateOf(false) }
    var emailStatusMessage by remember { mutableStateOf("") }
    var isEmailStatusOk by remember { mutableStateOf(false) }

    var isCheckingEmail by remember { mutableStateOf(false) }
    var isSigningUp by remember { mutableStateOf(false) }

    var cbTerms1 by remember { mutableStateOf(false) }
    var cbTerms2 by remember { mutableStateOf(false) }
    var cbTerms3 by remember { mutableStateOf(false) }

    val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
    val isPasswordValid = password.length >= 6
    val isPasswordMatch = password == passwordConfirm && password.isNotEmpty()

    val greenColor = Color(0xFF00C950)
    val redColor = Color(0xFFFB2C36)
    val buttonBlue = Color(0xFF36ABFF)
    val buttonDisabled = Color(0xFFE9E9E9)

    val requiredAgreed = cbTerms1 && cbTerms2
    val isSignupEnabled = isEmailChecked && isPasswordValid && isPasswordMatch && requiredAgreed && !isSigningUp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
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
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "로그인하러 가기",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier
                    .clickable { onBackClick() }
                    .padding(8.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text("회원가입", fontSize = 24.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(32.dp))

            // Email
            Text("이메일", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        isEmailChecked = false
                        emailStatusMessage = ""
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    placeholder = { Text("이메일을 입력하세요", fontSize = 14.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF4F4F4),
                        unfocusedContainerColor = Color(0xFFF4F4F4),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        val trimmedEmail = email.trim()
                        if (!Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
                            emailStatusMessage = "이메일 형식을 확인해주세요."
                            isEmailStatusOk = false
                            isEmailChecked = false
                            return@Button
                        }
                        scope.launch {
                            isCheckingEmail = true
                            try {
                                val res = withContext(Dispatchers.IO) {
                                    authRepository.duplicateCheck(trimmedEmail)
                                }
                                if (res.isSuccessful && res.body()?.isSuccess == true) {
                                    isEmailChecked = true
                                    isEmailStatusOk = true
                                    emailStatusMessage = "사용 가능한 이메일이에요."
                                } else {
                                    isEmailChecked = false
                                    isEmailStatusOk = false
                                    emailStatusMessage = if (res.code() == 409) "이미 사용 중인 이메일이에요." else "중복확인 실패 (HTTP ${res.code()})"
                                }
                            } catch (e: Exception) {
                                isEmailChecked = false
                                isEmailStatusOk = false
                                emailStatusMessage = "네트워크 오류: ${e.message}"
                            } finally {
                                isCheckingEmail = false
                            }
                        }
                    },
                    enabled = isEmailValid && !isCheckingEmail,
                    modifier = Modifier.height(52.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isEmailChecked) greenColor else buttonBlue,
                        disabledContainerColor = buttonDisabled,
                        contentColor = Color.White
                    )
                ) {
                    Text(if (isEmailChecked) "확인됨" else "중복확인")
                }
            }
            if (emailStatusMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = emailStatusMessage,
                    color = if (isEmailStatusOk) greenColor else redColor,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Password
            Text("비밀번호", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                placeholder = { Text("비밀번호 (6자 이상)", fontSize = 14.sp) },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                visualTransformation = if (pwVisible) VisualTransformation.None else PasswordVisualTransformation(),
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
            if (password.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isPasswordValid) "사용 가능한 비밀번호예요." else "비밀번호는 6자 이상이어야 해요.",
                    color = if (isPasswordValid) greenColor else redColor,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Password Confirm
            OutlinedTextField(
                value = passwordConfirm,
                onValueChange = { passwordConfirm = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                placeholder = { Text("비밀번호 확인", fontSize = 14.sp) },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                visualTransformation = if (pwConfirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.eye_closed),
                        contentDescription = null,
                        modifier = Modifier
                            .size(22.dp)
                            .clickable { pwConfirmVisible = !pwConfirmVisible }
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF4F4F4),
                    unfocusedContainerColor = Color(0xFFF4F4F4),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                )
            )
            if (passwordConfirm.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isPasswordMatch) "비밀번호가 일치해요." else "비밀번호가 일치하지 않아요.",
                    color = if (isPasswordMatch) greenColor else redColor,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Terms
            Text("약관 동의", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))

            TermCheckbox(
                text = "[필수] 서비스 이용약관 동의",
                checked = cbTerms1,
                onCheckedChange = { cbTerms1 = it },
                redColor = redColor
            )
            TermCheckbox(
                text = "[필수] 개인정보 수집 및 이용 동의",
                checked = cbTerms2,
                onCheckedChange = { cbTerms2 = it },
                redColor = redColor
            )
            TermCheckbox(
                text = "[선택] 마케팅 정보 수신 동의",
                checked = cbTerms3,
                onCheckedChange = { cbTerms3 = it },
                redColor = redColor
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    scope.launch {
                        isSigningUp = true
                        try {
                            val res = withContext(Dispatchers.IO) {
                                authRepository.signup(email.trim(), password, "000-0000-0000")
                            }
                            if (res.isSuccessful && res.body()?.isSuccess == true) {
                                Toast.makeText(context, "회원가입 성공!", Toast.LENGTH_SHORT).show()
                                onSignupSuccess()
                            } else {
                                Toast.makeText(context, res.body()?.message ?: "회원가입 실패 (HTTP ${res.code()})", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
                        } finally {
                            isSigningUp = false
                        }
                    }
                },
                enabled = isSignupEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonBlue,
                    disabledContainerColor = buttonDisabled,
                    contentColor = Color.White
                )
            ) {
                Text(if (isSigningUp) "처리 중..." else "가입하기")
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun TermCheckbox(text: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit, redColor: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onCheckedChange(!checked) }
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = Color(0xFF36ABFF),
                uncheckedColor = Color.Gray
            )
        )
        Spacer(modifier = Modifier.width(8.dp))

        val annotatedString = buildAnnotatedString {
            if (text.startsWith("[필수]")) {
                withStyle(style = SpanStyle(color = redColor)) {
                    append("[필수]")
                }
                append(text.substring(4))
            } else {
                append(text)
            }
        }
        Text(text = annotatedString, fontSize = 14.sp)
    }
}
