package com.tomy.compose.activity

import androidx.lifecycle.ViewModel
import com.tomy.compose.components.state.TopBarState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**@author Tomy
 * Created by Tomy on 2022/1/20.
 */
class MainViewModel: ViewModel() {

    /**
     * [ScaffoldState.drawerState]
     */
    private val _drawerShouldBeOpened = MutableStateFlow(false)
    val drawerShouldBeOpened: StateFlow<Boolean> = _drawerShouldBeOpened

    fun openDrawer() {
        _drawerShouldBeOpened.value = true
    }
    fun resetOpenDrawerAction() {
        _drawerShouldBeOpened.value = false
    }


    val topBarState = TopBarState()


    private val _topTabShouldShow = MutableStateFlow(true)
    val topTabShouldShow: StateFlow<Boolean> = _topTabShouldShow

    fun showTopTab() {
        _topTabShouldShow.value = true
    }

    fun hideTopTab() {
        _topTabShouldShow.value = false
    }

    fun toggleTopTabVisible() {
        _topTabShouldShow.value = _topTabShouldShow.value.not()
    }

    /**
     *
     */
    private val _tabSelectedPosition = MutableStateFlow(0)
    val tabSelectedPosition: StateFlow<Int> = _tabSelectedPosition

    fun selectTab(position: Int) {
        _tabSelectedPosition.value = position
    }


}