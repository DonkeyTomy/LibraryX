package com.ycuwq.datepicker.date

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.tomy.lib.ui.R
import com.ycuwq.datepicker.date.DayPicker.OnDaySelectedListener
import com.ycuwq.datepicker.date.MonthPicker.OnMonthSelectedListener
import com.ycuwq.datepicker.date.YearPicker.OnYearSelectedListener
import com.zzx.utils.date.TimeFormat
import timber.log.Timber
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * 日期选择器
 * Created by ycuwq on 2018/1/1.
 */
class DatePicker @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr), OnYearSelectedListener, OnMonthSelectedListener,
    OnDaySelectedListener {
    /**
     * Gets year picker.
     *
     * @return the year picker
     */
    var yearPicker: YearPicker? = null
        private set

    /**
     * Gets month picker.
     *
     * @return the month picker
     */
    var monthPicker: MonthPicker? = null
        private set

    /**
     * Gets day picker.
     *
     * @return the day picker
     */
    var dayPicker: DayPicker? = null
        private set
    private var mMaxDate: Long? = null
    private var mMinDate: Long? = null
    private var mOnDateSelectedListener: OnDateSelectedListener? = null
    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        if (attrs == null) {
            return
        }
        val a = context.obtainStyledAttributes(attrs, R.styleable.DatePicker)
        val textSize = a.getDimensionPixelSize(
            R.styleable.DatePicker_itemTextSize, resources.getDimensionPixelSize(R.dimen.WheelItemTextSize)
        )
        val textColor = a.getColor(
            R.styleable.DatePicker_itemTextColor, Color.BLACK
        )
        val isTextGradual = a.getBoolean(R.styleable.DatePicker_textGradual, true)
        val isCyclic = a.getBoolean(R.styleable.DatePicker_wheelCyclic, false)
        val halfVisibleItemCount = a.getInteger(R.styleable.DatePicker_halfVisibleItemCount, 2)
        val selectedItemTextColor = a.getColor(
            R.styleable.DatePicker_selectedTextColor, resources.getColor(R.color.date_picker_selectedTextColor)
        )
        val selectedItemTextSize = a.getDimensionPixelSize(
            R.styleable.DatePicker_selectedTextSize, resources.getDimensionPixelSize(R.dimen.WheelSelectedItemTextSize)
        )
        val itemWidthSpace = a.getDimensionPixelSize(
            R.styleable.DatePicker_itemWidthSpace, resources.getDimensionPixelOffset(R.dimen.WheelItemWidthSpace)
        )
        val itemHeightSpace = a.getDimensionPixelSize(
            R.styleable.DatePicker_itemHeightSpace, resources.getDimensionPixelOffset(R.dimen.WheelItemHeightSpace)
        )
        val isZoomInSelectedItem = a.getBoolean(R.styleable.DatePicker_zoomInSelectedItem, true)
        val isShowCurtain = a.getBoolean(R.styleable.DatePicker_wheelCurtain, true)
        val curtainColor = a.getColor(R.styleable.DatePicker_wheelCurtainColor, Color.WHITE)
        val isShowCurtainBorder = a.getBoolean(R.styleable.DatePicker_wheelCurtainBorder, true)
        val curtainBorderColor = a.getColor(
            R.styleable.DatePicker_wheelCurtainBorderColor, resources.getColor(R.color.date_picker_divider)
        )
        a.recycle()
        setTextSize(textSize)
        setTextColor(textColor)
        setTextGradual(isTextGradual)
        setCyclic(isCyclic)
        setHalfVisibleItemCount(halfVisibleItemCount)
        setSelectedItemTextColor(selectedItemTextColor)
        setSelectedItemTextSize(selectedItemTextSize)
        setItemWidthSpace(itemWidthSpace)
        setItemHeightSpace(itemHeightSpace)
        setZoomInSelectedItem(isZoomInSelectedItem)
        setShowCurtain(isShowCurtain)
        setCurtainColor(curtainColor)
        setShowCurtainBorder(isShowCurtainBorder)
        setCurtainBorderColor(curtainBorderColor)
    }

    private fun initChild() {
        yearPicker = findViewById(R.id.yearPicker_layout_date)
        yearPicker?.setOnYearSelectedListener(this)
        monthPicker = findViewById(R.id.monthPicker_layout_date)
        monthPicker?.setOnMonthSelectedListener(this)
        dayPicker = findViewById(R.id.dayPicker_layout_date)
        dayPicker?.setOnDaySelectedListener(this)
    }

    override fun setBackgroundColor(color: Int) {
        super.setBackgroundColor(color)
        yearPicker?.setBackgroundColor(color)
        monthPicker?.setBackgroundColor(color)
        dayPicker?.setBackgroundColor(color)
    }

    override fun setBackgroundResource(resid: Int) {
        super.setBackgroundResource(resid)
        yearPicker?.setBackgroundResource(resid)
        monthPicker?.setBackgroundResource(resid)
        dayPicker?.setBackgroundResource(resid)
    }

    override fun setBackground(background: Drawable) {
        super.setBackground(background)
        yearPicker?.background = background
        monthPicker?.background = background
        dayPicker?.background = background
    }

    private fun onDateSelected() {
        mOnDateSelectedListener?.onDateSelected(
            getYear(), getMonth(), getDay()
        )
    }

    override fun onMonthSelected(month: Int) {
        dayPicker?.setMonth(getYear(), month)
        onDateSelected()
    }

    override fun onDaySelected(day: Int) {
        onDateSelected()
    }

    override fun onYearSelected(year: Int) {
        monthPicker!!.setYear(year)
        dayPicker!!.setMonth(year, getMonth())
        onDateSelected()
    }

    /**
     * Sets date.
     *
     * @param year  the year
     * @param month the month
     * @param day   the day
     */
    fun setDate(year: Int, month: Int, day: Int) {
        setDate(year, month, day, true)
    }

    /**
     * Sets date.
     *
     * @param year         the year
     * @param month        the month
     * @param day          the day
     * @param smoothScroll the smooth scroll
     */
    fun setDate(year: Int, month: Int, day: Int, smoothScroll: Boolean) {
        yearPicker!!.setSelectedYear(year, smoothScroll)
        monthPicker!!.setSelectedMonth(month, smoothScroll)
        dayPicker!!.setSelectedDay(day, smoothScroll)
    }

    fun setDate(date: Long) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = date
        setDate(calendar[Calendar.YEAR], calendar[Calendar.MONTH] + 1, calendar[Calendar.DAY_OF_MONTH], true)
    }

    fun setMaxDate(date: Long) {
        setCyclic(false)
        mMaxDate = date
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = date
        Timber.v("setMaxYear = ${TimeFormat.formatDay(date)}")
        yearPicker!!.setEndYear(calendar[Calendar.YEAR])
        monthPicker!!.setMaxDate(date)
        dayPicker!!.setMaxDate(date)
        monthPicker!!.setYear(yearPicker!!.selectedYear)
        dayPicker!!.setMonth(yearPicker!!.selectedYear, monthPicker!!.selectedMonth)
    }

    fun setMinDate(date: Long) {
        setCyclic(false)
        mMinDate = date
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = date
        Timber.v("setMinYear = ${TimeFormat.formatDay(date)}")
        yearPicker!!.setStartYear(calendar[Calendar.YEAR])
        monthPicker!!.setMinDate(date)
        dayPicker!!.setMinDate(date)
        monthPicker!!.setYear(yearPicker!!.selectedYear)
        dayPicker!!.setMonth(yearPicker!!.selectedYear, monthPicker!!.selectedMonth)
    }

    /**
     * Gets date.
     * @return the date
     */
    fun getDate(): String {
        val format = SimpleDateFormat.getDateInstance()
        return getDate(format)
    }

    /**
     * Gets date.
     *
     * @param dateFormat the date format
     * @return the date
     */
    fun getDate(dateFormat: DateFormat): String {
        val calendar = Calendar.getInstance()
        setDate(getYear(), getMonth() - 1, getDay())
        return dateFormat.format(calendar.time)
    }

    /**
     * Gets year.
     *
     * @return the year
     */
    fun getYear() = yearPicker!!.selectedYear

    /**
     * Gets month.
     *
     * @return the month
     */
    fun getMonth() = monthPicker!!.selectedMonth

    /**
     * Gets day.
     *
     * @return the day
     */
    fun getDay() = dayPicker!!.selectedDay

    /**
     * 一般列表的文本颜色
     *
     * @param textColor 文本颜色
     */
    fun setTextColor(textColor: Int) {
        dayPicker!!.textColor = textColor
        monthPicker!!.textColor = textColor
        yearPicker!!.textColor = textColor
    }

    /**
     * 一般列表的文本大小
     *
     * @param textSize 文字大小
     */
    fun setTextSize(textSize: Int) {
        dayPicker!!.textSize = textSize
        monthPicker!!.textSize = textSize
        yearPicker!!.textSize = textSize
    }

    /**
     * 设置被选中时候的文本颜色
     *
     * @param selectedItemTextColor 文本颜色
     */
    fun setSelectedItemTextColor(selectedItemTextColor: Int) {
        dayPicker!!.selectedItemTextColor = selectedItemTextColor
        monthPicker!!.selectedItemTextColor = selectedItemTextColor
        yearPicker!!.selectedItemTextColor = selectedItemTextColor
    }

    /**
     * 设置被选中时候的文本大小
     *
     * @param selectedItemTextSize 文字大小
     */
    fun setSelectedItemTextSize(selectedItemTextSize: Int) {
        dayPicker!!.selectedItemTextSize = selectedItemTextSize
        monthPicker!!.selectedItemTextSize = selectedItemTextSize
        yearPicker!!.selectedItemTextSize = selectedItemTextSize
    }

    /**
     * 设置显示数据量的个数的一半。
     * 为保证总显示个数为奇数,这里将总数拆分，itemCount = mHalfVisibleItemCount * 2 + 1
     *
     * @param halfVisibleItemCount 总数量的一半
     */
    fun setHalfVisibleItemCount(halfVisibleItemCount: Int) {
        dayPicker!!.halfVisibleItemCount = halfVisibleItemCount
        monthPicker!!.halfVisibleItemCount = halfVisibleItemCount
        yearPicker!!.halfVisibleItemCount = halfVisibleItemCount
    }

    /**
     * Sets item width space.
     *
     * @param itemWidthSpace the item width space
     */
    fun setItemWidthSpace(itemWidthSpace: Int) {
        dayPicker!!.itemWidthSpace = itemWidthSpace
        monthPicker!!.itemWidthSpace = itemWidthSpace
        yearPicker!!.itemWidthSpace = itemWidthSpace
    }

    /**
     * 设置两个Item之间的间隔
     *
     * @param itemHeightSpace 间隔值
     */
    fun setItemHeightSpace(itemHeightSpace: Int) {
        dayPicker!!.itemHeightSpace = itemHeightSpace
        monthPicker!!.itemHeightSpace = itemHeightSpace
        yearPicker!!.itemHeightSpace = itemHeightSpace
    }

    /**
     * Set zoom in center item.
     *
     * @param zoomInSelectedItem the zoom in center item
     */
    fun setZoomInSelectedItem(zoomInSelectedItem: Boolean) {
        dayPicker!!.isZoomInSelectedItem = zoomInSelectedItem
        monthPicker!!.isZoomInSelectedItem = zoomInSelectedItem
        yearPicker!!.isZoomInSelectedItem = zoomInSelectedItem
    }

    /**
     * 设置是否循环滚动。
     * set wheel cyclic
     * @param cyclic 上下边界是否相邻
     */
    fun setCyclic(cyclic: Boolean) {
        dayPicker!!.isCyclic = cyclic
        monthPicker!!.isCyclic = cyclic
        yearPicker!!.isCyclic = cyclic
    }

    /**
     * 设置文字渐变，离中心越远越淡。
     * Set the text color gradient
     * @param textGradual 是否渐变
     */
    fun setTextGradual(textGradual: Boolean) {
        dayPicker!!.isTextGradual = textGradual
        monthPicker!!.isTextGradual = textGradual
        yearPicker!!.isTextGradual = textGradual
    }

    /**
     * 设置中心Item是否有幕布遮盖
     * set the center item curtain cover
     * @param showCurtain 是否有幕布
     */
    fun setShowCurtain(showCurtain: Boolean) {
        dayPicker!!.isShowCurtain = showCurtain
        monthPicker!!.isShowCurtain = showCurtain
        yearPicker!!.isShowCurtain = showCurtain
    }

    /**
     * 设置幕布颜色
     * set curtain color
     * @param curtainColor 幕布颜色
     */
    fun setCurtainColor(curtainColor: Int) {
        dayPicker!!.curtainColor = curtainColor
        monthPicker!!.curtainColor = curtainColor
        yearPicker!!.curtainColor = curtainColor
    }

    /**
     * 设置幕布是否显示边框
     * set curtain border
     * @param showCurtainBorder 是否有幕布边框
     */
    fun setShowCurtainBorder(showCurtainBorder: Boolean) {
        dayPicker!!.isShowCurtainBorder = showCurtainBorder
        monthPicker!!.isShowCurtainBorder = showCurtainBorder
        yearPicker!!.isShowCurtainBorder = showCurtainBorder
    }

    /**
     * 幕布边框的颜色
     * curtain border color
     * @param curtainBorderColor 幕布边框颜色
     */
    fun setCurtainBorderColor(curtainBorderColor: Int) {
        dayPicker!!.curtainBorderColor = curtainBorderColor
        monthPicker!!.curtainBorderColor = curtainBorderColor
        yearPicker!!.curtainBorderColor = curtainBorderColor
    }

    /**
     * 设置选择器的指示器文本
     * set indicator text
     * @param yearText  年指示器文本
     * @param monthText 月指示器文本
     * @param dayText   日指示器文本
     */
    fun setIndicatorText(yearText: String?, monthText: String?, dayText: String?) {
        yearPicker!!.setIndicatorText(yearText)
        monthPicker!!.setIndicatorText(monthText)
        dayPicker!!.setIndicatorText(dayText)
    }

    /**
     * 设置指示器文字的颜色
     * set indicator text color
     * @param textColor 文本颜色
     */
    fun setIndicatorTextColor(textColor: Int) {
        yearPicker!!.setIndicatorTextColor(textColor)
        monthPicker!!.setIndicatorTextColor(textColor)
        dayPicker!!.setIndicatorTextColor(textColor)
    }

    /**
     * 设置指示器文字的大小
     * indicator text size
     * @param textSize 文本大小
     */
    fun setIndicatorTextSize(textSize: Int) {
        yearPicker!!.textSize = textSize
        monthPicker!!.textSize = textSize
        dayPicker!!.textSize = textSize
    }

    /**
     * Sets on date selected listener.
     *
     * @param onDateSelectedListener the on date selected listener
     */
    fun setOnDateSelectedListener(onDateSelectedListener: OnDateSelectedListener?) {
        mOnDateSelectedListener = onDateSelectedListener
    }

    /**
     * The interface On date selected listener.
     */
    interface OnDateSelectedListener {
        /**
         * On date selected.
         *
         * @param year  the year
         * @param month the month
         * @param day   the day
         */
        fun onDateSelected(year: Int, month: Int, day: Int)
    }
    /**
     * Instantiates a new Date picker.
     *
     * @param context      the context
     * @param attrs        the attrs
     * @param defStyleAttr the def style attr
     */
    /**
     * Instantiates a new Date picker.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    /**
     * Instantiates a new Date picker.
     *
     * @param context the context
     */
    init {
        LayoutInflater.from(context).inflate(R.layout.layout_date, this)
        initChild()
        initAttrs(context, attrs)
        yearPicker!!.setBackgroundDrawable(background)
        monthPicker!!.setBackgroundDrawable(background)
        dayPicker!!.setBackgroundDrawable(background)
    }
}