package com.tomy.compose.components.state

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.tomy.compose.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Stable
class TopBarState {

    private val _topBarShouldShow = MutableStateFlow(false)
    val topBarVisibility: StateFlow<Boolean> = _topBarShouldShow


    fun setTopBarVisible(visible: Boolean) {
        _topBarShouldShow.value = visible
    }

    fun toggleTopBar() {
        _topBarShouldShow.value = _topBarShouldShow.value.not()
    }

    private val _topBarTitle = MutableStateFlow(R.string.home)
    val topBarTitle: StateFlow<Int> = _topBarTitle

    fun setTitle(@StringRes titleRes: Int) {
        _topBarTitle.value = titleRes
    }


    private val _bottomBarShouldShow = MutableStateFlow(false)
    val bottomBarVisibility: StateFlow<Boolean> = _bottomBarShouldShow


    fun setBottomBarVisible(visible: Boolean) {
        _bottomBarShouldShow.value = visible
    }

    fun toggleBottomBar() {
        _bottomBarShouldShow.value = _bottomBarShouldShow.value.not()
    }

    private val _floatBtnShouldShow = MutableStateFlow(false)
    val floatBtnShouldShow: StateFlow<Boolean> = _floatBtnShouldShow


    fun setFloatBtnVisible(visible: Boolean) {
        _floatBtnShouldShow.value = visible
    }

    fun toggleFloatBtn() {
        _floatBtnShouldShow.value = _floatBtnShouldShow.value.not()
    }


    private val _navigationIcon = MutableStateFlow(R.drawable.ic_settings)
    val navigationIcon: StateFlow<Int> = _navigationIcon

    fun setNavigationIcon(@DrawableRes iconRes: Int) {
        _navigationIcon.value = iconRes
    }

    private val _navigationShow = MutableStateFlow(false)
    val navigationShow = _navigationShow

    fun setNavigationVisibility(visible: Boolean) {
        _navigationShow.value = visible
    }



    private val _rightBtnTitle = MutableStateFlow<Int?>(null)
    val rightBtnTitle: StateFlow<Int?> = _rightBtnTitle

    fun setRightTitle(@StringRes titleRes: Int?) {
        titleRes?.let { setRightIcon(null) }
        _rightBtnTitle.value = titleRes
    }

    private val _rightBtnIcon = MutableStateFlow<Int?>(null)
    val rightBtnIcon: StateFlow<Int?> = _rightBtnIcon

    fun setRightIcon(@DrawableRes titleRes: Int?) {
        titleRes?.let { setRightTitle(null) }
        _rightBtnIcon.value = titleRes
    }

    fun hideRightBtn() {
        _rightBtnTitle.value    = null
        _rightBtnIcon.value     = null
    }

    private val _rightBtnClick = mutableStateOf({})

    val rightBtnClick: State<() -> Unit> = _rightBtnClick

    fun setRightBtnClick(click: () -> Unit) {
        _rightBtnClick.value = click
    }

}