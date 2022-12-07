package com.zzx.camera.presenter

import android.content.Context
import android.view.View
import android.widget.RadioGroup
import android.widget.Switch
import butterknife.BindView
import butterknife.ButterKnife
import com.zzx.camera.R
import com.zzx.camera.data.RecordSettings

/**@author Tomy
 * Created by Tomy on 2018/6/17.
 */
class SettingPresenter(context: Context, view: View) {

    @BindView(R.id.switch_mute)
    lateinit var mMuteSwitch: Switch

    @BindView(R.id.camera_setting_radio_duration)
    lateinit var mDurationGroup: RadioGroup

    @BindView(R.id.camera_setting_radio_collide)
    lateinit var mCollideGroup: RadioGroup

    private val mRecordSetting = RecordSettings(context)

    private var mDurationArray: IntArray? = null
    private var mCollideArray: IntArray? = null

    private var mSettingChangeListener: SettingChangeListener? = null

    init {
        context.resources.apply {
            mDurationArray  = getIntArray(R.array.record_duration)
            mCollideArray   = getIntArray(R.array.collide)
        }
        ButterKnife.bind(this, view)
        /*mMuteSwitch.setOnCheckedChangeListener {
            _, isChecked ->
            mRecordSetting.setRecordVoice(isChecked)
            mSettingChangeListener?.onVoiceChanged(isChecked)
        }*/
        mDurationGroup.setOnCheckedChangeListener(GroupCheckChangedListener())
        mCollideGroup.setOnCheckedChangeListener(GroupCheckChangedListener())

        init()
    }

    fun init() {
        setRecordVoice(mRecordSetting.getRecordVoice())
        setRecordDuration(mRecordSetting.getRecordDuration())
        setCollideLevel(mRecordSetting.getCollideLevel())
    }

    fun setSettingChangeListener(listener: SettingChangeListener) {
        mSettingChangeListener = listener
    }

    fun setRecordVoice(voice: Boolean) {
        mMuteSwitch.isChecked = voice
    }

    fun getVoice(): Boolean {
        return mRecordSetting.getRecordVoice()
    }

    fun getRecordDuration(): Int {
        return mRecordSetting.getRecordDuration()
    }

    fun getCollideLevel(): Int {
        return mRecordSetting.getCollideLevel()
    }

    fun setRecordDuration(duration: Int) {
        mDurationGroup.check(
            when(duration) {
                60 -> R.id.radio_duration_one
                300 -> R.id.radio_duration_five
                else -> R.id.radio_duration_three
        })
    }

    fun setCollideLevel(level: Int) {
        mCollideGroup.check(
                when(level) {
                    0 -> R.id.radio_collide_low
                    2 -> R.id.radio_collide_high
                    else -> R.id.radio_collide_mid
                }
        )
    }

    inner class GroupCheckChangedListener: RadioGroup.OnCheckedChangeListener {

        override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
            when(group!!.id) {
                R.id.camera_setting_radio_duration  -> {
                    var duration = 0
                    when(checkedId) {
                        R.id.radio_duration_one     -> duration = mDurationArray!![0]
                        R.id.radio_duration_three   -> duration = mDurationArray!![1]
                        R.id.radio_duration_five    -> duration = mDurationArray!![2]
                    }
                    mRecordSetting.setRecordDuration(duration)
                    mSettingChangeListener?.onSettingDurationChanged(duration)
                }
                R.id.camera_setting_radio_collide   -> {
                    var collideLevel = 0
                    when(checkedId) {
                        R.id.radio_collide_low      -> collideLevel = mCollideArray!![0]
                        R.id.radio_collide_mid      -> collideLevel = mCollideArray!![1]
                        R.id.radio_collide_high     -> collideLevel = mCollideArray!![2]
                    }
                    mRecordSetting.setCollideLevel(collideLevel)
                    mSettingChangeListener?.onCollideLevelChanged(collideLevel)
                }
            }

        }

    }

    interface SettingChangeListener {

        fun onSettingDurationChanged(duration: Int)

        fun onCollideLevelChanged(collide: Int)

        fun onVoiceChanged(needVoice: Boolean)
    }

}