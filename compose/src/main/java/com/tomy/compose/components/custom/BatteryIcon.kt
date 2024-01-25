package com.tomy.compose.components.custom

import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.tomy.compose.components.DrawDirection
import com.tomy.compose.components.TextWithImage
import timber.log.Timber

/**@author Tomy
 * Created by Tomy on 2024/1/25.
 */
@Composable
fun BatteryIcon(
    modifier: Modifier = Modifier,
    capacity: Int,
    charging: Boolean,
    @DrawDirection
    textDirection: Int = -1,
    textStyle: TextStyle = LocalTextStyle.current,
    batteryIconList: List<Int>? = null,
    chargeBatteryIconList: List<Int>? = null
) {

    val index = if (capacity < 3) {
        0
    } else if (capacity < 10) {
        1
    } else if (capacity > 95) {
        10
    } else {
        capacity / 10
    }

    Timber.v("level: $index")

    TextWithImage(
        modifier = modifier,
        slideDirection = textDirection,
        text = "$capacity%",
        textStyle = textStyle,
        margin = 0.dp,
        imageRes = if (charging && chargeBatteryIconList != null) {
            chargeBatteryIconList[index]
        } else {
            batteryIconList?.get(index)
        }
    )

}