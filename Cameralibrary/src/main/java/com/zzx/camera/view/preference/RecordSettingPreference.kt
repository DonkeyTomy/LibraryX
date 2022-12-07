package com.zzx.camera.view.preference

import android.content.Context
import android.content.Intent
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.tomy.lib.ui.view.preference.CustomListPreference
import com.zzx.camera.R
import com.zzx.camera.adapter.VideoSettingAdapter
import com.zzx.camera.data.HCameraSettings
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**@author Tomy
 * Created by Tomy on 2018/10/12.
 */
class RecordSettingPreference(context: Context, attr: AttributeSet): CustomListPreference(context, attr) {

    private lateinit var mDefault: String

    private var mUnBinder: Unbinder? = null

    private var mIndex = 0

    private val mCameraSetting = HCameraSettings(context)

    private val mListAdapter by lazy {
        VideoSettingAdapter(context).apply {
            addData(entries.toList())
        }
    }

    /*private val mReceiver by lazy {
        PreferenceChangeReceiver()
    }*/

    override fun onGetDefaultValue(a: TypedArray, index: Int): String {
        return a.getString(index)?.apply {
            mDefault = this
//            Timber.e("onGetDefaultValue = $this. value = $value")
        } ?: ""
    }

    override fun initData() {
        value = getPersistedString(mDefault)
//        Timber.e("${HCameraSettings.TAG_C_S} value = $value, key = $key")
        mIndex = findIndexOfValue(value)
        Timber.e("initData: mIndex = $mIndex")
        setSelect(mIndex)
        /*FlowableUtil.setBackgroundThread(
                Consumer {
                    when (key) {
                        getString(R.string.key_record_pre)  -> {
                            context.registerReceiver(mReceiver, IntentFilter(ACTION_RECORD_DEL_NOTIFY))
                        }
                        getString(R.string.key_record_delay)-> {
                            context.registerReceiver(mReceiver, IntentFilter(ACTION_RECORD_PRE_NOTIFY))
                        }
                    }
                }
        )*/
    }

    override fun onCreateView(parent: ViewGroup?): View {
        val view = super.onCreateView(parent)
        if (mUnBinder == null) {
            mUnBinder = ButterKnife.bind(this, view)
            initData()
        }
        return view
    }


    fun setSelect(position: Int) {
        if (position >= 0) {
            mIndex = position
            value = entryValues[position].toString()
            summary = entries[position]
        }
    }

    override fun bindListView(listView: ListView) {
        listView.apply {
            setSelector(R.drawable.bg_setting_item)
            adapter = mListAdapter
            mListAdapter.setCheck(findIndexOfValue(value))
            setOnItemClickListener { _, _, position, _ ->
                Timber.e("mIndex = $mIndex; position = $position")
                if (mIndex == position) {
                    mDialog?.dismiss()
                    return@setOnItemClickListener
                }
                mIndex = position
                setSelect(position)
                mListAdapter.setCheck(position)
                when (key) {
                    getString(R.string.key_record_pre)  -> {
                        sendBroadcast(ACTION_RECORD_PRE_NOTIFY)
                    }
                    getString(R.string.key_record_delay)-> {
                        sendBroadcast(ACTION_RECORD_DEL_NOTIFY)
                    }
                }
                Observable.just(Unit)
                        .observeOn(AndroidSchedulers.mainThread())
                        .delay(100, TimeUnit.MILLISECONDS)
                        .subscribe {
                            mDialog?.dismiss()
                        }
            }
        }
    }

    override fun onPrepareForRemoval() {
        Timber.e("onPrepareForRemoval()")
        /*if (key == getString(R.string.key_record_pre) || key == getString(R.string.key_record_delay)) {
            context.unregisterReceiver(mReceiver)
        }*/
        super.onPrepareForRemoval()
    }

        /*when (key) {
            getString(R.string.key_record_auto)     -> {

            }
            getString(R.string.key_record_section)  -> {

            }
            getString(R.string.key_record_pre)      -> {

            }
            getString(R.string.key_record_delay)    -> {

            }

            getString(R.string.key_mode_continuous) -> {

            }
            getString(R.string.key_mode_interval)   -> {

            }
            getString(R.string.key_mode_timer)      -> {

            }
        }*/

    /**
     * 当预录延录更改时需要通知摄像头,若正在录像,需要重启录像.
     */
    private fun sendBroadcast(action: String) {
        val intent = Intent(action)
        context.sendBroadcast(intent)
    }

    /*inner class PreferenceChangeReceiver: BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            Timber.e("[${intent.action}; key = $key]")
            setSelect(entries.size - 1)
            if (intent.action == ACTION_RECORD_DEL_NOTIFY) {
                context.sendBroadcast(Intent(ACTION_RECORD_PREFERENCE_NOTIFY))
            }
        }

    }*/

    companion object {
        const val ACTION_RECORD_PREFERENCE_NOTIFY = "action_RecordPreferenceNotify"
        const val ACTION_RECORD_PRE_NOTIFY  = "actionRecordPreModify"
        const val ACTION_RECORD_DEL_NOTIFY  = "actionRecordDelayModify"
    }
}