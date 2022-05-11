package com.tomy.compose.components.custom

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tomy.compose.R

/**@author Tomy
 * Created by Tomy on 2022/1/21.
 */
@Composable
fun IconOrText(
    modifier: Modifier = Modifier,
    @StringRes msg: Int? = null,
    style: TextStyle = MaterialTheme.typography.titleSmall,
    @DrawableRes icon: Int? = null,
    tint: Color = LocalContentColor.current,
    onClick: () -> Unit = {}
) {
    if (msg != null || icon != null) {
        Box(
            modifier = modifier.clickable {
                onClick()
            }
        ) {
            if (msg != null) {
                Text(
                    text = stringResource(id = msg),
                    style = style
                )
            } else if (icon != null) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = "",
                    tint = tint
                )
            }
        }

    }
}

@Composable
fun IconOutlineTextField(
    modifier: Modifier = Modifier,
    autoFocus: Boolean = false,
    value: String,
    onValueChange: (String) -> Unit,
    @StringRes labelRes: Int? = null,
    labelColor: Color = Color.Black,
    @DrawableRes leadingIcon: Int? = null,
    leadIconClick: (() -> Unit)? = null,
    @DrawableRes trailingIcon: Int? = null,
    trailIconClick: (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    val focusRequester = FocusRequester()
    if (autoFocus) {
        LaunchedEffect(key1 = Unit) {
            focusRequester.requestFocus()
        }
    }

    OutlinedTextField(
        modifier = if (autoFocus) modifier.focusRequester(focusRequester) else modifier,
        value = value,
        onValueChange = onValueChange,
        label = {
            labelRes?.let {
                Text(
                    text = stringResource(id = it),
                    color = labelColor,
                    style = MaterialTheme.typography.bodySmall
                )
            }

        },
        visualTransformation = visualTransformation,
        leadingIcon = {
            leadingIcon?.let {
                Icon(
                    modifier = leadIconClick?.let { click ->
                        Modifier.clickable { click() }
                    } ?: Modifier,
                    painter = painterResource(id = it),
                    contentDescription = null
                )
            }
        },
        trailingIcon = {
            trailingIcon?.let {
                Icon(
                    modifier = trailIconClick?.let { click ->
                        Modifier.clickable { click() }
                    } ?: Modifier,
                    painter = painterResource(id = it),
                    contentDescription = null
                )
            }
        },
        singleLine = singleLine,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions
    )
}

enum class ButtonState {
    Normal, Pressed
}

@Composable
fun AnimatedButton(
    modifier: Modifier = Modifier,
    @StringRes title: Int,
    enabled: Boolean = true,
    btnState: ButtonState,
    width: Dp,
    onClick: () -> Unit = {},
) {
    val duration = 600
    val transition = updateTransition(targetState = btnState, label = "ButtonState")

    val btnBackgroundColor: Color by transition.animateColor(
        transitionSpec = {
            tween(duration)
        },
        label = "Button Background Color"
    ) { buttonState ->
        when (buttonState) {
            ButtonState.Normal  -> MaterialTheme.colorScheme.secondary
            ButtonState.Pressed -> MaterialTheme.colorScheme.primary
        }
    }

    val btnWidth: Dp by transition.animateDp(
        transitionSpec = { tween(duration)},
        label = "Btn Width"
    ) {
        when (it) {
            ButtonState.Normal  -> width
            ButtonState.Pressed -> dimensionResource(id = R.dimen.animation_btn_width)
        }
    }

    val btnShape: Dp by transition.animateDp(
        transitionSpec = { tween(duration)},
        label = "Btn Shape"
    ) {
        when (it) {
            ButtonState.Normal  -> 5.dp
            ButtonState.Pressed -> 100.dp
        }
    }

    Button(
        modifier = modifier.width(btnWidth),
        shape = RoundedCornerShape(btnShape),
        colors = ButtonDefaults.buttonColors(
            containerColor = btnBackgroundColor,
            disabledContainerColor = MaterialTheme.colorScheme.tertiary,
        ),
        enabled = enabled,
        onClick = {
            onClick()
        }
    ) {
        if (btnState == ButtonState.Normal) {
            Text(
                text = stringResource(id = title),
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 2.dp,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .size(24.dp)
            )
        }

    }
}