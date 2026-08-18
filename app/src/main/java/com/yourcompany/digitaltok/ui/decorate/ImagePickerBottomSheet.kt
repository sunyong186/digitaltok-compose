package com.yourcompany.digitaltok.ui.decorate

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagePickerBottomSheet(
    onDismissRequest: () -> Unit,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = Color.Transparent,
        scrimColor = Color.Black.copy(alpha = 0.5f),
        dragHandle = null
    ) {
        ImagePickerBottomSheetContent(
            onDismissRequest = onDismissRequest,
            onCameraClick = onCameraClick,
            onGalleryClick = onGalleryClick
        )
    }
}

@Composable
fun ImagePickerBottomSheetContent(
    onDismissRequest: () -> Unit,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit
) {
    val main100Color = Color(0xFF36ABFF)
    val defaultGrayColor = Color(0xFFE0E0E0)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp, top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. 위 2개: 흰 카드 (CardView: cardCornerRadius=6dp, marginStart/End=18dp)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            shape = RoundedCornerShape(6.dp),
            color = Color.White,
            shadowElevation = 0.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // tvCamera: 카메라로 사진찍기
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onDismissRequest()
                            onCameraClick()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "카메라로 사진찍기",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = main100Color
                    )
                }

                // Divider: height=1dp, marginVertical=18dp
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 18.dp),
                    thickness = 1.dp,
                    color = defaultGrayColor
                )

                // tvGallery: 갤러리에서 가져오기
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onDismissRequest()
                            onGalleryClick()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "갤러리에서 가져오기",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = main100Color
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 2. 파란 버튼 영역 (btnCancel: 돌아가기)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onDismissRequest()
                },
            shape = RoundedCornerShape(4.dp),
            color = main100Color
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "돌아가기",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0x80000000, name = "Image Picker Bottom Sheet Preview")
@Composable
fun ImagePickerBottomSheetContentPreview() {
    ImagePickerBottomSheetContent(
        onDismissRequest = {},
        onCameraClick = {},
        onGalleryClick = {}
    )
}
