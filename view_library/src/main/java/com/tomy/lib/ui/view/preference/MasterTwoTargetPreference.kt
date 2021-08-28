package com.tomy.lib.ui.view.preference

import android.content.Context
import android.util.AttributeSet
import androidx.preference.PreferenceViewHolder
import com.tomy.lib.ui.R

/**@author Tomy
 * Created by Tomy on 2018/11/27.
 */
open class MasterTwoTargetPreference: TwoTargetPreference {

    private var mSummaryEnabled = false

    constructor(context: Context): super(context)
    constructor(context: Context, attrs: AttributeSet): super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int): super(context, attrs, defStyleAttr, defStyleRes)


    override fun getSecondTargetResId(): Int {
        return R.layout.preference_widget_go
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        /*if (!mSummaryEnabled) {
            holder.findViewById(android.R.id.title).apply {
                (layoutParams as ConstraintLayout.LayoutParams).bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            }
        }*/
    }

    override fun setWidgetEnabled(enabled: Boolean) {
        mItemView?.setBackgroundResource(if (enabled) R.drawable.bg_item_gray_line_click else R.drawable.bg_item_gray_line_pressed)
        mItemView?.isClickable = enabled
    }

}