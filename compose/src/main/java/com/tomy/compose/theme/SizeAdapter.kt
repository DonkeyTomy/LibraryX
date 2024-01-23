package com.tomy.compose.theme

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Density
import timber.log.Timber

/**@author Tomy
 * Created by Tomy on 2023/8/17.
 */

/**
 * 根据UI设计图动态适配不同屏幕
 * @param designWidth
 * @param designHeight
 */
@Composable
fun dynamicDensity(designWidth: Float = 720f, designHeight: Float = 1280f): Density {
    val displayMetrics = LocalContext.current.resources.displayMetrics
    val widthPixels     = displayMetrics.widthPixels
    val heightPixels    = displayMetrics.heightPixels
    val isPortrait  = LocalContext.current.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    val density = if (isPortrait) widthPixels / designWidth else heightPixels / designHeight
    Timber.v("density: $density; w x h: ${widthPixels}x$heightPixels")
    return Density(density, 1f)
}