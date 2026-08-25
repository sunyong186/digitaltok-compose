package com.yourcompany.digitaltok.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yourcompany.digitaltok.ui.theme.*

// ==================== 1. 로그아웃 다이얼로그 (bg_logout_dialog 1:1 이식) ====================

@Preview(showBackground = true)
@Composable
fun LogoutDialogContentPreview() {
    LogoutDialogContent(onConfirm = {}, onDismiss = {})
}

@Composable
fun LogoutDialogContent(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 21.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "로그아웃",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = DtTextBlack,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(9.dp))

            Text(
                text = "정말 로그아웃 하시겠습니까?",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = DtTextGray3,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 로그아웃 버튼 (btnLogout: 빨간색, 44dp 높이)
                Button(
                    onClick = onConfirm,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DtFavoriteRed,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "로그아웃",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // 돌아가기 버튼 (btnCancel: 회색, 44dp 높이)
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DtLightGrayBg,
                        contentColor = DtTextGray3
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "돌아가기",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ==================== 2. 회원탈퇴 다이얼로그 (dialog_withdraw.xml 1:1 이식) ====================

@Preview(showBackground = true)
@Composable
fun WithdrawDialogContentPreview() {
    WithdrawDialogContent(onConfirm = {}, onDismiss = {})
}

@Composable
fun WithdrawDialogContent(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 21.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 타이틀 ("회원 탈퇴")
            Text(
                text = "회원 탈퇴",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DtTextBlack,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(9.dp))

            // 메시지 ("정말 회원탈퇴를 하시겠습니까?")
            Text(
                text = "정말 회원탈퇴를 하시겠습니까?",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = DtTextGray3,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 경고 박스 (warnBox: bg_withdraw_warning_box 1:1)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = DtWithdrawBg,
                border = BorderStroke(1.dp, DtFavoriteRed.copy(alpha = 0.5f))
            ) {
                Text(
                    text = "※ 탈퇴 시 주의사항:\n\n• 모든 계정 정보가 삭제됩니다\n• 저장된 사진과 템플릿이 모두 삭제됩니다\n• 이 작업은 되돌릴 수 없습니다",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DtFavoriteRed,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(start = 20.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 버튼 영역 (btnRow)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 회원탈퇴 버튼 (btnWithdraw: 빨간색, 48dp 높이)
                Button(
                    onClick = onConfirm,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DtFavoriteRed,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "회원탈퇴",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // 돌아가기 버튼 (btnCancel: 회색, 48dp 높이)
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DtLightGrayBg,
                        contentColor = DtTextGray3
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "돌아가기",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ==================== 3. 이메일 변경 다이얼로그 (bg_dialog_card_white 1:1 이식) ====================

@Composable
private fun CustomDialogInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholderText: String,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(8.dp),
        color = DtLightGrayBg
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            if (value.isEmpty()) {
                Text(
                    text = placeholderText,
                    fontSize = 14.sp,
                    color = DtTextGray3.copy(alpha = 0.6f)
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    color = DtTextBlack
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChangeEmailDialogContentPreview() {
    ChangeEmailDialogContent(onConfirm = { _, _ -> }, onDismiss = {})
}

@Composable
fun ChangeEmailDialogContent(
    onConfirm: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var password by remember { mutableStateOf("") }
    var newEmail by remember { mutableStateOf("") }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 21.dp, vertical = 20.dp)
        ) {
            // 타이틀 ("이메일 변경")
            Text(
                text = "이메일 변경",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = DtTextBlack,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(9.dp))

            // 설명 ("기존 비밀번호 확인 후 새 이메일로 변경합니다.")
            Text(
                text = "기존 비밀번호 확인 후 새 이메일로 변경합니다.",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = DtTextGray3,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // 안내 박스 (infoBox: bg_withdraw_warning_box 1:1)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = DtWithdrawBg,
                border = BorderStroke(1.dp, DtFavoriteRed.copy(alpha = 0.5f))
            ) {
                Text(
                    text = "• 변경 후에는 새 이메일로 로그인해야 합니다.\n• 기존 데이터(사진/템플릿)는 그대로 유지됩니다.",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DtFavoriteRed,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(start = 20.dp, end = 16.dp, top = 14.dp, bottom = 14.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 입력 영역 (inputBox)
            Text(
                text = "기존 비밀번호",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = DtTextGray3
            )

            Spacer(modifier = Modifier.height(6.dp))

            CustomDialogInputField(
                value = password,
                onValueChange = { password = it },
                placeholderText = "기존 비밀번호 입력",
                isPassword = true,
                keyboardType = KeyboardType.Password
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "변경할 새 이메일",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = DtTextGray3
            )

            Spacer(modifier = Modifier.height(6.dp))

            CustomDialogInputField(
                value = newEmail,
                onValueChange = { newEmail = it },
                placeholderText = "새 이메일 입력",
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 버튼 영역 (btnRow)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 변경 버튼 (btnChange: 빨간색, 48dp 높이)
                Button(
                    onClick = { onConfirm(password, newEmail) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DtFavoriteRed,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "변경",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // 취소 버튼 (btnCancel: 회색, 48dp 높이)
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DtLightGrayBg,
                        contentColor = DtTextGray3
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "취소",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
