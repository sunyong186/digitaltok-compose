package com.yourcompany.digitaltok.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yourcompany.digitaltok.ui.theme.*

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
                        if (index == current) DtMain100 else DtBorderGray,
                        CircleShape
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
                if (enabled) containerColor else DtBorderGray,
                RoundedCornerShape(8.dp)
            )
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = if (enabled) textColor else DtTextGray1,
            textAlign = TextAlign.Center
        )
    }
}
