package com.tomy.compose.components.button

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
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
import com.tomy.compose.components.dialog.DialogBtn

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
    enabled: Boolean = true
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }
    val isPressed by interactionSource.collectIsPressedAsState()
    IconButton(
        modifier = Modifier
            .shadow(shadowElevation, shape ?: RectangleShape, clip = false)
            .then(if (shape != null) Modifier.clip(shape) else Modifier)
            .background(
                color = if (!enabled) {
                    disBackgroundColor
                } else if (isPressed) {
                    pressedColor
                } else {
                    backgroundColor
                },
                shape = shape ?: RectangleShape
            )
            .then(modifier),
        interactionSource = interactionSource,
        enabled = enabled,
        onClick = {
            onClick()
        },
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = "$iconRes",
            tint = if (enabled) iconColor else disableIconColor
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
    enabled: Boolean = true,
    txtRes: Any? = null,
    textStyle: TextStyle = LocalTextStyle.current,
    shape: Shape? = CircleShape,
    shadowElevation: Dp = 2.dp,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .wrapContentSize()
            .clickable(enabled = enabled) {
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
            enabled = enabled
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

@Composable
fun ConfirmButtons(
    modifier: Modifier = Modifier,
    shape: Shape = ButtonDefaults.shape,
    btnPaddingValues: PaddingValues = PaddingValues(0.dp),

    @StringRes
    confirmBtnId: Int? = null,
    confirmColors: ButtonColors = ButtonDefaults.buttonColors(),
    confirmBorder: BorderStroke? = null,
    onConfirmClick: () -> Unit = {},

    @StringRes
    cancelBtnId: Int? = null,
    cancelColors: ButtonColors = confirmColors,
    cancelBorder: BorderStroke? = confirmBorder,
    onCancelClick: () -> Unit = {}
) {
    if ((confirmBtnId != null && confirmBtnId != -1) || (cancelBtnId != null && cancelBtnId != -1)) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (cancelBtnId != null && cancelBtnId != -1) {
                DialogBtn(
                    modifier = Modifier
                        .weight(1f).padding(btnPaddingValues),
                    shape = shape,
                    titleId = cancelBtnId,
                    colors = cancelColors,
                    border = cancelBorder,
                    onClick = onCancelClick
                )
            }

            if (confirmBtnId != null && confirmBtnId != -1) {
                DialogBtn(
                    modifier = Modifier
                        .weight(1f).padding(btnPaddingValues),
                    shape = shape,
                    titleId = confirmBtnId,
                    colors = confirmColors,
                    border = confirmBorder,
                    onClick =  onConfirmClick
                )
            }
        }
    }

}