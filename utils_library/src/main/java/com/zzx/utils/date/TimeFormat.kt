package com.zzx.utils.date

import java.text.SimpleDateFormat
import java.util.*

/**@author Tomy
 * Created by Tomy on 19/11/2020.
 */
object TimeFormat {

    private var DAY_FORMAT    = "yyyy-MM-dd"

    private var TIME_FORMAT   = "HH-mm-ss"

    private var TIME_WITHOUT_SECOND = "HH-mm"

    private var FULL_TIME_FORMAT  = "$DAY_FORMAT $TIME_FORMAT"

    private val mDayFormatter by lazy {
        SimpleDateFormat(DAY_FORMAT, Locale.getDefault())
    }

    private val mTimeFormatter by lazy {
        SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
    }

    private val mTimeWithoutSecondFormatter by lazy {
        SimpleDateFormat(TIME_WITHOUT_SECOND, Locale.getDefault())
    }

    private val mFullTimeFormatter by lazy {
        SimpleDateFormat(FULL_TIME_FORMAT, Locale.getDefault())
    }

    fun initDayFormat(dayFormat: String) {
        DAY_FORMAT = dayFormat
    }

    fun initTimeFormat(timeFormat: String) {
        TIME_FORMAT = timeFormat
    }

    fun initTimeWithoutSecondFormat(timeWithoutSecond: String) {
        TIME_WITHOUT_SECOND = timeWithoutSecond
    }

    fun initFullTimeFormat(fullTimeFormat: String) {
        FULL_TIME_FORMAT = fullTimeFormat
    }

    fun formatDay(time: Long): String {
        return mDayFormatter.format(Date(time))
    }

    fun formatTime(time: Long): String {
        return mTimeFormatter.format(Date(time))
    }

    fun formatTimeWithoutSecond(time: Long): String {
        return mTimeWithoutSecondFormatter.format(Date(time))
    }

    fun formatFullTime(time: Long): String {
        return mFullTimeFormatter.format(Date(time))
    }
}