package com.tomy.lib.ui.view.preference

import android.content.Context
import android.util.AttributeSet
import android.widget.Switch
import androidx.preference.PreferenceViewHolder
import com.tomy.lib.ui.R
import com.zzx.utils.rxjava.FlowableUtil
import timber.log.Timber

/**@author Tomy
 * Created by Tomy on 2018/11/20.
 */
open class MasterSwitchPreference: TwoTargetPreference {

    private var mSwitch: Switch? = null

    private var mChecked = false

    private var mSummaryEnabled = false

    constructor(context: Context): super(context)
    constructor(context: Context, attrs: AttributeSet): super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int): super(context, attrs, defStyleAttr, defStyleRes)

    override fun getSecondTargetResId(): Int {
        return R.layout.preference_widget_switch
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        Timber.e("onBindViewHolder.key = $key")
        /*if (!mSummaryEnabled) {
            holder.findViewById(android.R.id.title).apply {
                (layoutParams as ConstraintLayout.LayoutParams).bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            }
        }*/

        val widgetView = holder.findViewById(android.R.id.widget_frame)
        widgetView.setOnClickListener {
            Timber.e("${this.javaClass.simpleName} widgetView.Click().isEnabled = ${mSwitch?.isEnabled}")
            if (mSwitch?.isEnabled != true) {
                return@setOnClickListener
            }
            mSwitch?.isEnabled = false
            setChecked(!mChecked)
            /*if (!callChangeListener(mChecked)) {
                setChecked(!mChecked)
            } else {
                persistBoolean(mChecked)
            }*/
        }
        mSwitch = holder.findViewById(R.id.switchWidget) as Switch?
        mSwitch?.apply {
            contentDescription = title
            isChecked = mChecked
//            isEnabled = mSwitchEnabled
        }
        mListener?.onBindViewHolder(holder)
    }

    fun setChecked(checked: Boolean) {
        Timber.e("${this.javaClass.simpleName}  setChecked.isChecked = $checked; isChecked = ${mSwitch?.isChecked}")
        mChecked = checked
        mSwitch?.apply {
            if (isChecked != checked) {
                isChecked = checked
            }
        }
    }

    override fun setWidgetEnabled(enabled: Boolean) {
        Timber.e("${this.javaClass.simpleName} enabled = $enabled")
        FlowableUtil.setMainThread {
            mSwitch?.isEnabled = enabled
        }
    }

    fun isChecked(): Boolean {
        return mSwitch?.isEnabled == true && mSwitch?.isChecked == true
    }

    fun getSwitch(): Switch? {
        return mSwitch
    }

}