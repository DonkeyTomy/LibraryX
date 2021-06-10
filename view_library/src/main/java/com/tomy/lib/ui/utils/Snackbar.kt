package com.tomy.lib.ui.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.tomy.lib.ui.R


/**
 * Created by zdm on 2021/6/8 0008.
 * 功能：
 */

fun View.showSnackBar(text: String, duration: Int = Snackbar.LENGTH_SHORT,
                      actionText: String? = null, block: (() -> Unit)? = null,
                      colorId: Int = R.color.white) {
    val snackBar = Snackbar.make(this, text, duration).setTextColor(resources.getColor(colorId))
    if (actionText != null && block != null) {
        snackBar.setAction(actionText) {
            block()
        }
    }
    snackBar.show()
}

fun View.showSnackBar(textId: Int, duration: Int = Snackbar.LENGTH_SHORT,
                      actionText: String? = null, block: (() -> Unit)? = null, colorId: Int = R.color.white) {
    val snackBar = Snackbar.make(this, textId, duration).setTextColor(resources.getColor(colorId))
    if (actionText != null && block != null) {
        snackBar.setAction(actionText) {
            block()
        }
    }
    snackBar.show()
}
