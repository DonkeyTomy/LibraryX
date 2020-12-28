package com.tomy.lib.ui.preference

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.preference.PreferenceScreen
import androidx.preference.PreferenceViewHolder
import com.tomy.lib.ui.view.preference.TwoTargetPreference
import com.tomy.lib.ui.view.preference.core.AbstractPreferenceController
import com.tomy.lib.ui.preference.widget.BaseEnabler
import com.tomy.lib.ui.preference.widget.SummaryUpdater

/**@author Tomy
 * Created by Tomy on 21/7/2020.
 */
abstract class BaseMasterPreferenceController(context: Context): AbstractPreferenceController(context), LifecycleObserver,
        TwoTargetPreference.OnBindViewHolderListener,
        SummaryUpdater.OnSummaryChangeListener {

    protected var mPreference: TwoTargetPreference? = null

    protected var mBaseEnabler: BaseEnabler? = null

    protected var mSummaryUpdater: SummaryUpdater? = null

    override fun displayPreference(screen: PreferenceScreen) {
        mPreference = screen.findPreference(getPreferenceKey()) as TwoTargetPreference?
        mPreference?.setOnBindViewHolderListener(this)
        super.displayPreference(screen)
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        mBaseEnabler?.setupController()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    override fun onStart() {
        super.onStart()
        mPreference?.apply {
            mBaseEnabler = getBaseEnabler(this)
            mSummaryUpdater = getSummaryUpdater()
        }
    }

    abstract fun getBaseEnabler(mastSwitchPreference: TwoTargetPreference): BaseEnabler?

    open fun getSummaryUpdater(): SummaryUpdater? {
        return null
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    override fun onPause() {
        super.onPause()
        mBaseEnabler?.pause()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    override fun onResume() {
        super.onResume()
        mSummaryUpdater?.register(true)
        mBaseEnabler?.resume()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    override fun onStop() {
        super.onStop()
        mSummaryUpdater?.register(false)
        mBaseEnabler?.tearDownController()
        mBaseEnabler    = null
        mSummaryUpdater = null
    }

    override fun onSummaryChanged(summary: String?) {
    }

    override fun isAvailable(): Boolean {
        return true
    }

    override fun getPreferenceKey(): String {
        return mContext.getString(getPreferenceKeyId())
    }

    abstract fun getPreferenceKeyId(): Int

}