package com.tomy.compose.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.ViewWindowInsetObserver

abstract class BaseComposeFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(inflater.context).apply {
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        // Create a ViewWindowInsetObserver using this view, and call start() to
        // start listening now. The WindowInsets instance is returned, allowing us to
        // provide it to AmbientWindowInsets in our content below.
        val windowInsets = ViewWindowInsetObserver(this)
            // We use the `windowInsetsAnimationsEnabled` parameter to enable animated
            // insets support. This allows our `ConversationContent` to animate with the
            // on-screen keyboard (IME) as it enters/exits the screen.
            .start(windowInsetsAnimationsEnabled = true)

        setContent {
            CompositionLocalProvider(
                LocalWindowInsets provides windowInsets
            ) {
                CreateContent()
            }
        }
    }

    @Composable
    fun CreateContent1() {
        Surface(
            color = Color.Blue,
        ) {
            Column(
                modifier = Modifier.background(Color.Red)
            ) {

            }

        }
    }

    @Composable
    abstract fun CreateContent()
}