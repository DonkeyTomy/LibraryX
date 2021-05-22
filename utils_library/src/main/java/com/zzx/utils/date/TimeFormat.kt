package com.zzx.utils.date

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**@author Tomy
 * Created by Tomy on 19/11/2020.
 */
object TimeFormat {

    private var DAY_FORMAT = "yyyy-MM-dd"

    private var DAY_FORMAT_WITHOUT_YEAR = "MM-dd"

    private var TIME_FORMAT = "HH:mm:ss"

    private var TIME_WITHOUT_SECOND = "HH-mm"

    private var FULL_TIME_FORMAT = "$DAY_FORMAT $TIME_FORMAT"

    private var FULL_TIME_FORMAT_WITHOUT_YEAR = "$DAY_FORMAT_WITHOUT_YEAR $TIME_FORMAT"

    private var DURATION_FORMAT = "mm:ss"

    private var DURATION_FORMAT_WITH_MILL = "ss.SS″"

    private val mDurationFormatWithMill by lazy {
        SimpleDateFormat(DURATION_FORMAT_WITH_MILL, Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("GMT+0")
        }
    }

    private val mDurationFormat by lazy {
        SimpleDateFormat(DURATION_FORMAT, Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("GMT+0")
        }
    }

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

    private val mFullTimeWithoutYearFormatter by lazy {
        SimpleDateFormat(FULL_TIME_FORMAT_WITHOUT_YEAR, Locale.getDefault())
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

    fun initDurationFormat(durationFormat: String) {
        DURATION_FORMAT = durationFormat
    }

    fun initDurationWithMillFormat(durationFormat: String) {
        DURATION_FORMAT_WITH_MILL = durationFormat
    }

    fun formatDay(time: Long): String {
        return mDayFormatter.format(Date(time))
    }

    fun formatOnlyTime(time: Long): String {
        return mTimeFormatter.format(Date(time))
    }

    fun formatTimeWithoutSecond(time: Long): String {
        return mTimeWithoutSecondFormatter.format(Date(time))
    }

    fun formatFullTimeWithoutYear(time: Long): String {
        return mFullTimeWithoutYearFormatter.format(Date(time))
    }

    fun formatFullTime(time: Long): String {
        return mFullTimeFormatter.format(Date(time))
    }

    fun formatDuration(time: Long): String {
        return mDurationFormat.format(Date(time))
    }

    fun formatDurationWithMill(time: Long): String {
        return mDurationFormatWithMill.format(Date(time))
    }

    /**
     * 根据当前日期获得是星期几
     */
    fun getWeek(): String {
        return when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            1 -> "星期日"
            2 -> "星期一"
            3 -> "星期二"
            4 -> "星期三"
            5 -> "星期四"
            6 -> "星期五"
            7 -> "星期六"
            else -> "星期日"
        }

    }

}