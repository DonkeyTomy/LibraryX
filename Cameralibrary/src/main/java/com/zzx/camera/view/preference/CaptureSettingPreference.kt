package com.zzx.camera.view.preference

import android.content.Context
import android.content.Intent
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.ListView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.tomy.lib.ui.view.preference.CustomListPreference
import com.zzx.camera.R
import com.zzx.camera.R2
import com.zzx.camera.adapter.PhotoSettingAdapter
import com.zzx.camera.data.HCameraSettings
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**@author Tomy
 * Created by Tomy on 2018/10/15.
 */
class CaptureSettingPreference(context: Context, attr: AttributeSet): CustomListPreference(context, attr) {

    @BindView(R2.id.check_box)
    lateinit var mCheckBox: CheckBox

    @BindView(R2.id.icon)
    lateinit var mIvIcon: ImageView

    private val mIconList = ArrayList<Drawable>()

    private var mUnBinder: Unbinder? = null

    private lateinit var mDefault: String

    private val mCameraSetting by lazy {
        HCameraSettings(context)
    }

    init {
        context.obtainStyledAttributes(attr, com.tomy.lib.ui.R.styleable.CustomListPreference).apply {
            getResourceId(com.tomy.lib.ui.R.styleable.CustomListPreference_icon_entries, 0).apply {
                context.resources.obtainTypedArray(this).apply {
                    for (i in 0 until length()) {
                        getDrawable(i)?.let { mIconList.add(it) }
                    }
                    recycle()
                }
            }
            recycle()
        }
    }


    private val mListAdapter by lazy {
        PhotoSettingAdapter(context).apply {
            setIconListId(mIconList)
            addData(entries.toList())
        }
    }

    override fun onGetDefaultValue(a: TypedArray, index: Int): String {
        return a.getString(index)?.apply {
            mDefault = this
            Timber.e("onGetDefaultValue = $this. value = $value")
        } ?: ""
    }

    override fun initData() {
        setChecked(mCameraSetting.getPhotoModeKey() == key)
        value = getPersistedString(mDefault)
        Timber.e("${HCameraSettings.TAG_C_S} value = $value, key = $key")
        setSelect(findIndexOfValue(value))
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
            value = entryValues[position].toString()
            mIvIcon.setImageDrawable(mIconList[position])
        }
    }

    override fun setSummary(summary: CharSequence?) {

    }

    fun setChecked(checked: Boolean) {
        mCheckBox.isChecked = checked
    }

    override fun bindListView(listView: ListView) {
        listView.apply {
            setSelector(R.drawable.bg_setting_item)
            adapter = mListAdapter
            mListAdapter.setCheck(findIndexOfValue(value))
            setOnItemClickListener { _, _, position, _ ->
                setSelect(position)
                mListAdapter.setCheck(position)
                Observable.just(Unit)
                        .observeOn(AndroidSchedulers.mainThread())
                        .delay(100, TimeUnit.MILLISECONDS)
                        .subscribe {
                            mDialog?.dismiss()
                        }
            }
        }
    }

    override fun onClick() {
        if (key == getString(R.string.key_mode_picture)) {
            value = "0"
        } else {
            super.onClick()
        }
        setChecked(true)
        when (key) {
            getString(R.string.key_mode_continuous) -> {
                mCameraSetting.setPhotoMode(1)
                setOtherPreferenceUnCheck(R.string.key_mode_interval)
                setOtherPreferenceUnCheck(R.string.key_mode_timer)
                setOtherPreferenceUnCheck(R.string.key_mode_picture)
            }
            getString(R.string.key_mode_interval)   -> {
                mCameraSetting.setPhotoMode(2)
                setOtherPreferenceUnCheck(R.string.key_mode_continuous)
                setOtherPreferenceUnCheck(R.string.key_mode_timer)
                setOtherPreferenceUnCheck(R.string.key_mode_picture)
            }
            getString(R.string.key_mode_timer)      -> {
                mCameraSetting.setPhotoMode(3)
                setOtherPreferenceUnCheck(R.string.key_mode_interval)
                setOtherPreferenceUnCheck(R.string.key_mode_continuous)
                setOtherPreferenceUnCheck(R.string.key_mode_picture)
            }
            getString(R.string.key_mode_picture)    -> {
                mCameraSetting.setPhotoMode(0)
                setOtherPreferenceUnCheck(R.string.key_mode_interval)
                setOtherPreferenceUnCheck(R.string.key_mode_timer)
                setOtherPreferenceUnCheck(R.string.key_mode_continuous)
            }
        }
    }

    fun setOtherPreferenceUnCheck(keyId: Int) {
        (findPreferenceInHierarchy(getString(keyId)) as CaptureSettingPreference).setChecked(false)
    }

    /**
     * 当预录延录更改时需要通知摄像头,若正在录像,需要重启录像.
     */
    private fun sendBroadcastToCamera() {
        val intent = Intent(ACTION_RECORD_PREFERENCE_NOTIFY)
        context.sendBroadcast(intent)
    }

    companion object {
        const val ACTION_RECORD_PREFERENCE_NOTIFY = "action_RecordPreferenceNotify"
    }
}