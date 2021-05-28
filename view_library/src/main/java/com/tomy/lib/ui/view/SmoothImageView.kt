package com.tomy.lib.ui.view

import android.animation.Animator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.load.resource.gif.GifDrawable
import timber.log.Timber
import java.lang.reflect.Field

/**
 * Author: liuk
 * Created at: 15/12/15
 */
class SmoothImageView : AppCompatImageView {
    private var mOriginalWidth = 0
    private var mOriginalHeight = 0
    private var mOriginalLocationX = 0
    private var mOriginalLocationY = 0
    private var mState = STATE_NORMAL
    private var mSmoothMatrix: Matrix? = null
    private var mBitmap: Bitmap? = null
    private var mTransformStart = false
    private var mTransform: Transform? = null
    private val mBgColor = -0x1000000
    private var mBgAlpha = 0
    private var mPaint: Paint? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }

    private fun init() {
        mSmoothMatrix = Matrix()
        mPaint = Paint()
        mPaint!!.color = mBgColor
        mPaint!!.style = Paint.Style.FILL
    }

    fun setOriginalInfo(width: Int, height: Int, locationX: Int, locationY: Int) {
        mOriginalWidth = width
        mOriginalHeight = height
        mOriginalLocationX = locationX
        mOriginalLocationY = locationY
        mOriginalLocationY -= getStatusBarHeight(context)
    }

    fun transformIn() {
        mState = STATE_TRANSFORM_IN
        mTransformStart = true
        invalidate()
    }

    fun transformOut() {
        mState = STATE_TRANSFORM_OUT
        mTransformStart = true
        invalidate()
    }

    private inner class Transform {
        var startScale = 0f
        var endScale = 0f
        var scale = 0f
        var startRect: LocationSizeF? = null
        var endRect: LocationSizeF? = null
        var rect: LocationSizeF? = null

        fun initStartIn() {
            scale = startScale
            try {
                rect = startRect!!.clone() as LocationSizeF
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun initStartOut() {
            scale = endScale
            try {
                rect = endRect!!.clone() as LocationSizeF
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun initTransform() {
        if (drawable == null) {
            return
        }
        if (drawable is ColorDrawable)
            return
        if (mBitmap == null || mBitmap!!.isRecycled) {
            mBitmap = when (drawable) {
                is BitmapDrawable -> {
                    (drawable as BitmapDrawable).bitmap
                }
                is GifDrawable -> {
                    (drawable as GifDrawable).firstFrame
                }
                else -> {
                    return
                }
            }
        }
        if (mBitmap == null) {
            return
        }
        if (mTransform != null) {
            return
        }
        if (width == 0 || height == 0) {
            return
        }
        mTransform = Transform()
        val xSScale = mOriginalWidth / mBitmap!!.width.toFloat()
        val ySScale = mOriginalHeight / mBitmap!!.height.toFloat()
        val startScale = if (xSScale > ySScale) xSScale else ySScale
        mTransform!!.startScale = startScale
        val xEScale = width / mBitmap!!.width.toFloat()
        val yEScale = height / mBitmap!!.height.toFloat()
        val endScale = if (xEScale < yEScale) xEScale else yEScale
        mTransform!!.endScale = endScale
        mTransform!!.startRect = LocationSizeF()
        mTransform!!.startRect!!.left = mOriginalLocationX.toFloat()
        mTransform!!.startRect!!.top = mOriginalLocationY.toFloat()
        mTransform!!.startRect!!.width = mOriginalWidth.toFloat()
        mTransform!!.startRect!!.height = mOriginalHeight.toFloat()
        mTransform!!.endRect = LocationSizeF()
        val bitmapEndWidth = mBitmap!!.width * mTransform!!.endScale
        val bitmapEndHeight = mBitmap!!.height * mTransform!!.endScale
        mTransform!!.endRect!!.left = (width - bitmapEndWidth) / 2
        mTransform!!.endRect!!.top = (height - bitmapEndHeight) / 2
        mTransform!!.endRect!!.width = bitmapEndWidth
        mTransform!!.endRect!!.height = bitmapEndHeight
        mTransform!!.rect = LocationSizeF()
    }

    private inner class LocationSizeF : Cloneable {
        var left = 0f
        var top = 0f
        var width = 0f
        var height = 0f
        override fun toString(): String {
            return "[left:$left top:$top width:$width height:$height]"
        }

        @Throws(CloneNotSupportedException::class)
        public override fun clone(): Any {
            return super.clone()
        }
    }

    fun getBmpMatrix() {
        if (drawable == null) {
            return
        }
        if (mTransform == null) {
            return
        }
        if (mBitmap == null || mBitmap!!.isRecycled) {
            mBitmap = (drawable as BitmapDrawable).bitmap
        }
        mSmoothMatrix!!.setScale(mTransform!!.scale, mTransform!!.scale)
        mSmoothMatrix!!.postTranslate(
            -(mTransform!!.scale * mBitmap!!.width / 2 - mTransform!!.rect!!.width / 2), -(mTransform!!.scale * mBitmap!!.height / 2 - mTransform!!.rect!!.height / 2)
        )
    }


    override fun onDraw(canvas: Canvas) {
        if (drawable == null) {
            return
        }
        if (mState == STATE_TRANSFORM_IN || mState == STATE_TRANSFORM_OUT) {
            if (mTransformStart) {
                initTransform()
            }
            if (mTransform == null) {
                super.onDraw(canvas)
                return
            }
            if (mTransformStart) {
                if (mState == STATE_TRANSFORM_IN) {
                    mTransform!!.initStartIn()
                } else {
                    mTransform!!.initStartOut()
                }
            }
            if (mTransformStart) {
                Timber.d("mTransform.startScale:%s", mTransform!!.startScale)
                Timber.d("mTransform.startScale:%s", mTransform!!.endScale)
                Timber.d("mTransform.scale:%s", mTransform!!.scale)
                Timber.d("mTransform.startRect:%s", mTransform!!.startRect.toString())
                Timber.d("mTransform.endRect:%s", mTransform!!.endRect.toString())
                Timber.d("mTransform.rect:%s", mTransform!!.rect.toString())
            }
            mPaint!!.alpha = mBgAlpha
            canvas.drawPaint(mPaint)
            val saveCount = canvas.saveCount
            canvas.save()
            getBmpMatrix()
            canvas.translate(mTransform!!.rect!!.left, mTransform!!.rect!!.top)
            canvas.clipRect(0f, 0f, mTransform!!.rect!!.width, mTransform!!.rect!!.height)
            canvas.concat(mSmoothMatrix)
            drawable.draw(canvas)
            canvas.restoreToCount(saveCount)
            if (mTransformStart) {
                mTransformStart = false
                startTransform(mState)
            }
        } else {
            mPaint!!.alpha = 255
            canvas.drawPaint(mPaint)
            super.onDraw(canvas)
        }
    }

    private fun startTransform(state: Int) {
        if (mTransform == null) {
            return
        }
        val valueAnimator = ValueAnimator()
        valueAnimator.duration = 300
        valueAnimator.interpolator = AccelerateDecelerateInterpolator()
        if (state == STATE_TRANSFORM_IN) {
            val scaleHolder =
                PropertyValuesHolder.ofFloat("scale", mTransform!!.startScale, mTransform!!.endScale)
            val leftHolder =
                PropertyValuesHolder.ofFloat("left", mTransform!!.startRect!!.left, mTransform!!.endRect!!.left)
            val topHolder =
                PropertyValuesHolder.ofFloat("top", mTransform!!.startRect!!.top, mTransform!!.endRect!!.top)
            val widthHolder =
                PropertyValuesHolder.ofFloat("width", mTransform!!.startRect!!.width, mTransform!!.endRect!!.width)
            val heightHolder =
                PropertyValuesHolder.ofFloat("height", mTransform!!.startRect!!.height, mTransform!!.endRect!!.height)
            val alphaHolder = PropertyValuesHolder.ofInt("alpha", 0, 255)
            valueAnimator.setValues(scaleHolder, leftHolder, topHolder, widthHolder, heightHolder, alphaHolder)
        } else {
            val scaleHolder =
                PropertyValuesHolder.ofFloat("scale", mTransform!!.endScale, mTransform!!.startScale)
            val leftHolder =
                PropertyValuesHolder.ofFloat("left", mTransform!!.endRect!!.left, mTransform!!.startRect!!.left)
            val topHolder =
                PropertyValuesHolder.ofFloat("top", mTransform!!.endRect!!.top, mTransform!!.startRect!!.top)
            val widthHolder =
                PropertyValuesHolder.ofFloat("width", mTransform!!.endRect!!.width, mTransform!!.startRect!!.width)
            val heightHolder =
                PropertyValuesHolder.ofFloat("height", mTransform!!.endRect!!.height, mTransform!!.startRect!!.height)
            val alphaHolder = PropertyValuesHolder.ofInt("alpha", 255, 0)
            valueAnimator.setValues(scaleHolder, leftHolder, topHolder, widthHolder, heightHolder, alphaHolder)
        }
        valueAnimator.addUpdateListener { animation ->
            mTransform!!.scale = animation.getAnimatedValue("scale") as Float
            mTransform!!.rect!!.left = animation.getAnimatedValue("left") as Float
            mTransform!!.rect!!.top = animation.getAnimatedValue("top") as Float
            mTransform!!.rect!!.width = animation.getAnimatedValue("width") as Float
            mTransform!!.rect!!.height = animation.getAnimatedValue("height") as Float
            mBgAlpha = animation.getAnimatedValue("alpha") as Int
            invalidate()
            (context as Activity).window.decorView.invalidate()
        }
        valueAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                if (state == STATE_TRANSFORM_IN) {
                    mState = STATE_NORMAL
                }
                if (mTransformListener != null) {
                    mTransformListener!!.onTransformComplete(state)
                }
            }

            override fun onAnimationCancel(animation: Animator) {}
        })
        valueAnimator.start()
    }

    fun setOnTransformListener(listener: TransformListener?) {
        mTransformListener = listener
    }

    private var mTransformListener: TransformListener? = null

    interface TransformListener {
        //mode STATE_TRANSFORM_IN 1 ,STATE_TRANSFORM_OUT 2
        fun onTransformComplete(mode: Int) // mode 1
    }

    companion object {
        private const val STATE_NORMAL = 0
        private const val STATE_TRANSFORM_IN = 1
        private const val STATE_TRANSFORM_OUT = 2
        fun getStatusBarHeight(context: Context): Int {
            val c: Class<*>
            var obj: Any? = null
            var field: Field? = null
            var x = 0
            var statusBarHeight = 0
            try {
                c = Class.forName("com.android.internal.R\$dimen")
                obj = c.newInstance()
                field = c.getField("status_bar_height")
                x = field[obj].toString().toInt()
                statusBarHeight = context.resources.getDimensionPixelSize(x)
                return statusBarHeight
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return statusBarHeight
        }
    }

    data class ImageConfig(
        val imagePath: String,
        val position: Int   = 0,
        val locationX: Int  = 0,
        val locationY: Int  = 0,
        val width: Int  = 0,
        val height: Int = 0
    ): Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt()
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(imagePath)
            parcel.writeInt(position)
            parcel.writeInt(locationX)
            parcel.writeInt(locationY)
            parcel.writeInt(width)
            parcel.writeInt(height)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<ImageConfig> {
            override fun createFromParcel(parcel: Parcel): ImageConfig {
                return ImageConfig(parcel)
            }

            override fun newArray(size: Int): Array<ImageConfig?> {
                return arrayOfNulls(size)
            }
        }

    }

}