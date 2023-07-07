package com.tomy.component.receiver

import android.content.Context
import android.content.Intent

/**@author Tomy
 * Created by Tomy on 2023/7/7.
 */
class BatteryReceiver(context: Context): BaseReceiver(context) {

    override val mActionList: ArrayList<String> = arrayListOf(
        Intent.ACTION_BATTERY_LOW,
        Intent.ACTION_BATTERY_CHANGED
    )


}