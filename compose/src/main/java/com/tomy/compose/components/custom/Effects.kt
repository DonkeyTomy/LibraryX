package com.tomy.compose.components.custom

import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner

/**
 * @see onBackPressedDispatcher 可直接指定[OnBackPressedDispatcher]或者通过[LocalBackPressedDispatcher].provide指定.
 */
@Composable
fun BackPressedHandler(
    onBack: () -> Unit,
    onBackPressedDispatcher: OnBackPressedDispatcher? = null,
    enabled: Boolean = true
) {
    val currentOnBackPressed by rememberUpdatedState(newValue = onBack)

    val onBackPressedCallback = remember {
        object: OnBackPressedCallback(enabled) {
            override fun handleOnBackPressed() {
                currentOnBackPressed()
            }
        }
    }

    SideEffect {
        onBackPressedCallback.isEnabled = enabled
    }

    val backPressedDispatcher = onBackPressedDispatcher ?: LocalBackPressedDispatcher.current

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner, onBackPressedDispatcher) {
        backPressedDispatcher.addCallback(onBackPressedCallback)
        onDispose {
            onBackPressedCallback.remove()
        }
    }
}

/**
 * This [CompositionLocal] is used to provide an [OnBackPressedDispatcher]:
 *
 * ```
 * CompositionLocalProvider(
 *     LocalBackPressedDispatcher provides requireActivity().onBackPressedDispatcher
 * ) { }
 * ```
 *
 * and setting up the callbacks with [BackPressHandler].
 */
val LocalBackPressedDispatcher = staticCompositionLocalOf<OnBackPressedDispatcher> {
    error("No Back Dispatcher provided")
}
