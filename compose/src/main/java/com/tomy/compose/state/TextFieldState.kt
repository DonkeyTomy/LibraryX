package com.tomy.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.setValue
import timber.log.Timber

/**
 * @see validator 判断该数据是否有效
 * @see errorMsg 根据当前数据返回展示的错误提示信息
 */
open class TextFieldState(
    private val validator: (String) -> Boolean = {
        it.isNotEmpty()
    },
    private val errorMsg: (String) -> String = {""}
) {
    /**
     * 写入的数据
     */
    var text: String by mutableStateOf("")

    /**
     * 是否曾今获取到焦点
     */
    var isFocusedDirty: Boolean by mutableStateOf(false)

    var isFocused: Boolean by mutableStateOf(false)

    /**
     * 是否需要显示错误信息
     */
    private var displayError: Boolean by mutableStateOf(false)

    open val isValid: Boolean
        get() = validator(text)

    fun onFocusChange(focused: Boolean) {
        Timber.v("onFocusChange: $focused")
        isFocused = focused
        if (isFocused) {
            isFocusedDirty = true
        }
    }

    fun enableShowError() {
        //only show errors if the text was at least once focused
        Timber.v("enableShowError: $isFocusedDirty")
        if (isFocusedDirty) {
            displayError = true
        }
    }

    fun canShowError() = !isValid && displayError

    open fun getErrorMsg(): String? {
        return if (canShowError()) {
            errorMsg(text)
        } else {
            null
        }
    }
}

fun textFieldStateSaver(state: TextFieldState) = listSaver<TextFieldState, Any>(
    save = {
        listOf(it.text, it.isFocusedDirty)
    },
    restore = {
        state.apply {
            text = it[0] as String
            isFocusedDirty = it[1] as Boolean
        }
    }
)