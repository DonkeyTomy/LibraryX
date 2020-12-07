package com.tomy.lib.ui.receiver

import android.content.Context
import android.content.Intent

/**@author Tomy
 * Created by Tomy on 7/12/2020.
 */
abstract class BaseTimeReceiver(context: Context): BaseReceiver(context) {

    override val mActionList = arrayListOf(Intent.ACTION_TIME_CHANGED, Intent.ACTION_DATE_CHANGED, Intent.ACTION_TIME_TICK)

}