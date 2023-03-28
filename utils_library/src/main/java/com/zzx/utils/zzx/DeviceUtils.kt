package com.zzx.utils.zzx

import android.content.Context
import android.provider.Settings.System
import android.text.TextUtils
import com.zzx.utils.system.SettingsUtils

/**@author Tomy
 * Created by Tomy on 2014/10/23.
 */
object DeviceUtils {
    private const val MODEL_NEED_PRE = "ZZXNeedPre"
    const val CFG_ENABLED_PRISON = "ZZXPrisonEnabled"


    fun getUserNum(context: Context): String {
        return SettingsUtils.getSystemString(context, USER_NUMBER, USER_DEFAULT_NUM)
    }

    fun getUserName(context: Context): String {
        return SettingsUtils.getSystemString(context, USER_NAME, USER_DEFAULT_NAME)
    }

    fun setUserName(context: Context, name: String) {
        SettingsUtils.putSystemValue(context, USER_NAME, name)
    }

    fun getDeviceNum(context: Context): String {
        return SettingsUtils.getSystemString(context, DEVICE_NUMBER, DEVICE_DEFAULT_ONLY_NUM)
    }

    fun getDeviceInfo(context: Context): String {
        return "$DEVICE_PRE-${getModelName(context)}-${getDeviceNum(context)}"
    }

    fun getModelName(context: Context): String {
        return SettingsUtils.getSystemString(context, MODEL_NAME, MODEL_DEFAULT_NAME)
    }

    fun getModelPreName(context: Context): String {
        val model = System.getString(context.contentResolver, MODEL_NEED_PRE)
        return if (TextUtils.isEmpty(model)) {
            DEVICE_PRE
        } else model
    }


    fun writeUserNum(context: Context, policeNum: String?): Boolean {
        if (policeNum == null) {
            return false
        }
        /*if (isPrisonEnabled(context)) {
            if (!policeNum.matches("[A-Za-z0-9]{8}".toRegex())) {
                return false
            }
        } else {
            if (!policeNum.matches("[A-Za-z0-9]{6}".toRegex())) {
                return false
            }
        }*/
        SettingsUtils.putSystemValue(context, USER_NUMBER, policeNum)
        return true
    }

    fun writeDeviceNum(context: Context, deviceNum: String?): Boolean {
        return deviceNum != null && System.putString(context.contentResolver, DEVICE_NUMBER, deviceNum)
    }

    fun getServiceInfo(mContext: Context): ServiceInfo? {
        var info: ServiceInfo? = ServiceInfo()
        val resolver = mContext.contentResolver
        val serviceIP = System.getString(resolver, SERVICE_IP_ADDRESS)
        val portStr = System.getString(resolver, SERVICE_IP_PORT)
        if (serviceIP != null && portStr != null && serviceIP != "" && portStr != "") {
            try {
                val port = Integer.parseInt(portStr)
                info!!.mServiceIp = serviceIP
                info.mPort = port
            } catch (e: Exception) {
                return null
            }

        } else {
            val parser = XMLParser.instance
            info = parser.parserXMLFile("/etc/service.xml")
            parser.release()
        }
        return info
    }

    fun checkHasService(context: Context): Boolean {
        val info = DeviceUtils.getServiceInfo(context)
        return info != null
    }

    fun getSerialNum(context: Context): String {
        val value = System.getString(context.contentResolver, CFG_SETTING_SERIAL)
        return if (TextUtils.isEmpty(value)) {
            MODEL_DEFAULT_SERIAL
        } else value
    }

    fun setSerialNum(context: Context, num: String) {
        System.putString(context.contentResolver, CFG_SETTING_SERIAL, num)
    }

    fun getGroupNum(context: Context): String {
        return SettingsUtils.getSystemString(context, GROUP_NUM, GROUP_NUM_DEFAULT)
    }

    fun setGroupNum(context: Context, num: String) {
        SettingsUtils.putSystemValue(context, GROUP_NUM, num)
    }

    fun getGroupName(context: Context): String {
        return SettingsUtils.getSystemString(context, GROUP_NAME, GROUP_NAME_DEFAULT)
    }

    fun setGroupName(context: Context, name: String) {
        SettingsUtils.putSystemValue(context, GROUP_NAME, name)
    }

    fun getIDCodeName(context: Context): String {
        val value = System.getString(context.contentResolver, CFG_SETTING_IDCode)
        return if (TextUtils.isEmpty(value)) {
            MODEL_DEFAULT_IDCODE
        } else value
    }

    fun getUserPassword(context: Context): String {
        return SettingsUtils.getSystemString(context, USER_PSW, USER_DEFAULT_PWD)
    }

    fun setUserPassword(context: Context, password: String) {
        SettingsUtils.putSystemValue(context, USER_PSW, password)
    }

    fun getAdminPassword(context: Context): String {
        return SettingsUtils.getSystemString(context, ADMIN_PSW, ADMIN_DEFAULT_PASSWORD)
    }

    fun setAdminPassword(context: Context, password: String) {
        SettingsUtils.putSystemValue(context, ADMIN_PSW, password)
    }

    fun getSuperAdminPassword(context: Context): String {
        return SettingsUtils.getSystemString(context, ADMIN_SUPER_PSW, DEVICE_DEFAULT_SUPER_PASSWORD)
    }

    fun setSuperAdminPassword(context: Context, password: String) {
        SettingsUtils.putSystemValue(context, ADMIN_SUPER_PSW, password)
    }

    fun setDialOpenEnable(context: Context, enable: String) {
        SettingsUtils.putSystemValue(context, IsOpenDialer, enable)
    }

    fun getDialIsOpen(context: Context): String {
        val value = System.getString(context.contentResolver, IsOpenDialer)
        return if (TextUtils.isEmpty(value)) {
            "1"
        } else value
    }

    const val PATH_POLICE_NUMBER = "/sys/devices/platform/zzx-misc/police_num_stats"
    const val PATH_GPS_INFO = "/sys/devices/platform/zzx-misc/gps_stats"
    const val USER_DEFAULT_NUM = "000000"
    const val PRISONER_NUMBER = "PrisonerNumber"
    const val USER_DEFAULT_NUM_8 = "00000000"
    const val PRISONER_DEFAULT_NUM = "0000000000"
    const val DEVICE_DEFAULT_NUM = "DSJ-00-00000"
    const val DEVICE_DEFAULT_ONLY_NUM = "00000"
    const val SERVICE_IP_ADDRESS = "service_ip_address"
    const val SERVICE_IP_PORT = "service_ip_port"
    const val USER_NUMBER = "user_number"

    const val USER_NAME = "user_name"
    const val USER_DEFAULT_NAME = "123456"

    const val GROUP_NUM     = "group_num"
    const val GROUP_NUM_DEFAULT     ="123456"
    const val GROUP_NAME    = "group_name"
    const val GROUP_NAME_DEFAULT    = "12345"

    const val DEVICE_NUMBER = "device_number"
    const val DEVICE_PRE = "DSJ"
    const val MODEL_NAME = "model_name"
    const val MODEL_DEFAULT_NAME = "00"
    const val MODEL_DEFAULT_SERIAL = "0000000"
    const val CFG_SETTING_SERIAL = "ZZXSerial"
    const val CFG_SETTING_IDCode = "ZZXIDCode"
    const val MODEL_DEFAULT_IDCODE = "ABCDE"
    const val CFG_ENABLED_VIDEO_CUT = "ZZXCut"

    /**
     * 密码
     */
    /**
     * 普通用户密码
     */
    const val USER_PSW = "zzx_user_password"
    const val USER_DEFAULT_PWD  = "000000"
    /**
     * 管理员用户密码
     */
    const val ADMIN_PSW = "zzx_admin_password"
    /**
     * 超级管理员用户密码
     */
    const val ADMIN_SUPER_PSW = "zzx_super_password"

    const val ADMIN_DEFAULT_PASSWORD = "888888"

    const val IsOpenDialer = "is_open_dialer"

    /**
     * 设备H9超级密码
     */
    const val DEVICE_DEFAULT_SUPER_PASSWORD = "191021"
}
