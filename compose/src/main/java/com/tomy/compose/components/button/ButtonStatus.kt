package com.tomy.compose.components.button

import androidx.annotation.DrawableRes
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape

/**@author Tomy
 * Created by Tomy on 2023/8/31.
 */
data class ButtonStatus(
    var enabled: Boolean = true,
    @DrawableRes
    var iconRes: Int,
    var shape: Shape = RectangleShape,
    var backgroundColor: Color = Color.White,
    var disableColor: Color = Color.Gray,
    var iconTint: Color = Color.White,
    var iconDisableColor: Color = disableColor,
    var txtRes: Any? = null
)

val ButtonStatusSaver = listSaver(
    save = {
        listOf(it.enabled, it.iconRes, /*it.shape, it.backgroundColor, it.disableColor, it.iconTint, it.iconDisableColor, it.txtRes*/)
    },
    restore = {
        ButtonStatus(
            it[0] as Boolean,
            it[1] as Int,
            /*it[2] as Shape,
            it[3] as Color,
            it[4] as Color,
            it[5] as Color,
            it[6] as Color,
            it[7]*/
        )
    }
)