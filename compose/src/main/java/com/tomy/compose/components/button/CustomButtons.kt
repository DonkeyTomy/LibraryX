package com.tomy.compose.components.button

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp

@Composable
fun CircleIconButton(
    modifier: Modifier = Modifier,
    @DrawableRes
    iconRes: Int,
    onClick: () -> Unit,
    enable: Boolean = true
) {
    Button(
        modifier = modifier,
        shape = RoundedCornerShape(100),
        enabled = enable,
        onClick = onClick,
    ) {
        Image(
            painter = painterResource(id = iconRes), contentDescription = "$iconRes"
        )
        Text(text = "123")
    }
}