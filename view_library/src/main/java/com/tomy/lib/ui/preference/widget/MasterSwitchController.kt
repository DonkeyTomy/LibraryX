package com.tomy.lib.ui.preference.widget

import androidx.appcompat.widget.SwitchCompat
import androidx.preference.Preference
import com.tomy.lib.ui.view.preference.MasterSwitchPreference

/**@author Tomy
 * Created by Tomy on 2018/11/21.
 *
 * The [MasterSwitchController] that is used to update the switch widget in the [MasterSwitchPreference]layout.
 */
class MasterSwitchController(val mPreference: MasterSwitchPreference): SwitchWidgetController(), Preference.OnPreferenceChangeListener {


    override fun isChecked(): Boolean {
        return mPreference.isChecked()
    }

    override fun setChecked(checked: Boolean) {
        mPreference.setChecked(checked)
    }

    override fun getSwitch(): SwitchCompat? {
        return mPreference.getSwitch()
    }

    override fun updateTitle(isChecked: Boolean) {
    }

    override fun startListening() {
        mPreference.onPreferenceChangeListener = this
    }

    override fun stopListening() {
        mPreference.onPreferenceChangeListener = null
    }

    override fun setEnabled(enabled: Boolean) {
        mPreference.setWidgetEnabled(enabled)
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        return mListener?.onSwitchToggled(newValue as Boolean) == true
    }
}