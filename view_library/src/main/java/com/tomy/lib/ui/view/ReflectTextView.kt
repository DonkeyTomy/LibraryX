package com.tomy.lib.ui.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.ViewCompat
import com.tomy.lib.ui.R
import kotlin.math.ceil
import kotlin.math.roundToInt


/**
 * 基于 http://www.cnblogs.com/shang53880/p/3549513.html 修改
 *
 *
 * 带倒影效果的文本
 * Created by dean on 2017/8/23.
 */
class ReflectTextView constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) :
    AppCompatTextView(context, attrs, defStyleAttr) {

    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)


    private var mMatrix: Matrix? = null
    private var mPaint: Paint? = null
    private fun init() {
        mMatrix = Matrix()
        mMatrix!!.preScale(1f, -1f)
        //这句是关闭硬件加速，启用软件加速，如果报相关错误可以尝试注释这句代码，反正楼主注释掉这句话是启动不起来
        ViewCompat.setLayerType(this, View.LAYER_TYPE_SOFTWARE, null)
        isDrawingCacheEnabled = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val temp = (measuredHeight - (lineHeight - textSize) / 2).toInt()
        OFF_Y = temp - temp * REFLECT_HEIGHT_MULTIPLE
        setMeasuredDimension(
            measuredWidth,
            (temp * 2 - OFF_Y).roundToInt() + SPACING_VALUE
        )
    }

    private val mReflectPaint by lazy { Paint().apply { alpha = REFLECT_ALPHA } }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val height: Int = measuredHeight
        val width: Int = measuredWidth
        val originalImage: Bitmap = drawingCache
        val reflectionImage = Bitmap.createBitmap(
            originalImage, 0,
            0, width.coerceAtMost(originalImage.width), height, mMatrix, false
        )
        canvas.drawBitmap(reflectionImage, 0f, OFF_Y, mReflectPaint)
        if (mPaint == null) {
            mPaint = Paint()
            //阴影的效果可以自己根据需要设定
            val shader = LinearGradient(
                0f, (height + OFF_Y) / 2, 0f,
                height.toFloat(), -0x1, 0x00ffffff, Shader.TileMode.CLAMP
            )
            mPaint!!.shader = shader
            mPaint!!.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        }
        canvas.drawRect(0f, (height + OFF_Y) / 2, width.toFloat(), height.toFloat(), mPaint!!)
    }

    override fun onTextChanged(
        text: CharSequence?, start: Int,
        lengthBefore: Int, lengthAfter: Int
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        buildDrawingCache()
        postInvalidate()
        //每次更新TextView后遗留上次的残影，所以在这里每次刷新TextView后清楚DrawingCache
        destroyDrawingCache()
    }

    private val fontHeight: Float
        private get() {
            val paint = Paint()
            paint.textSize = textSize
            val fm = paint.fontMetrics
            return ceil(fm.descent - fm.ascent.toDouble()).toFloat()
        }

    companion object {
        private var REFLECT_ALPHA //倒影透明度
                = 70
        private var REFLECT_HEIGHT_MULTIPLE //倒影的高度倍数
                = 0.6f
        private var SPACING_VALUE //实体文字与倒影之间的空隙
                = 0
        private var OFF_Y //Y轴偏移，由于倒影的高度倍数设置小于1时就会出现偏移，显示部分倒影
                = 0f

        /**
         * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
         */
        fun dip2px(context: Context, dpValue: Float): Int {
            val scale = context.resources.displayMetrics.density
            return (dpValue * scale + 0.5f).toInt()
        }

        /**
         * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
         */
        fun px2dip(context: Context, pxValue: Float): Int {
            val scale = context.resources.displayMetrics.density
            return (pxValue / scale + 0.5f).toInt()
        }
    }

    init {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.reflect, defStyleAttr, 0)
        val n = a.indexCount
        for (i in 0 until n) {
            val attr = a.getIndex(i)
            if (attr == R.styleable.reflect_reflectAlpha) {
                //倒影透明度[1-255]
                REFLECT_ALPHA = a.getInteger(attr, REFLECT_ALPHA) //默认100
                if (REFLECT_ALPHA < 1) REFLECT_ALPHA =
                    1 else if (REFLECT_ALPHA > 255) REFLECT_ALPHA = 255
            } else if (attr == R.styleable.reflect_reflectHeightMultiple) {
                //倒影的高度倍数[0-1]
                REFLECT_HEIGHT_MULTIPLE = a.getFloat(attr, 1f) //默认1
                if (REFLECT_HEIGHT_MULTIPLE < 0) REFLECT_HEIGHT_MULTIPLE =
                    0f else if (REFLECT_HEIGHT_MULTIPLE > 1) REFLECT_HEIGHT_MULTIPLE = 1f
            } else if (attr == R.styleable.reflect_spacingValue) {
                //实体文字与倒影之间的空隙 默认10dp
                SPACING_VALUE = a.getDimensionPixelSize(
                    attr,
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        0f,
                        resources.displayMetrics
                    )
                        .toInt()
                )
            }
        }
        a.recycle()
        init()
    }
}