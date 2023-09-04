package com.tomy.compose.components.button

import androidx.annotation.DrawableRes
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape

/**@author Tomy
 * Created by Tomy on 2023/8/31.
 */
@Stable
data class ButtonStatus(
    var enabled: MutableState<Boolean> = mutableStateOf(true),
    @DrawableRes
    var iconRes: MutableState<Int>,
    val shape: Shape = RectangleShape,
    val backgroundColor: Color = Color.White,
    val disableColor: Color = Color.Gray,
    val iconTint: Color = Color.White,
    val iconDisableColor: Color = disableColor,
    var txtRes: MutableState<Any?>? = null
)

val ButtonStatusSaver = listSaver(
    save = {
        listOf(it.enabled, it.iconRes, /*it.shape, it.backgroundColor, it.disableColor, it.iconTint, it.iconDisableColor, it.txtRes*/)
    },
    restore = {
        ButtonStatus(
            it[0] as MutableState<Boolean>,
            it[1] as MutableState<Int>,
            /*it[2] as Shape,
            it[3] as Color,
            it[4] as Color,
            it[5] as Color,
            it[6] as Color,
            it[7]*/
        )
    }
)