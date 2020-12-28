package com.tomy.lib.ui.preference.widget

import androidx.preference.Preference
import com.tomy.lib.ui.view.preference.TwoTargetPreference

/**@author Tomy
 * Created by Tomy on 21/7/2020.
 */
open class MasterController(var mPreference: TwoTargetPreference): WidgetController, Preference.OnPreferenceChangeListener {

    override fun setupView() {
    }

    override fun teardownView() {
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
        return false
    }
}