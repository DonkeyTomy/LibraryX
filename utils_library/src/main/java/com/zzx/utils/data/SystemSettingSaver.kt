package com.zzx.utils.data

import android.content.Context
import com.zzx.utils.system.SettingsUtils

class SystemSettingSaver(val mContext: Context): IDataSaver<String> {

    override fun saveInt(key: String, value: Int) {
        SettingsUtils.putSystemValue(mContext, key, value)
    }

    override fun saveString(key: String, value: String) {
        SettingsUtils.putSystemValue(mContext, key, value)
    }

    override fun saveFloat(key: String, value: Float) {
        SettingsUtils.putSystemValue(mContext, key, value)
    }

    override fun saveBoolean(key: String, value: Boolean) {
        SettingsUtils.putSystemValue(mContext, key, if (value) 1 else -1)
    }

    override fun saveLong(key: String, value: Long) {
        SettingsUtils.putSystemValue(mContext, key, value)
    }

    override fun getInt(key: String, defValue: Int): Int {
        return SettingsUtils.getSystemInt(mContext, key, defValue)
    }

    override fun getString(key: String, defValue: String): String {
        return SettingsUtils.getSystemString(mContext, key, defValue)
    }

    override fun getLong(key: String, defValue: Long): Long {
        return SettingsUtils.getSystemLong(mContext, key, defValue)
    }

    override fun getFloat(key: String, defValue: Float): Float {
        return SettingsUtils.getSystemFloat(mContext, key, defValue)
    }

    override fun getBoolean(key: String, defValue: Boolean): Boolean {
        return SettingsUtils.getSystemInt(mContext, key, if (defValue) 1 else -1) == 1
    }

    override fun clear() {

    }

    override fun release() {
    }
}