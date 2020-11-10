package com.tomy.lib.ui.manager

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.zzx.utils.system.PermissionChecker
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

/**@author Tomy
 * Created by Tomy on 2018/6/6.
 */
class FloatWinManager(private var mContext: Context, var mRootView: View, private var mWinType: Int = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY) {

    private lateinit var mWindowManager: WindowManager

    private var mParameter: WindowManager.LayoutParams? = null

    private var mShowed: AtomicBoolean = AtomicBoolean(false)

    private var mMax = false

    private var mWidth  = 0
    private var mHeight = 0

    private var mDismissListener: OnDismissListener? = null

    private var mDisplayWidth = 0

    private var mDisplayHeight = 0

    init {
        initWindowManager()
    }

    private fun initWindowManager() {
        mWindowManager  = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        if (PermissionChecker.checkSystemAlertDialog(mContext)) {
            initParameters()
        }
        val point = Point()
        mWindowManager.defaultDisplay.getSize(point)
        mDisplayWidth   = point.x
        mDisplayHeight  = point.y
    }

    private fun initParameters() {
        mParameter      = WindowManager.LayoutParams()
        mParameter?.apply {
            x = 0
            y = 0
            gravity = Gravity.START or Gravity.TOP
            format = PixelFormat.RGBA_8888
            flags = flags.or(WindowManager.LayoutParams.FLAG_FULLSCREEN) or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
//        mParameter.flags = mParameter.flags.or(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN).and(WindowManager.LayoutParams.FLAG_FULLSCREEN.inv())
            Timber.e("flags = $flags")
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mWinType
            } else {
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
            }
        }
    }

    fun setSize(width: Int, height: Int) {
        mWidth  = width
        mHeight = height
    }

    fun showFloatWindow() {
        Timber.w("showFloatWindow()")
        mParameter?.apply {
            x = 0
            y = 0
            alpha = 1f
            flags = flags.and(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE.inv())
            updateView(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        }
    }

    fun showAt(x: Int, y: Int, width: Int, height: Int) {
//        Timber.w("showAt. (${x}x$y). [${width}x$height]")
        mParameter?.apply {
            this.x = x
            this.y = y
//            flags = flags.and(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE.inv())
            updateView(mDisplayWidth, height)
        }
    }

    fun setOnWindowDismissListener(listener: OnDismissListener) {
        mDismissListener = listener
    }

    /**
     * @see showFloatWindow
     * */
    fun dismissWindow() {
        mParameter?.apply {
            x = -mDisplayHeight
            alpha = 1f
            flags = flags.or(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            updateView(mDisplayWidth, mHeight)
            mDismissListener?.onWindowDismiss()
        }

    }

    fun removeFloatWindow() {
        Observable.just(Unit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mMax = false
                    if (mShowed.get()) {
                        mWindowManager.removeView(mRootView)
                        mShowed.set(false)
                    }
                }, {
                    it.printStackTrace()
                }
                )
    }

    private fun updateView(width: Int, height: Int) {
        Timber.w("updateView. width x height = [$width x $height]")
        setResolutionRation(width, height)
        /*Observable.just(Unit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {*/
//                    Timber.w("updateView. mShowed = ${mShowed.get()}")
                    if (!mShowed.get()) {
                        mWindowManager.addView(mRootView, mParameter)
                        mShowed.set(true)
                    } else {
                        mWindowManager.updateViewLayout(mRootView, mParameter)
                    }
//                }
    }

    private fun setResolutionRation(width: Int, height: Int) {
        mParameter?.width    = width
        mParameter?.height   = height
    }

    private fun setKeepScreenOn(keep: Boolean) {
        if (keep) {
            mParameter?.flags?.apply {
                or(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        } else {
            mParameter?.flags?.apply {
                and(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON.inv())
            }
        }
    }

    interface OnDismissListener {
        fun onWindowDismiss()
    }

}