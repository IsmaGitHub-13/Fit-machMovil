package com.fic.mobile_app_base_compose.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape

private val LightColorScheme = lightColorScheme(
    primary          = FitmachRed,
    onPrimary        = OnPrimary,
    primaryContainer = FitmachRedLight,

    secondary        = MetalGrey,
    onSecondary      = OnSecondary,
    secondaryContainer = MetalGreyLight,

    background       = BackgroundLight,
    onBackground     = MetalGreyDark,

    surface          = SurfaceLight,
    onSurface        = MetalGreyDark,

    error            = FitmachRedDark,
)
private val DarkColorScheme = darkColorScheme(
    primary          = FitmachRedLight,
    onPrimary        = OnPrimary,
    primaryContainer = FitmachRedDark,

    secondary        = MetalGreyLight,
    onSecondary      = OnSecondary,
    secondaryContainer = MetalGreyDark,

    background       = BackgroundDark,
    onBackground     = BackgroundLight,

    surface          = SurfaceDark,
    onSurface        = BackgroundLight,

    error            = FitmachRedLight,
)

private val FitmachShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small      = RoundedCornerShape(12.dp),
    medium     = RoundedCornerShape(16.dp),
    large      = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun MobileappbasecomposeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        shapes      = FitmachShapes,
        content     = content
    )
}