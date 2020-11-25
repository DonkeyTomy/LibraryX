package com.tomy.lib.ui.view

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.widget.TextSwitcher
import android.widget.TextView
import android.widget.ViewSwitcher
import com.tomy.lib.ui.R
import timber.log.Timber
import java.lang.ref.WeakReference
import java.util.*

/**
 * @author Tomy
 * Created by Tomy on 25/11/2020.
 */
class VerticalScrollTextView @JvmOverloads constructor(
    private val mContext: Context,
    attrs: AttributeSet? = null
) : TextSwitcher(
    mContext, attrs
), ViewSwitcher.ViewFactory {
    private var mTextSize = 15f
    private var mPadding = 5
    private var textColor = Color.BLACK
    private var mMaxLines = 1 //默认最多一行
    private var mHandler: Handler? = null

    private var mStillTime = 0L

    private var mAnimationDuration = 0L

    init {
        mHandler = ScrollHandler(this, Looper.getMainLooper())
        mContext.obtainStyledAttributes(attrs, R.styleable.VerticalScrollTextView).apply {
            mStillTime = getInt(R.styleable.VerticalScrollTextView_textStillTime, 0).toLong()
            mMaxLines = getInt(R.styleable.VerticalScrollTextView_maxLines, 1)
            mAnimationDuration = getInt(R.styleable.VerticalScrollTextView_animationDuration, 0).toLong()
            textColor = getColor(R.styleable.VerticalScrollTextView_textColor, Color.BLACK)
            setAnimTime(mAnimationDuration)
            recycle()
        }
    }

    /**
     * @param textSize 字号
     * @param padding 内边距
     * @param textColor 字体颜色
     */
    fun setTextStyle(textSize: Float, padding: Int, textColor: Int) {
        mTextSize = textSize
        mPadding = padding
        this.textColor = textColor
    }

    /**
     * @param maxLines 最大行数
     */
    fun setMaxLines(maxLines: Int) {
        this.mMaxLines = maxLines
    }

    private var itemClickListener: OnItemClickListener? = null
    private var currentId = -1
    private val textList: ArrayList<String> = ArrayList()
    
    /**
     * 渐进渐出时间间隔
     * @param animDuration
     */
    fun setAnimTime(animDuration: Long) {
        mAnimationDuration = animDuration
        setFactory(this)
        setInAnimation(context, R.anim.vertical_scroll_in)
        setOutAnimation(context, R.anim.vertical_scroll_out)
    }

    private fun changeText() {
        if (textList.size > 0) {
            currentId++
            setText(textList[currentId % textList.size])
        }
    }

    /**
     * 设置文本
     * @param time
     */
    fun setTextStillTime(time: Long) {
        mStillTime = time
    }

    /**
     * 设置数据源
     * @param titles
     */
    fun setTextList(titles: List<String>?) {
        textList.clear()
        textList.addAll(titles!!)
        currentId = -1
    }

    fun autoDelayScroll() {
        mHandler?.sendEmptyMessageDelayed(FLAG_START_AUTO_SCROLL, mStillTime)
    }

    /**
     * 开始滚动
     */
    fun startAutoScroll() {
        mHandler?.sendEmptyMessage(FLAG_START_FIRST_SCROLL)
    }

    /**
     * 停止滚动
     */
    fun stopAutoScroll() {
        mHandler?.sendEmptyMessage(FLAG_STOP_AUTO_SCROLL)
    }

    override fun makeView(): View {
        Timber.d("makeView()")
        return TextView(mContext).apply {
            gravity = Gravity.START
            maxLines = maxLines
            setPadding(mPadding, mPadding, mPadding, mPadding)
            setTextColor(textColor)
            textSize = mTextSize
            isClickable = true
            setOnClickListener {
                if (itemClickListener != null && textList.size > 0 && currentId != -1) {
                    itemClickListener!!.onItemClick(currentId % textList.size)
                }
            }
        }
    }

    /**
     * 设置点击事件监听
     * @param itemClickListener
     */
    fun setOnItemClickListener(itemClickListener: OnItemClickListener?) {
        this.itemClickListener = itemClickListener
    }

    /**
     * 轮播文本点击监听器
     */
    interface OnItemClickListener {
        /**
         * 点击回调
         * @param position 当前点击ID
         */
        fun onItemClick(position: Int)
    }

    companion object {
        private const val FLAG_START_AUTO_SCROLL = 0
        private const val FLAG_STOP_AUTO_SCROLL = 1
        private const val FLAG_START_FIRST_SCROLL = 2 //第一次滚动不用间隔,后续滚动有间隔

        class ScrollHandler(scrollTextView: VerticalScrollTextView, looper: Looper): Handler(looper) {
            private val mScrollTextViewRef = WeakReference(scrollTextView)

            override fun handleMessage(msg: Message) {
                mScrollTextViewRef.get()?.apply {
                    when (msg.what) {
                        FLAG_START_AUTO_SCROLL -> {
                            changeText()
                            autoDelayScroll()
                        }
                        FLAG_STOP_AUTO_SCROLL -> removeMessages(FLAG_START_AUTO_SCROLL)

                        FLAG_START_FIRST_SCROLL -> sendEmptyMessageDelayed(FLAG_START_AUTO_SCROLL, 0)
                    }
                }
            }

        }
    }

}