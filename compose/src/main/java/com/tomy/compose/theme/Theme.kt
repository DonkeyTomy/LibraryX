package com.tomy.compose.theme

import android.os.Build
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext


/**@author Tomy
 * Created by Tomy on 2022/1/20.
 */
private val LightColorScheme = lightColorScheme(
    primary = ColorPrimary,
    secondary = ColorSecondary,
    onPrimary = Color.White,
    onPrimaryContainer = Color.Black,
    tertiary = ColorTertiary,
    onSurface = Color.White,
    primaryContainer = Color.White
)

@Composable
fun MainTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    isDynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val dynamicColor = isDynamicColor && Build.VERSION.SDK_INT >= 31
    val myColorScheme = when {
        dynamicColor && isDarkTheme -> {
            dynamicDarkColorScheme(LocalContext.current)
        }
        dynamicColor && !isDarkTheme -> {
            dynamicLightColorScheme(LocalContext.current)
        }
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = myColorScheme,
        typography = MainTypography,
        shapes = MaterialTheme.shapes
    ) {
        // TODO (M3): MaterialTheme doesn't provide LocalIndication, remove when it does
        val rippleIndication = rememberRipple()
        CompositionLocalProvider(
            LocalIndication provides rippleIndication,
            LocalIconColor provides Color.White,
            content = content
        )
    }
}


val LocalIconColor = staticCompositionLocalOf { Color.Black }

val LocalContainerColor = staticCompositionLocalOf { Color.Transparent }

val LocalTitleTextStyle = compositionLocalOf(structuralEqualityPolicy()) {
    MainTypography.titleLarge
}