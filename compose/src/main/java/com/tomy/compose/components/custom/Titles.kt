package com.tomy.compose.components.custom

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tomy.compose.theme.LocalTitleTextStyle
import com.tomy.compose.theme.allWrap
import com.tomy.compose.theme.wrapHeight

@Composable
fun TitleWithBg(
    @SuppressLint("ModifierParameter")
    titleModifier: Modifier = Modifier,
    @StringRes
    titleId: Int,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Center,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    @StringRes
    rightTitleId: Int? = null,
    rightTitleModifier: Modifier = Modifier,
    titleTextStyle: TextStyle = LocalTitleTextStyle.current,
    background: Color = MaterialTheme.colorScheme.primary,
    padding: Dp = 5.dp
) {
    RowWrapHeightWithBg(
        background = background,
        padding = padding,
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment
    ) {
        Text(
            modifier = (if (rightTitleId != null) Modifier.weight(1f) else Modifier).then(titleModifier),
            text = stringResource(id = titleId),
            style = titleTextStyle
        )
        rightTitleId?.let {
            Text(
                modifier = Modifier
                    .allWrap()
                    .then(rightTitleModifier),
                text = stringResource(id = it),
                style = titleTextStyle
            )
        }
    }
}

@Composable
fun RowWrapHeightWithBg(
    background: Color = MaterialTheme.colorScheme.primary,
    padding: Dp = 5.dp,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = Modifier
            .background(background)
            .padding(padding)
            .wrapHeight(),
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment,
        content = content
    )
}