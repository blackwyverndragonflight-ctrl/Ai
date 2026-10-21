package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.sp
import com.example.R

// Authentic Iran Sans (ایران سنس) Persian Typography
val IranSansFontFamily = FontFamily(
    Font(R.font.iransans, FontWeight.Normal),
    Font(R.font.iransans_bold, FontWeight.Bold),
    Font(R.font.iransans_bold, FontWeight.SemiBold),
    Font(R.font.iransans, FontWeight.Medium)
)

val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = IranSansFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp,
        textDirection = TextDirection.ContentOrRtl
    ),
    displayMedium = TextStyle(
        fontFamily = IranSansFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp,
        textDirection = TextDirection.ContentOrRtl
    ),
    headlineLarge = TextStyle(
        fontFamily = IranSansFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp,
        textDirection = TextDirection.ContentOrRtl
    ),
    headlineMedium = TextStyle(
        fontFamily = IranSansFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
        textDirection = TextDirection.ContentOrRtl
    ),
    titleLarge = TextStyle(
        fontFamily = IranSansFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.sp,
        textDirection = TextDirection.ContentOrRtl
    ),
    titleMedium = TextStyle(
        fontFamily = IranSansFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
        textDirection = TextDirection.ContentOrRtl
    ),
    titleSmall = TextStyle(
        fontFamily = IranSansFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp,
        textDirection = TextDirection.ContentOrRtl
    ),
    bodyLarge = TextStyle(
        fontFamily = IranSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp,
        textDirection = TextDirection.ContentOrRtl
    ),
    bodyMedium = TextStyle(
        fontFamily = IranSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 19.sp,
        letterSpacing = 0.sp,
        textDirection = TextDirection.ContentOrRtl
    ),
    bodySmall = TextStyle(
        fontFamily = IranSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp,
        textDirection = TextDirection.ContentOrRtl
    ),
    labelLarge = TextStyle(
        fontFamily = IranSansFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp,
        textDirection = TextDirection.ContentOrRtl
    ),
    labelMedium = TextStyle(
        fontFamily = IranSansFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp,
        textDirection = TextDirection.ContentOrRtl
    ),
    labelSmall = TextStyle(
        fontFamily = IranSansFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.sp,
        textDirection = TextDirection.ContentOrRtl
    )
)
