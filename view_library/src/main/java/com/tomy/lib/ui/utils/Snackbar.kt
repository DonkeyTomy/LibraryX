package com.tomy.lib.ui.utils

import android.graphics.Color
import android.view.View
import com.google.android.material.snackbar.Snackbar


/**
 * Created by zdm on 2021/6/8 0008.
 * 功能：
 */

fun View.showSnackBar(text: String, duration: Int = Snackbar.LENGTH_SHORT, actionText: String? = null, block: (() -> Unit)? = null, colorString: String = "#FFFFFF") {
    val snackBar = Snackbar.make(this, text, duration).setTextColor(Color.parseColor(colorString))
    if (actionText != null && block != null) {
        snackBar.setAction(actionText) {
            block()
        }
    }
    snackBar.show()
}

fun View.showSnackBar(textId: Int, duration: Int = Snackbar.LENGTH_SHORT, actionText: String? = null, block: (() -> Unit)? = null, colorString: String = "#FFFFFF") {
    val snackBar = Snackbar.make(this, textId, duration).setTextColor(Color.parseColor(colorString))
    if (actionText != null && block != null) {
        snackBar.setAction(actionText) {
            block()
        }
    }
    snackBar.show()
}
