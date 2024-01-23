package com.tomy.compose.activity

import android.os.Bundle
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Density
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.tomy.component.activity.BaseKeyListenerActivity
import com.tomy.compose.components.custom.CustomBottomBar
import com.tomy.compose.components.custom.CustomTopBar
import com.tomy.compose.components.custom.LocalBackPressedDispatcher
import com.tomy.compose.theme.LocalContainerColor
import com.tomy.compose.theme.MainTheme
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

/**@author Tomy
 * Created by Tomy on 2022/1/20.
 */
abstract class ComposeScaffoldBaseActivity: BaseKeyListenerActivity() {

    protected val mMainViewModel by viewModel<MainViewModel>()

    private val mRightPressedDispatcher: OnBackPressedDispatcher = OnBackPressedDispatcher()
    private val mNavigationPressedDispatcher: OnBackPressedDispatcher = OnBackPressedDispatcher()

    override fun init(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(
            window,
            false
        )
        setContent {
            MainTheme(density = dynamicDensity()) {
                Content()
            }
        }
    }

    open fun dynamicDensity(): Density {
        return Density(1f, 1f)
    }

    @Composable
    fun Content(modifier: Modifier = Modifier) {
        val systemUiController = rememberSystemUiController()
        val useDarIcons = isSystemInDarkTheme()
        val color = MaterialTheme.colorScheme.primary
        SideEffect {
            /*systemUiController.setStatusBarColor(
                color = color,
                darkIcons = useDarIcons
            )*/
            systemUiController.isStatusBarVisible = false
        }
        CompositionLocalProvider(
            LocalBackPressedDispatcher provides onBackPressedDispatcher,
            LocalNavPressedDispatcher provides mNavigationPressedDispatcher,
            LocalRightPressedDispatcher provides mRightPressedDispatcher
        ) {
            val topBarState = mMainViewModel.topBarState

            val topBarVisibility = topBarState.topBarVisibility.collectAsState()
            val bottomBarVisibility = topBarState.bottomBarVisibility.collectAsState()
            val navigationVisible = topBarState.navigationShow.collectAsState()
            val navigationIcon = topBarState.navigationIcon.collectAsState()
            Timber.v("contentColor: ${LocalContentColor.current}")
            Scaffold(
                modifier = modifier
                    .navigationBarsPadding()
                    .systemBarsPadding(),
                contentColor = LocalContentColor.current,
                containerColor = LocalContainerColor.current,
                topBar = {
                    CustomTopBar(
                        isVisible = topBarVisibility.value,
                        titleRes = topBarState.topBarTitle.collectAsState().value,
                        navigationVisible = navigationVisible.value,
                        navigationIcon = navigationIcon.value,
                        navigationOnClick = {
                            mNavigationPressedDispatcher.onBackPressed()
                        },
                        actionIcon = topBarState.rightBtnIcon.collectAsState().value,
                        actionMsg = topBarState.rightBtnTitle.collectAsState().value,
                        actionOnClick = {
                            mRightPressedDispatcher.onBackPressed()
                        }
                    )
                },
                bottomBar = {
                    CustomBottomBar(
                        isVisible = bottomBarVisibility.value,
                        content = createBottomBar()
                    )
                },
                floatingActionButton = {
                    if (topBarState.floatBtnShouldShow.collectAsState().value) {
                        createFloatButton()
                    }
                }
            ) {
                CreateContent(paddingValues = it)
            }
        }
    }

    @Composable
    fun createBottomBar(): @Composable (RowScope.() -> Unit)? = null

    fun createFloatButton(): @Composable (() -> Unit)? = null

    @Composable
    abstract fun CreateContent(paddingValues: PaddingValues)

    override fun onSupportNavigateUp(): Boolean {
        return super.onSupportNavigateUp()
    }

    /**
     * See https://issuetracker.google.com/142847973
     */
    /*fun findNavController(): NavController {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_nav_host) as NavHostFragment
        return navHostFragment.navController
    }*/
}

val LocalNavPressedDispatcher = staticCompositionLocalOf<OnBackPressedDispatcher> {
    error("No Back Dispatcher provided")
}

val LocalRightPressedDispatcher = staticCompositionLocalOf<OnBackPressedDispatcher> {
    error("No Back Dispatcher provided")
}

val LocalBackgroundColor = staticCompositionLocalOf {
    Color.Transparent
}