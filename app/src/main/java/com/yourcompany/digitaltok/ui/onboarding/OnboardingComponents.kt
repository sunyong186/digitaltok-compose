package com.yourcompany.digitaltok.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OnboardingIndicator(total: Int, current: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(total) { index ->
            Box(
                modifier = Modifier
                    .height(10.dp)
                    .width(if (index == current) 24.dp else 10.dp)
                    .background(
                        if (index == current) Color(0xFF36ABFF) else Color(0xFFD9D9D9),
                        RoundedCornerShape(50)
                    )
            )
            if (index != total - 1) {
                Box(modifier = Modifier.width(5.dp))
            }
        }
    }
}

@Composable
fun OnboardingButton(
    text: String,
    containerColor: Color,
    textColor: Color,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(52.dp)
            .background(
                if (enabled) containerColor else Color(0xFFE9E9E9),
                RoundedCornerShape(8.dp)
            )
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = if (enabled) textColor else Color(0xFF9E9E9E),
            textAlign = TextAlign.Center
        )
    }
}
