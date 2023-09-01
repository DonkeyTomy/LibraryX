package com.tomy.compose.components.button

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.IconButton
import androidx.compose.material.TextButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import timber.log.Timber

@Composable
fun CircleIconButton(
    modifier: Modifier = Modifier,
    @DrawableRes
    iconRes: Int,
    onClick: () -> Unit,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
    pressedColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    disBackgroundColor: Color = MaterialTheme.colorScheme.inverseSurface,
    iconColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    disableIconColor: Color = MaterialTheme.colorScheme.onTertiary,
    shape: Shape? = CircleShape,
    shadowElevation: Dp = 0.dp,
    enable: Boolean = true
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }
    val isPressed by interactionSource.collectIsPressedAsState()
    IconButton(
        modifier = Modifier
            .shadow(shadowElevation, shape ?: RectangleShape, clip = false)
            .background(
                color = if (!enable) {
                    disBackgroundColor
                } else if (isPressed) {
                    pressedColor
                } else {
                    backgroundColor
                },
                shape = shape ?: RectangleShape
            )
            .then(if (shape != null) Modifier.clip(shape) else Modifier)
            .then(modifier),
        interactionSource = interactionSource,
        enabled = enable,
        onClick = {
            Timber.d("IconButton.click")
            onClick()
        },
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = "$iconRes",
            tint = if (enable) iconColor else disableIconColor
        )
    }
}

@Composable
fun IconButtonWithTxt(
    modifier: Modifier = Modifier,
    btnModifier: Modifier = Modifier,
    @DrawableRes
    iconRes: Int,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
    pressedColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    disBackgroundColor: Color = MaterialTheme.colorScheme.inverseSurface,
    iconColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    disableIconColor: Color = MaterialTheme.colorScheme.onTertiary,
    enable: Boolean = true,
    txtRes: Any? = null,
    textStyle: TextStyle = LocalTextStyle.current,
    shape: Shape? = CircleShape,
    shadowElevation: Dp = 5.dp,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .wrapContentSize()
            .clickable {
                onClick()
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircleIconButton(
            modifier = btnModifier,
            iconRes = iconRes,
            onClick = onClick,
            backgroundColor = backgroundColor,
            pressedColor = pressedColor,
            disBackgroundColor = disBackgroundColor,
            iconColor = iconColor,
            disableIconColor = disableIconColor,
            shape = shape,
            shadowElevation = shadowElevation,
            enable = enable
        )
        Spacer(modifier = Modifier.height(10.dp))
        txtRes?.let {
            Text(
                text = if (txtRes is Int) stringResource(id = txtRes) else if (txtRes is String) txtRes else "",
                maxLines = 1,
                style = textStyle
            )
        }
    }
}

@Composable
fun MainButton(
    @StringRes
    msgId: Int,
    shape: Shape = MaterialTheme.shapes.large,
    enable: Boolean = true,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        enabled = enable,
        shape = shape
    ) {
        Text(text = stringResource(id = msgId))
    }
}