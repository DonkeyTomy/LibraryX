package com.tomy.lib.ui.preference.widget

import android.content.Context
import com.zzx.utils.rxjava.FlowableUtil
import timber.log.Timber

/**@author Tomy
 * Created by Tomy on 21/7/2020.
 */
abstract class BaseSwitchEnabler(context: Context, protected var mSwitchWidgetController: SwitchWidgetController): BaseEnabler(context, mSwitchWidgetController) {

    init {
        mSwitchWidgetController.setListener(SwitchToggledListener())
    }

    fun setSwitchBarChecked(checked: Boolean) {
        Timber.i("${this.javaClass.simpleName} checked = $checked")
        mStateMachineEvent = true
        mSwitchWidgetController.setChecked(checked)
        mStateMachineEvent = false
    }


    /**
     * Notice: this method Run in BackgroundThread.
     * @param isChecked Switch.isChecked.
     */
    abstract fun switchToggled(isChecked: Boolean)

    inner class SwitchToggledListener: SwitchWidgetController.OnSwitchChangeListener {
        override fun onSwitchToggled(isChecked: Boolean): Boolean {
            Timber.w("${javaClass.name} onSwitchToggled.isChecked[$isChecked] mStateMachineEvent[$mStateMachineEvent]")
            if (mStateMachineEvent) {
                return true
            }
            FlowableUtil.setBackgroundThread {
                switchToggled(isChecked)
            }
            return false
        }

    }

}