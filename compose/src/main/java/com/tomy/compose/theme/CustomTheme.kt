package com.tomy.compose.theme

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

/**@author Tomy
 * Created by Tomy on 2022/1/14.
 */
@Stable
class CustomTheme(
    txtColor: Color,
    txtClickColor: Color,
    backgroundColor: Color,
    backgroundClickColor: Color
) {
    var txtColor by mutableStateOf(txtColor)
        private set

    var txtClickColor by mutableStateOf(txtClickColor)
        private set

    var backgroundColor by mutableStateOf(backgroundColor)
        private set

    var backgroundClickColor by mutableStateOf(backgroundClickColor)
        private set

    fun update(customTheme: CustomTheme) {
        txtColor        = customTheme.txtColor
        txtClickColor   = customTheme.txtClickColor
        backgroundColor = customTheme.backgroundColor
        backgroundClickColor    = customTheme.backgroundClickColor
    }

    fun copy(titleColor: Color = this.txtColor,
        txtClickColor: Color = this.txtClickColor,
        backgroundColor: Color = this.backgroundColor,
        backgroundClickColor: Color = this.backgroundClickColor) = CustomTheme(titleColor, txtClickColor, backgroundColor, backgroundClickColor)
}