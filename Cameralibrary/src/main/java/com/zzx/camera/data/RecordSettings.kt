package com.zzx.camera.data

import android.content.Context
import com.zzx.utils.data.IDataSaver
import com.zzx.utils.data.SystemSettingSaver

/**@author Tomy
 * Created by Tomy on 2018/6/17.
 */
open class RecordSettings constructor(var mContext: Context, var name: String = mContext.packageName, mode: Int = Context.MODE_PRIVATE) {

    protected val mDataSaver: IDataSaver<String> = SystemSettingSaver(mContext)

    open fun setRecordDuration(duration: Int) {
        mDataSaver.saveInt(RECORD_DURATION, duration)
    }

    open fun setRecordVoice(needVoice: Boolean) {
        mDataSaver.saveBoolean(RECORD_VOICE, needVoice)
    }

    fun setCollideLevel(collide: Int) {
        mDataSaver.saveInt(RECORD_COLLIDE, collide)
    }

    open fun getRecordDuration(): Int {
        return mDataSaver.getInt(RECORD_DURATION, DURATION_DEFAULT)
    }

    fun getRecordVoice(): Boolean {
        return mDataSaver.getBoolean(RECORD_VOICE, RECORD_VOICE_DEFAULT)
    }

    fun getCollideLevel(): Int {
        return mDataSaver.getInt(RECORD_COLLIDE, COLLIDE_DEFAULT_MID)
    }

    companion object {
        const val RECORD_DURATION   = "RecordDuration"
        const val RECORD_VOICE       = "RecordMute"
        const val RECORD_COLLIDE    = "RecordCollide"

        const val DURATION_DEFAULT  = 3 * 60
        const val COLLIDE_DEFAULT_MID   = 2
        const val RECORD_VOICE_DEFAULT  = false
    }

}