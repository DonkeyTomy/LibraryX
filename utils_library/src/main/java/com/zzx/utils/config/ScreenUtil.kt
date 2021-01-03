package com.zzx.utils.config

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.util.Size

/**@author Tomy
 * Created by Tomy on 3/1/2021.
 */
object ScreenUtil {

    fun getScreenSize(context: Context): Size {
        val displayMetrics = context.resources.displayMetrics
        return Size(displayMetrics.widthPixels, displayMetrics.heightPixels)
    }

    fun getDisplaySize(activity: Activity, withoutMainKey: Boolean = false): Size {
        val point = Point()
        if (withoutMainKey) {
            activity.windowManager.defaultDisplay.getSize(point)
        } else {
            activity.windowManager.defaultDisplay.getRealSize(point)
        }
        return Size(point.x, point.y)
    }


}