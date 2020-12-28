package com.tomy.lib.ui.preference

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.preference.PreferenceScreen
import androidx.preference.PreferenceViewHolder
import com.tomy.lib.ui.view.preference.MasterSwitchPreference
import com.tomy.lib.ui.preference.widget.BaseSwitchEnabler

/**@author Tomy
 * Created by Tomy on 2018/11/30.
 */
abstract class BaseMasterSwitchPreferenceController(context: Context): BaseMasterPreferenceController(context) {

    protected var mSwitchPreference: MasterSwitchPreference? = null

    protected var mBaseSwitchEnabler: BaseSwitchEnabler? = null

    override fun displayPreference(screen: PreferenceScreen) {
        mSwitchPreference = screen.findPreference(getPreferenceKey())
        mSwitchPreference?.setOnBindViewHolderListener(this)
        super.displayPreference(screen)
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        mBaseSwitchEnabler?.setupController()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    override fun onStart() {
        super.onStart()
        mSwitchPreference?.apply {
            mBaseSwitchEnabler = getBaseEnabler(this) as BaseSwitchEnabler
            mSummaryUpdater = getSummaryUpdater()
        }
    }


}