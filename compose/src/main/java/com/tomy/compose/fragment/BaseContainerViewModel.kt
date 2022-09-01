package com.tomy.compose.fragment

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**@author Tomy
 * Created by Tomy on 2022/9/1.
 */
class BaseContainerViewModel: ViewModel() {

    private val _topBarShouldShow = MutableStateFlow(false)
    val topBarVisibility: StateFlow<Boolean> = _topBarShouldShow


    fun setTopBarVisible(visible: Boolean) {
        _topBarShouldShow.value = visible
    }

    fun toggleTopBar() {
        _topBarShouldShow.value = _topBarShouldShow.value.not()
    }

    private val _bottomBarShouldShow = MutableStateFlow(false)
    val bottomBarVisibility: StateFlow<Boolean> = _bottomBarShouldShow


    fun setBottomBarVisible(visible: Boolean) {
        _bottomBarShouldShow.value = visible
    }

    fun toggleBottomBar() {
        _bottomBarShouldShow.value = bottomBarVisibility.value.not()
    }

}