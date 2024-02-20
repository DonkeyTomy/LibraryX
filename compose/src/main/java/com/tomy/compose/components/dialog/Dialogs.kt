package com.tomy.compose.components.dialog

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.tomy.compose.theme.dynamicDensity
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotifyDialog(
    modifier: Modifier = Modifier,
    properties: DialogProperties = DialogProperties(),
    show: MutableState<Boolean>
) {
    if (show.value) {
        BasicAlertDialog(
            modifier = Modifier.background(
                Color.Green
            ),
            onDismissRequest = {
                Timber.d("onDismiss")
            },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            )
        ) {
            Button(onClick = { show.value = false }) {
                Text(text = "Message")
            }
        }
    }
}

@Composable
fun ContainerDialog(
    modifier: Modifier = Modifier,
    show: MutableState<Boolean> = remember {
        mutableStateOf(true)
    },
    topContent: @Composable (ColumnScope.() -> Unit)? = null,
    middleContent: @Composable (ColumnScope.() -> Unit)? = null,
    properties: DialogProperties = DialogProperties(),
    bottomContent: @Composable (ColumnScope.() -> Unit)? = null
) {
    if (show.value) {
        Dialog(
            onDismissRequest = {
                show.value = false
            },
            properties = properties
        ) {
            Column(
                modifier = modifier,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                topContent?.invoke(this)
                middleContent?.invoke(this)
                bottomContent?.invoke(this)
            }
        }
    }
}

@Composable
fun ContainerDialog(
    navController: NavController,
    modifier: Modifier = Modifier,
    topContent: @Composable (ColumnScope.() -> Unit)? = null,
    middleContent: @Composable (ColumnScope.() -> Unit)? = null,
    properties: DialogProperties = DialogProperties(),
    bottomContent: @Composable (ColumnScope.() -> Unit)? = null
) {
    Dialog(
        onDismissRequest = {
            navController.popBackStack()
        },
        properties = properties
    ) {
        CompositionLocalProvider(
            LocalDensity provides dynamicDensity()
        ) {
            Column(
                modifier = modifier,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                topContent?.invoke(this)
                middleContent?.invoke(this)
                bottomContent?.invoke(this)
            }
        }
    }
}

@Composable
fun TitleMsgDialog(
    navController: NavController,
    @StringRes
    titleId: Int? = null,
    @StringRes
    msgId: Int,
    @StringRes
    confirmBtnId: Int? = null,
    @StringRes
    cancelBtnId: Int? = null,
    properties: DialogProperties = DialogProperties()
) {

    ContainerDialog(
        modifier = Modifier
            .fillMaxWidth(0.98f)
            .aspectRatio(1.8f)
            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(20.dp)),
        navController = navController,
        topContent = {
            if (titleId != null && titleId != -1) {
                DialogTitle(modifier = Modifier.weight(1f), titleId = titleId)
            }
        },
        middleContent = {
            if (msgId != -1) {
                DialogMsg(modifier = Modifier.weight(1f), msgId = msgId)
            }
        },
        properties = properties
    ) {
        if ((confirmBtnId != null && confirmBtnId != -1) || (cancelBtnId != null && cancelBtnId != -1)) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (confirmBtnId != null && confirmBtnId != -1) {
                    DialogBtn(
                        modifier = Modifier
                            .weight(1f),
                        titleId = confirmBtnId
                    ) {
                        navController.popBackStack()
                    }
                }

                if (cancelBtnId != null && cancelBtnId != -1) {
                    DialogBtn(
                        modifier = Modifier
                            .weight(1f),
                        titleId = cancelBtnId
                    ) {
                        navController.popBackStack()
                    }
                }
            }
        }

    }
}

@Composable
fun TitleMsgDialog(
    showDialog: MutableState<Boolean> = mutableStateOf(true),
    @StringRes
    titleId: Int? = null,
    @StringRes
    msgId: Int,
    @StringRes
    confirmBtnId: Int,
    @StringRes
    cancelBtnId: Int? = null,
    properties: DialogProperties = DialogProperties()
) {

    ContainerDialog(
        modifier = Modifier
            .fillMaxWidth(1f)
            .fillMaxHeight(0.2f)
            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(10.dp)),
        show = showDialog,
        topContent = {
            if (titleId != null && titleId != -1) {
                DialogTitle(modifier = Modifier.weight(1f), titleId = titleId)
            }
        },
        middleContent = {
            DialogMsg(modifier = Modifier.weight(1f), msgId = msgId)
        },
        properties = properties
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DialogBtn(
                modifier = Modifier.weight(1f),
                titleId = confirmBtnId
            ) {
                showDialog.value = false
            }
            cancelBtnId?.let {
                DialogBtn(
                    modifier = Modifier.weight(1f),
                    titleId = cancelBtnId
                ) {
                    showDialog.value = false
                }
            }
        }
    }
}

@Composable
fun DialogTitle(
    modifier: Modifier = Modifier,
    @StringRes
    titleId: Int
) {
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            textAlign = TextAlign.Center,
            text = stringResource(id = titleId),
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun DialogMsg(
    modifier: Modifier = Modifier,
    @StringRes
    msgId: Int
) {
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            textAlign = TextAlign.Center,
            text = stringResource(id = msgId),
            style = MaterialTheme.typography.bodySmall,
        )
    }

}

@Composable
fun DialogBtn(
    modifier: Modifier = Modifier,
    @StringRes
    titleId: Int,
    enable: Boolean = true,
    textColor: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit = {}
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        )
    ) {
        Text(
            text = stringResource(id = titleId),
            style = MaterialTheme.typography.bodySmall.copy(color = if (enable) textColor else MaterialTheme.colorScheme.inverseSurface),
        )
    }
}