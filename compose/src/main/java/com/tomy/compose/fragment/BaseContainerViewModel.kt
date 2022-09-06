package com.tomy.compose.fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**@author Tomy
 * Created by Tomy on 2022/9/1.
 */
class BaseContainerViewModel: ViewModel() {

    private val _topBarShouldShow = MutableStateFlow(false)
    val topBarVisibility: StateFlow<Boolean> = _topBarShouldShow


    fun setTopBarVisible(visible: Boolean) {
        viewModelScope.launch {
            _topBarShouldShow.emit(visible)
        }
    }

    fun toggleTopBar() {
        viewModelScope.launch {
            _topBarShouldShow.emit(_topBarShouldShow.value.not())
        }
    }

    private val _bottomBarShouldShow = MutableStateFlow(false)
    val bottomBarVisibility: StateFlow<Boolean> = _bottomBarShouldShow


    fun setBottomBarVisible(visible: Boolean) {
        viewModelScope.launch {
            _bottomBarShouldShow.emit(visible)
        }
    }

    fun toggleBottomBar() {
        viewModelScope.launch {
            _bottomBarShouldShow.emit(_bottomBarShouldShow.value.not())
        }
    }

}