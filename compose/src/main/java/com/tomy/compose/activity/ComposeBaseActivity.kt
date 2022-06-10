package com.tomy.compose.activity

import android.os.Bundle
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.core.view.WindowCompat
import com.google.accompanist.insets.ProvideWindowInsets
import com.tomy.compose.components.custom.CustomTopBar
import com.tomy.compose.components.custom.LocalBackPressedDispatcher
import com.tomy.compose.theme.MainTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

/**@author Tomy
 * Created by Tomy on 2022/1/20.
 */
abstract class ComposeBaseActivity: AppCompatActivity() {

    private val mMainViewModel by viewModel<MainViewModel>()

    private val mRightPressedDispatcher: OnBackPressedDispatcher = OnBackPressedDispatcher()
    private val mNavigationPressedDispatcher: OnBackPressedDispatcher = OnBackPressedDispatcher()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(
            window,
            false
        )
        setContent {
            ProvideWindowInsets(false) {
                CompositionLocalProvider(
                    LocalBackPressedDispatcher provides this.onBackPressedDispatcher,
                    LocalNavPressedDispatcher provides mNavigationPressedDispatcher,
                    LocalRightPressedDispatcher provides mRightPressedDispatcher
                ) {
                    val scaffoldState = rememberScaffoldState()
                    val topBarState = mMainViewModel.topBarState

                    val topBarVisibility = topBarState.topBarVisibility.collectAsState()
                    val navigationVisible = topBarState.navigationShow.collectAsState()
                    val navigationIcon = topBarState.navigationIcon.collectAsState()
                    MainTheme {
                        Scaffold(
                            scaffoldState = scaffoldState,
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
                            }
                        ) {
                            CreateContent(paddingValues = it)
                        }
                    }
                }
            }
        }
    }

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