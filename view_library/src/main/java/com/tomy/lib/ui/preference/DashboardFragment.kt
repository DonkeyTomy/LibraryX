package com.tomy.lib.ui.preference

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.ArrayMap
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.tomy.lib.ui.view.preference.core.AbstractPreferenceController
import timber.log.Timber

/**@author Tomy
 * Created by Tomy on 2018/11/22.
 */
abstract class DashboardFragment: PreferenceFragmentCompat() {

    private val mPreferenceControllers by lazy {
        ArrayMap<Class<Any>, AbstractPreferenceController>()
    }

    var mContext: AppCompatActivity? = null

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        mContext = activity as AppCompatActivity
        val controllers = getPreferenceControllers(activity)
        controllers?.forEach {
            addPreferenceController(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set ComparisonCallback so we get better animation when list changes.
        preferenceManager.preferenceComparisonCallback = PreferenceManager.SimplePreferenceComparisonCallback()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        Timber.v("onCreatePreferences")
        refreshAllPreferences(getLogTag())
    }

    private fun refreshAllPreferences(tag: String) {
        //First remove all old Preferences.
        preferenceScreen?.removeAll()
        //Add resource based tiles.
        displayResourceTiles()
//        refreshDashboardTiles(tag)
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        Timber.v("onPreferenceTreeClick().preference = ${preference.key}")
        Timber.v("onPreferenceTreeClick().fragment = ${preference.intent}")
        return super.onPreferenceTreeClick(preference)
    }

    override fun onDetach() {
        super.onDetach()
        mContext = null
    }

    /**
     * 加载PreferenceScreen,并调用所有[AbstractPreferenceController.displayPreference]来调整Preference.
     */
    private fun displayResourceTiles() {
        val resId = getPreferenceScreenResId()
        if (resId <= 0) {
            return
        }
        addPreferencesFromResource(resId)
        val screen = preferenceScreen
        Timber.e("size = ${mPreferenceControllers.size}")
        mPreferenceControllers.forEach {
            Timber.v("controller = ${it.key.name}")
            it.value.displayPreference(screen)
        }
    }

    protected fun addPreferenceController(controller: AbstractPreferenceController) {
        Timber.v("addPreferenceController: javaClass = ${controller.javaClass}")
        mPreferenceControllers[controller.javaClass] = controller
    }

    /**
     * 获得此Fragment对应的一个[AbstractPreferenceController]队列.
     */
    open fun getPreferenceControllers(context: Context): List<AbstractPreferenceController>? = null

    /**
     * @return Preference Xml id.
     */
    abstract fun getPreferenceScreenResId(): Int

    /**
     * @return The Log Tag.
     */
    open fun getLogTag(): String = ""

}