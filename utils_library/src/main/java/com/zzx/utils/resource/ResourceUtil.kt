package com.zzx.utils.resource

import android.content.Context
import android.util.TypedValue

/**@author Tomy
 * Created by Tomy on 29/11/2020.
 */
object ResourceUtil {

    fun getFloatValueFromDimen(context: Context, dimenKey: Int): Float {
        return TypedValue().let {
            context.resources.getValue(dimenKey, it, true)
            it.float
        }
    }

}