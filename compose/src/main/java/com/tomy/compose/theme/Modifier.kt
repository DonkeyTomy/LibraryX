package com.tomy.compose.theme

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**@author Tomy
 * Created by Tomy on 2021/12/4.
 */

@Stable
fun Modifier.allWrap(align: Alignment = Alignment.Center,
    unbounded: Boolean = false) =
    this.wrapContentSize(align, unbounded)

@Stable
fun Modifier.allMatch(fraction: Float = 1f) = this.fillMaxSize(fraction)

@Stable
fun Modifier.wrapWidth() =
    this.wrapContentWidth()
        .fillMaxHeight()

@Stable
fun Modifier.wrapHeight() =
    this.wrapContentHeight()
        .fillMaxWidth()