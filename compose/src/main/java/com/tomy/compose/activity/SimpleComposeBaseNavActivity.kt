package com.tomy.compose.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.tomy.compose.components.custom.LocalBackPressedDispatcher
import com.tomy.compose.databinding.MainActivityComposeBinding
import com.tomy.compose.theme.MainTheme

/**@author Tomy
 * Created by Tomy on 2022/6/15.
 */
abstract class SimpleComposeBaseNavActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(
            window,
            false
        )
        setContent {
            val systemUiController = rememberSystemUiController()
            val useDarIcons = MaterialTheme.colors.isLight
            SideEffect {
                systemUiController.setStatusBarColor(
                    color = Color.Black,
                    darkIcons = useDarIcons
                )
            }
            CompositionLocalProvider(
                LocalBackPressedDispatcher provides this.onBackPressedDispatcher,
            ) {
                MainTheme {
                    CreateContent()
                }
            }
        }
    }

    @Composable
    fun CreateContent() {
        AndroidViewBinding(
            MainActivityComposeBinding::inflate,
            modifier = Modifier
                .statusBarsPadding()
                .navigationBarsPadding(),
            update = {
                mainNavHost.findNavController().setGraph(getNavResId())
            }
        )
    }

    abstract fun getNavResId(): Int
}