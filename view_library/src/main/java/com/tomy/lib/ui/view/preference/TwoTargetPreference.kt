package com.tomy.lib.ui.view.preference

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.tomy.lib.ui.R

/**@author Tomy
 * Created by Tomy on 2018/11/20.
 */
open class TwoTargetPreference: Preference {

    constructor(context: Context): this(context, null)
    constructor(context: Context, attrs: AttributeSet?): super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): this(context, attrs, defStyleAttr, 0)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int): super(context, attrs, defStyleAttr, defStyleRes)

    protected var mListener: OnBindViewHolderListener? = null
    
    protected var mItemView: View? = null

    init {
        layoutResource = getLayoutResourceId()
        val secondTargetId = getSecondTargetResId()
        if (secondTargetId != 0) {
            widgetLayoutResource = secondTargetId
        }
    }

    fun setOnBindViewHolderListener(listener: OnBindViewHolderListener) {
        mListener = listener
    }

    open fun getLayoutResourceId() = R.layout.preference_two_target

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        mItemView = holder.itemView
        val widgetFrame = holder.findViewById(android.R.id.widget_frame)
        if (widgetFrame != null) {
            widgetFrame.visibility = if (shouldHideSecondTarget()) View.GONE else View.VISIBLE
        }
        super.onBindViewHolder(holder)
        mListener?.onBindViewHolder(holder)
    }

    fun shouldHideSecondTarget(): Boolean {
        return getSecondTargetResId() == 0
    }

    open fun getSecondTargetResId(): Int {
        return 0
    }

    interface OnBindViewHolderListener {
        fun onBindViewHolder(holder: PreferenceViewHolder)
    }


    open fun setWidgetEnabled(enabled: Boolean) {}
}