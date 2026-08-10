package com.yourcompany.digitaltok.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val BackArrowIcon: ImageVector
    get() {
        if (_backArrowIcon != null) {
            return _backArrowIcon!!
        }
        _backArrowIcon = ImageVector.Builder(
            name = "BackArrowIcon",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(15.5f, 19f)
                arcToRelative(1f, 1f, 0f, false, true, -0.7f, -0.3f)
                lineToRelative(-6.2f, -6.2f)
                arcToRelative(1f, 1f, 0f, false, true, 0f, -1.4f)
                lineToRelative(6.2f, -6.2f)
                arcToRelative(1f, 1f, 0f, true, true, 1.4f, 1.4f)
                lineTo(10.7f, 12f)
                lineToRelative(5.5f, 5.5f)
                arcTo(1f, 1f, 0f, false, true, 15.5f, 19f)
                close()
            }
        }.build()
        return _backArrowIcon!!
    }

private var _backArrowIcon: ImageVector? = null
