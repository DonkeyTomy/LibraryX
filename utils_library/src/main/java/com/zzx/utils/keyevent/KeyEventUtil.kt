package com.zzx.utils.keyevent

import android.view.KeyEvent

/**@author Tomy
 * Created by Tomy on 8/1/2021.
 */
object KeyEventUtil {

    /**
     * [KeyEvent.KEYCODE_0] to [KeyEvent.KEYCODE_9] convert to "0~9" and "*" "#"
     * @param keyCode Int
     * @return String
     */
    fun getKeyCodeString(keyCode: Int): String {
        return when (keyCode) {
            KeyEvent.KEYCODE_0  -> "0"
            KeyEvent.KEYCODE_1  -> "1"
            KeyEvent.KEYCODE_2  -> "2"
            KeyEvent.KEYCODE_3  -> "3"
            KeyEvent.KEYCODE_4  -> "4"
            KeyEvent.KEYCODE_5  -> "5"
            KeyEvent.KEYCODE_6  -> "6"
            KeyEvent.KEYCODE_7  -> "7"
            KeyEvent.KEYCODE_8  -> "8"
            KeyEvent.KEYCODE_9  -> "9"
            KeyEvent.KEYCODE_STAR   -> "*"
            KeyEvent.KEYCODE_POUND  -> "#"
            else -> {
                return ""
            }
        }
    }

    /**
     * [KeyEvent.KEYCODE_0] to [KeyEvent.KEYCODE_9]
     * @param keyCode Int
     * @return Boolean
     */
    fun checkKeyCodeNumber(keyCode: Int): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_0,
            KeyEvent.KEYCODE_1,
            KeyEvent.KEYCODE_2,
            KeyEvent.KEYCODE_3,
            KeyEvent.KEYCODE_4,
            KeyEvent.KEYCODE_5,
            KeyEvent.KEYCODE_6,
            KeyEvent.KEYCODE_7,
            KeyEvent.KEYCODE_8,
            KeyEvent.KEYCODE_9 -> {
                return true
            }
            else -> {
                return false
            }
        }
    }

}