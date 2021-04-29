package com.ycuwq.datepicker.date

import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.tomy.lib.ui.R
import java.util.*

/**
 * 时间选择器，弹出框
 * Created by ycuwq on 2018/1/6.
 */
open class DatePickerDialogFragment : DialogFragment() {

    private val mCalendar by lazy { Calendar.getInstance() }

    protected var mDatePicker: DatePicker? = null
    private var mSelectedYear = -1
    private var mSelectedMonth = -1
    private var mSelectedDay = -1
    private var mOnDateChooseListener: DatePicker.OnDateSelectedListener? = null
    private var mOnDateSelectListener: DatePicker.OnDateSelectedListener? = null
    private var mIsShowAnimation = true
    protected var mCancelButton: Button? = null
    protected var mDecideButton: Button? = null

    private var mMaxDateInMill: Long? = null

    private var mMinDateInMill: Long? = null

    fun setOnDateChooseListener(onDateChooseListener: DatePicker.OnDateSelectedListener?) {
        mOnDateChooseListener = onDateChooseListener
    }

    fun setOnDateSelectedListener(onDateChooseListener: DatePicker.OnDateSelectedListener?) {
        mOnDateSelectListener = onDateChooseListener
    }

    fun showAnimation(show: Boolean) {
        mIsShowAnimation = show
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_date, container)
        mDatePicker = view.findViewById(R.id.dayPicker_dialog)
        mDatePicker?.apply {
            mMaxDateInMill?.let {
                setMaxDate(it)
            }
            mMinDateInMill?.let {
                setMinDate(it)
            }
            mOnDateSelectListener?.let {
                setOnDateSelectedListener(it)
            }
        }
        mCancelButton = view.findViewById(R.id.btn_dialog_date_cancel)
        mDecideButton = view.findViewById(R.id.btn_dialog_date_decide)
        mCancelButton?.setOnClickListener { dismiss() }
        mDecideButton?.setOnClickListener {
            mOnDateChooseListener?.onDateSelected(
                mDatePicker!!.getYear(), mDatePicker!!.getMonth(), mDatePicker!!.getDay()
            )
            dismiss()
        }
        if (mSelectedYear > 0) {
            setSelectedDate()
        }
        initChild()
        return view
    }


    protected fun initChild() {}
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext(), R.style.DatePickerBottomDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // 设置Content前设定
        dialog.setContentView(R.layout.dialog_date)
        dialog.setCanceledOnTouchOutside(true) // 外部点击取消
        dialog.window?.apply {
            if (mIsShowAnimation) {
                attributes.windowAnimations = R.style.DatePickerDialogAnim
            }
            val lp = attributes
            lp.gravity = Gravity.BOTTOM // 紧贴底部
            lp.width = WindowManager.LayoutParams.MATCH_PARENT // 宽度持平
            lp.dimAmount = 0.35f
            attributes = lp
            addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }
        return dialog
    }

    fun setSelectedDate(date: Long) {
        mCalendar.apply {
            timeInMillis    = date
            setSelectedDate(get(Calendar.YEAR), get(Calendar.MONTH) + 1, get(Calendar.DAY_OF_MONTH))
        }
    }

    fun setSelectedDate(year: Int, month: Int, day: Int) {
        mSelectedYear = year
        mSelectedMonth = month
        mSelectedDay = day
        setSelectedDate()
    }

    private fun setSelectedDate() {
        mDatePicker?.setDate(mSelectedYear, mSelectedMonth, mSelectedDay, false)
    }

    fun setMaxDateInMill(date: Long) {
        mMaxDateInMill = date
    }

    fun setMinDateInMill(date: Long) {
        mMinDateInMill = date
    }

    fun show(fragmentManager: FragmentManager) {
        show(fragmentManager, this::class.java.name)
    }

}