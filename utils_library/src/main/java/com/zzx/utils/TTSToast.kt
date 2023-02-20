package com.zzx.utils

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.coder.vincent.smart_toast.SmartToast
import com.zzx.utils.rxjava.FlowableUtil
import timber.log.Timber

/**@author Tomy
 * Created by Tomy on 2014/6/13.
 */
@SuppressLint("StaticFieldLeak")
object TTSToast {
    private val TAG = "TTSToast"
//    private var mToast: Toast? = null
    private var mTTS: TextToSpeech? = null

    private var mContext: Context? = null

    @JvmStatic
    fun init(context: Application) {
        Timber.v("init Toast()")
//        VincentLibDevTool.printDevLog = true
        mContext = context
        if (mTTS == null) {
            mTTS = TextToSpeech(context, TextToSpeech.OnInitListener { status ->
                if (status != TextToSpeech.SUCCESS) {
                    return@OnInitListener
                }
                val result = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    mTTS!!.setLanguage(context.resources.configuration.locales[0])
                } else {
                    mTTS!!.setLanguage(context.resources.configuration.locale)
                }
                if (result != TextToSpeech.LANG_NOT_SUPPORTED && result != TextToSpeech.LANG_MISSING_DATA) {
                    mTTS!!.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String) {

                        }

                        override fun onDone(utteranceId: String) {

                        }

                        override fun onError(utteranceId: String) {
                        }

                        override fun onError(utteranceId: String?, errorCode: Int) {
                            onError(utteranceId!!)
                            mTTS = null
                        }

                    })
                }
            })
        }
//        mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT)
    }

    @JvmStatic
    fun release() {
        mTTS?.stop()
        mTTS?.shutdown()
        mTTS = null
        mContext = null
//        mToast = null
    }

    @Deprecated("")
    @JvmStatic
    fun showToast(msg: String, needTTS: Boolean = false, show: Boolean = true, showTime: Int = Toast.LENGTH_SHORT) {
        if (show && showTime >= 0) {
            FlowableUtil.setMainThread {
                if (showTime == Toast.LENGTH_LONG) {
                    SmartToast.classic().showLong(msg)
                } else {
                    SmartToast.classic().show(msg)
                }
            }
        }
        try {
            if (needTTS) {
                mTTS!!.speak(msg, TextToSpeech.QUEUE_FLUSH, null, null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Deprecated("")
    @JvmStatic
    fun showToast(msgId: Int, needTTS: Boolean = false, show: Boolean = true, show_time: Int = Toast.LENGTH_SHORT) {
        showToast(mContext!!.getString(msgId), needTTS, show, show_time)
    }

    @JvmStatic
    @JvmOverloads
    fun showToast(msg: String, needTTS: Boolean = false, show: Boolean = true, shortTime: Boolean = true, type: Type = Type.NORMAL) {
        if (show) {
            FlowableUtil.setMainThread {
                if (shortTime) {
                    when (type) {
                        Type.NORMAL     -> SmartToast.classic().show(msg)
                        Type.INFO       -> SmartToast.emotion().info(msg)
                        Type.WARN       -> SmartToast.emotion().warning(msg)
                        Type.SUCCESS    -> SmartToast.emotion().success(msg)
                        Type.ERROR      -> SmartToast.emotion().error(msg)
                        Type.FAIL       -> SmartToast.emotion().fail(msg)
                        Type.FORBID     -> SmartToast.emotion().forbid(msg)
                        Type.WAITING    -> SmartToast.emotion().waiting(msg)
                        Type.COMPLETE   -> SmartToast.emotion().complete(msg)
                    }
                } else {
                    when (type) {
                        Type.NORMAL     -> SmartToast.classic().showLong(msg)
                        Type.INFO       -> SmartToast.emotion().infoLong(msg)
                        Type.WARN       -> SmartToast.emotion().warningLong(msg)
                        Type.SUCCESS    -> SmartToast.emotion().successLong(msg)
                        Type.ERROR      -> SmartToast.emotion().errorLong(msg)
                        Type.FAIL       -> SmartToast.emotion().failLong(msg)
                        Type.FORBID     -> SmartToast.emotion().forbidLong(msg)
                        Type.WAITING    -> SmartToast.emotion().waitingLong(msg)
                        Type.COMPLETE   -> SmartToast.emotion().completeLong(msg)
                    }
                }
            }
        }
        if (needTTS) {
            speakTTS(msg)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun showToast(msg: Int, needTTS: Boolean = false, show: Boolean = true, shortTime: Boolean = true, type: Type = Type.NORMAL) {
        if (show) {
            FlowableUtil.setMainThread {
                if (shortTime) {
                    when (type) {
                        Type.NORMAL     -> SmartToast.classic().show(msg)
                        Type.INFO       -> SmartToast.emotion().info(msg)
                        Type.WARN       -> SmartToast.emotion().warning(msg)
                        Type.SUCCESS    -> SmartToast.emotion().success(msg)
                        Type.ERROR      -> SmartToast.emotion().error(msg)
                        Type.FAIL       -> SmartToast.emotion().fail(msg)
                        Type.FORBID     -> SmartToast.emotion().forbid(msg)
                        Type.WAITING    -> SmartToast.emotion().waiting(msg)
                        Type.COMPLETE   -> SmartToast.emotion().complete(msg)
                    }
                } else {
                    when (type) {
                        Type.NORMAL     -> SmartToast.classic().showLong(msg)
                        Type.INFO       -> SmartToast.emotion().infoLong(msg)
                        Type.WARN       -> SmartToast.emotion().warningLong(msg)
                        Type.SUCCESS    -> SmartToast.emotion().successLong(msg)
                        Type.ERROR      -> SmartToast.emotion().errorLong(msg)
                        Type.FAIL       -> SmartToast.emotion().failLong(msg)
                        Type.FORBID     -> SmartToast.emotion().forbidLong(msg)
                        Type.WAITING    -> SmartToast.emotion().waitingLong(msg)
                        Type.COMPLETE   -> SmartToast.emotion().completeLong(msg)
                    }
                }
            }
        }
        if (needTTS) {
            speakTTS(msg)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun showMsg(msg: Int, needTTS: Boolean = false, shortTime: Boolean = true) {
        FlowableUtil.setMainThread {
            if (shortTime) {
                SmartToast.classic().show(msg)
            } else {
                SmartToast.classic().showLong(msg)
            }
        }
        if (needTTS) {
            speakTTS(msg)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun showInfo(msg: Int, needTTS: Boolean = false, shortTime: Boolean = true) {
        FlowableUtil.setMainThread {
            Timber.v("showInfo(): $shortTime")
            if (shortTime) {
                SmartToast.emotion().config().apply().info(msg)
            } else {
                SmartToast.emotion().infoLong(msg)
            }
        }
        if (needTTS) {
            speakTTS(msg)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun showWarn(msg: Int, needTTS: Boolean = false, shortTime: Boolean = true) {
        FlowableUtil.setMainThread {
            if (shortTime) {
                SmartToast.emotion().warning(msg)
            } else {
                SmartToast.emotion().warningLong(msg)
            }
        }
        if (needTTS) {
            speakTTS(msg)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun showSuccess(msg: Int, needTTS: Boolean = false, shortTime: Boolean = true) {
        FlowableUtil.setMainThread {
            Timber.v("showSuccess(): $shortTime")
            if (shortTime) {
                SmartToast.emotion().success(msg)
            } else {
                SmartToast.emotion().successLong(msg)
            }
        }
        if (needTTS) {
            speakTTS(msg)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun showFail(msg: Int, needTTS: Boolean = false, shortTime: Boolean = true) {
        FlowableUtil.setMainThread {
            if (shortTime) {
                SmartToast.emotion().fail(msg)
            } else {
                SmartToast.emotion().failLong(msg)
            }
        }
        if (needTTS) {
            speakTTS(msg)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun showError(msg: Int, needTTS: Boolean = false, shortTime: Boolean = true) {
        FlowableUtil.setMainThread {
            if (shortTime) {
                SmartToast.emotion().error(msg)
            } else {
                SmartToast.emotion().errorLong(msg)
            }
        }
        if (needTTS) {
            speakTTS(msg)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun showForbid(msg: Int, needTTS: Boolean = false, shortTime: Boolean = true) {
        FlowableUtil.setMainThread {
            if (shortTime) {
                SmartToast.emotion().forbid(msg)
            } else {
                SmartToast.emotion().forbidLong(msg)
            }
        }
        if (needTTS) {
            speakTTS(msg)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun showWait(msg: Int, needTTS: Boolean = false, shortTime: Boolean = true) {
        FlowableUtil.setMainThread {
            if (shortTime) {
                SmartToast.emotion().waiting(msg)
            } else {
                SmartToast.emotion().waitingLong(msg)
            }
        }
        if (needTTS) {
            speakTTS(msg)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun showComplete(msg: Int, needTTS: Boolean = false, shortTime: Boolean = true) {
        FlowableUtil.setMainThread {
            if (shortTime) {
                SmartToast.emotion().complete(msg)
            } else {
                SmartToast.emotion().completeLong(msg)
            }
        }
        if (needTTS) {
            speakTTS(msg)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun showMsg(msg: String, needTTS: Boolean = false, shortTime: Boolean = true) {
        FlowableUtil.setMainThread {
            if (shortTime) {
                SmartToast.classic().show(msg)
            } else {
                SmartToast.classic().showLong(msg)
            }
        }
        if (needTTS) {
            speakTTS(msg)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun showInfo(msg: String, needTTS: Boolean = false, shortTime: Boolean = true) {
        FlowableUtil.setMainThread {
            if (shortTime) {
                SmartToast.emotion().info(msg)
            } else {
                SmartToast.emotion().infoLong(msg)
            }
        }
        if (needTTS) {
            speakTTS(msg)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun showWarn(msg: String, needTTS: Boolean = false, shortTime: Boolean = true) {
        FlowableUtil.setMainThread {
            if (shortTime) {
                SmartToast.emotion().warning(msg)
            } else {
                SmartToast.emotion().warningLong(msg)
            }
        }
        if (needTTS) {
            speakTTS(msg)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun showSuccess(msg: String, needTTS: Boolean = false, shortTime: Boolean = true) {
        FlowableUtil.setMainThread {
            if (shortTime) {
                SmartToast.emotion().success(msg)
            } else {
                SmartToast.emotion().successLong(msg)
            }
        }
        if (needTTS) {
            speakTTS(msg)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun showFail(msg: String, needTTS: Boolean = false, shortTime: Boolean = true) {
        FlowableUtil.setMainThread {
            if (shortTime) {
                SmartToast.emotion().fail(msg)
            } else {
                SmartToast.emotion().failLong(msg)
            }
        }
        if (needTTS) {
            speakTTS(msg)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun showError(msg: String, needTTS: Boolean = false, shortTime: Boolean = true) {
        FlowableUtil.setMainThread {
            if (shortTime) {
                SmartToast.emotion().error(msg)
            } else {
                SmartToast.emotion().errorLong(msg)
            }
        }
        if (needTTS) {
            speakTTS(msg)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun showForbid(msg: String, needTTS: Boolean = false, shortTime: Boolean = true) {
        FlowableUtil.setMainThread {
            if (shortTime) {
                SmartToast.emotion().forbid(msg)
            } else {
                SmartToast.emotion().forbidLong(msg)
            }
        }
        if (needTTS) {
            speakTTS(msg)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun showWait(msg: String, needTTS: Boolean = false, shortTime: Boolean = true) {
        FlowableUtil.setMainThread {
            if (shortTime) {
                SmartToast.emotion().waiting(msg)
            } else {
                SmartToast.emotion().waitingLong(msg)
            }
        }
        if (needTTS) {
            speakTTS(msg)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun showComplete(msg: String, needTTS: Boolean = false, shortTime: Boolean = true) {
        FlowableUtil.setMainThread {
            if (shortTime) {
                SmartToast.emotion().complete(msg)
            } else {
                SmartToast.emotion().completeLong(msg)
            }
        }
        if (needTTS) {
            speakTTS(msg)
        }
    }

    @JvmStatic
    fun speakTTS(msgId: Int) {
        speakTTS(mContext!!.getString(msgId))
    }

    @JvmStatic
    fun speakTTS(msg: String) {
        try {
            Timber.v("speakTTS: $msg")
            mTTS!!.speak(msg, TextToSpeech.QUEUE_FLUSH, null, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    @JvmStatic
    @JvmOverloads
    fun Fragment.showToast(msg: String, needTTS: Boolean = false, show: Boolean = true, shortTime: Boolean = true, type: Type = Type.NORMAL) {
        this@TTSToast.showToast(msg, needTTS, show, shortTime, type)
    }

    @JvmStatic
    @JvmOverloads
    fun Fragment.showToast(msg: Int, needTTS: Boolean = false, show: Boolean = true, shortTime: Boolean = true, type: Type = Type.NORMAL) {
        this@TTSToast.showToast(msg, needTTS, show, shortTime, type)
    }

    @JvmStatic
    @JvmOverloads
    fun Fragment.showMsg(msg: Int, needTTS: Boolean = false, shortTime: Boolean = true) {
        this@TTSToast.showMsg(msg, needTTS, shortTime)
    }

    @JvmStatic
    @JvmOverloads
    fun Fragment.showInfo(msg: Int, needTTS: Boolean = false, shortTime: Boolean = true) {
        this@TTSToast.showInfo(msg, needTTS, shortTime)
    }

    @JvmStatic
    @JvmOverloads
    fun Fragment.showWarn(msg: Int, needTTS: Boolean = false, shortTime: Boolean = true) {
        this@TTSToast.showWarn(msg, needTTS, shortTime)
    }

    @JvmStatic
    @JvmOverloads
    fun Fragment.showSuccess(msg: Int, needTTS: Boolean = false, shortTime: Boolean = true) {
        this@TTSToast.showSuccess(msg, needTTS, shortTime)
    }

    @JvmStatic
    @JvmOverloads
    fun Fragment.showFail(msg: Int, needTTS: Boolean = false, shortTime: Boolean = true) {
        this@TTSToast.showFail(msg, needTTS, shortTime)
    }

    @JvmStatic
    @JvmOverloads
    fun Fragment.showError(msg: Int, needTTS: Boolean = false, shortTime: Boolean = true) {
        this@TTSToast.showError(msg, needTTS, shortTime)
    }

    @JvmStatic
    @JvmOverloads
    fun Fragment.showForbid(msg: Int, needTTS: Boolean = false, shortTime: Boolean = true) {
        this@TTSToast.showForbid(msg, needTTS, shortTime)
    }

    @JvmStatic
    @JvmOverloads
    fun Fragment.showWait(msg: Int, needTTS: Boolean = false, shortTime: Boolean = true) {
        this@TTSToast.showWait(msg, needTTS, shortTime)
    }

    @JvmStatic
    @JvmOverloads
    fun Fragment.showComplete(msg: Int, needTTS: Boolean = false, shortTime: Boolean = true) {
        this@TTSToast.showComplete(msg, needTTS, shortTime)
    }

    @JvmStatic
    @JvmOverloads
    fun Fragment.showMsg(msg: String, needTTS: Boolean = false, shortTime: Boolean = true) {
        this@TTSToast.showMsg(msg, needTTS, shortTime)
    }

    @JvmStatic
    @JvmOverloads
    fun Fragment.showInfo(msg: String, needTTS: Boolean = false, shortTime: Boolean = true) {
        this@TTSToast.showInfo(msg, needTTS, shortTime)
    }

    @JvmStatic
    @JvmOverloads
    fun Fragment.showWarn(msg: String, needTTS: Boolean = false, shortTime: Boolean = true) {
        this@TTSToast.showWarn(msg, needTTS, shortTime)
    }

    @JvmStatic
    @JvmOverloads
    fun Fragment.showSuccess(msg: String, needTTS: Boolean = false, shortTime: Boolean = true) {
        this@TTSToast.showSuccess(msg, needTTS, shortTime)
    }

    @JvmStatic
    @JvmOverloads
    fun Fragment.showFail(msg: String, needTTS: Boolean = false, shortTime: Boolean = true) {
        this@TTSToast.showFail(msg, needTTS, shortTime)
    }

    @JvmStatic
    @JvmOverloads
    fun Fragment.showError(msg: String, needTTS: Boolean = false, shortTime: Boolean = true) {
        this@TTSToast.showError(msg, needTTS, shortTime)
    }

    @JvmStatic
    @JvmOverloads
    fun Fragment.showForbid(msg: String, needTTS: Boolean = false, shortTime: Boolean = true) {
        this@TTSToast.showForbid(msg, needTTS, shortTime)
    }

    @JvmStatic
    @JvmOverloads
    fun Fragment.showWait(msg: String, needTTS: Boolean = false, shortTime: Boolean = true) {
        this@TTSToast.showWait(msg, needTTS, shortTime)
    }

    @JvmStatic
    @JvmOverloads
    fun Fragment.showComplete(msg: String, needTTS: Boolean = false, shortTime: Boolean = true) {
        this@TTSToast.showComplete(msg, needTTS, shortTime)
    }

    @JvmStatic
    fun Fragment.speakTTS(msgId: Int) {
        this@TTSToast.speakTTS(msgId)
    }

    @JvmStatic
    fun Fragment.speakTTS(msg: String) {
        this@TTSToast.speakTTS(msg)
    }




    @JvmStatic
    @JvmOverloads
    fun Context.showToast(msg: String, needTTS: Boolean = false, show: Boolean = true, shortTime: Boolean = true, type: Type = Type.NORMAL) {
        this@TTSToast.showToast(msg, needTTS, show, shortTime, type)
    }

    @JvmStatic
    @JvmOverloads
    fun Context.showToast(msg: Int, needTTS: Boolean = false, show: Boolean = true, shortTime: Boolean = true, type: Type = Type.NORMAL) {
        this@TTSToast.showToast(msg, needTTS, show, shortTime, type)
    }

    @JvmStatic
    @JvmOverloads
    fun Context.showMsg(msg: Int, needTTS: Boolean = false, shortTime: Boolean = true) {
        this@TTSToast.showMsg(msg, needTTS, shortTime)
    }

    @JvmStatic
    @JvmOverloads
    fun Context.showInfo(msg: Int, needTTS: Boolean = false, shortTime: Boolean = true) {
        this@TTSToast.showInfo(msg, needTTS, shortTime)
    }

    @JvmStatic
    @JvmOverloads
    fun Context.showWarn(msg: Int, needTTS: Boolean = false, shortTime: Boolean = true) {
        this@TTSToast.showWarn(msg, needTTS, shortTime)
    }

    @JvmStatic
    @JvmOverloads
    fun Context.showSuccess(msg: Int, needTTS: Boolean = false, shortTime: Boolean = true) {
        this@TTSToast.showSuccess(msg, needTTS, shortTime)
    }

    @JvmStatic
    @JvmOverloads
    fun Context.showFail(msg: Int, needTTS: Boolean = false, shortTime: Boolean = true) {
        this@TTSToast.showFail(msg, needTTS, shortTime)
    }

    @JvmStatic
    @JvmOverloads
    fun Context.showError(msg: Int, needTTS: Boolean = false, shortTime: Boolean = true) {
        this@TTSToast.showError(msg, needTTS, shortTime)
    }

    @JvmStatic
    @JvmOverloads
    fun Context.showForbid(msg: Int, needTTS: Boolean = false, shortTime: Boolean = true) {
        this@TTSToast.showForbid(msg, needTTS, shortTime)
    }

    @JvmStatic
    @JvmOverloads
    fun Context.showWait(msg: Int, needTTS: Boolean = false, shortTime: Boolean = true) {
        this@TTSToast.showWait(msg, needTTS, shortTime)
    }

    @JvmStatic
    @JvmOverloads
    fun Context.showComplete(msg: Int, needTTS: Boolean = false, shortTime: Boolean = true) {
        this@TTSToast.showComplete(msg, needTTS, shortTime)
    }

    @JvmStatic
    @JvmOverloads
    fun Context.showMsg(msg: String, needTTS: Boolean = false, shortTime: Boolean = true) {
        this@TTSToast.showMsg(msg, needTTS, shortTime)
    }

    @JvmStatic
    @JvmOverloads
    fun Context.showInfo(msg: String, needTTS: Boolean = false, shortTime: Boolean = true) {
        this@TTSToast.showInfo(msg, needTTS, shortTime)
    }

    @JvmStatic
    @JvmOverloads
    fun Context.showWarn(msg: String, needTTS: Boolean = false, shortTime: Boolean = true) {
        this@TTSToast.showWarn(msg, needTTS, shortTime)
    }

    @JvmStatic
    @JvmOverloads
    fun Context.showSuccess(msg: String, needTTS: Boolean = false, shortTime: Boolean = true) {
        this@TTSToast.showSuccess(msg, needTTS, shortTime)
    }

    @JvmStatic
    @JvmOverloads
    fun Context.showFail(msg: String, needTTS: Boolean = false, shortTime: Boolean = true) {
        this@TTSToast.showFail(msg, needTTS, shortTime)
    }

    @JvmStatic
    @JvmOverloads
    fun Context.showError(msg: String, needTTS: Boolean = false, shortTime: Boolean = true) {
        this@TTSToast.showError(msg, needTTS, shortTime)
    }

    @JvmStatic
    @JvmOverloads
    fun Context.showForbid(msg: String, needTTS: Boolean = false, shortTime: Boolean = true) {
        this@TTSToast.showForbid(msg, needTTS, shortTime)
    }

    @JvmStatic
    @JvmOverloads
    fun Context.showWait(msg: String, needTTS: Boolean = false, shortTime: Boolean = true) {
        this@TTSToast.showWait(msg, needTTS, shortTime)
    }

    @JvmStatic
    @JvmOverloads
    fun Context.showComplete(msg: String, needTTS: Boolean = false, shortTime: Boolean = true) {
        this@TTSToast.showComplete(msg, needTTS, shortTime)
    }

    @JvmStatic
    fun Context.speakTTS(msgId: Int) {
        this@TTSToast.speakTTS(msgId)
    }

    @JvmStatic
    fun Context.speakTTS(msg: String) {
        this@TTSToast.speakTTS(msg)
    }

    enum class Type {
        NORMAL,
        //信息
        INFO,
        //感叹号警告
        WARN,
        //成功:√
        SUCCESS,
        //错误:×
        ERROR,
        //失败:不开心表情
        FAIL,
        //禁止
        FORBID,
        //等待
        WAITING,
        //完成
        COMPLETE
    }

}
