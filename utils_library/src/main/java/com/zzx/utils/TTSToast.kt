package com.zzx.utils

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.widget.Toast
import com.coder.zzq.smartshow.core.SmartShow
import com.coder.zzq.smartshow.toast.SmartToast
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
        SmartShow.init(context)
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
                    SmartToast.showLong(msg)
                } else {
                    SmartToast.show(msg)
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
                        Type.NORMAL -> SmartToast.show(msg)
                        Type.INFO -> SmartToast.info(msg)
                        Type.WARN -> SmartToast.warning(msg)
                        Type.SUCCESS -> SmartToast.success(msg)
                        Type.ERROR -> SmartToast.error(msg)
                        Type.FAIL -> SmartToast.fail(msg)
                        Type.FORBID -> SmartToast.forbid(msg)
                        Type.WAITING -> SmartToast.waiting(msg)
                        Type.COMPLETE -> SmartToast.complete(msg)
                    }
                } else {
                    when (type) {
                        Type.NORMAL -> SmartToast.showLong(msg)
                        Type.INFO -> SmartToast.infoLong(msg)
                        Type.WARN -> SmartToast.warningLong(msg)
                        Type.SUCCESS -> SmartToast.successLong(msg)
                        Type.ERROR -> SmartToast.errorLong(msg)
                        Type.FAIL -> SmartToast.failLong(msg)
                        Type.FORBID -> SmartToast.forbidLong(msg)
                        Type.WAITING -> SmartToast.waitingLong(msg)
                        Type.COMPLETE -> SmartToast.completeLong(msg)
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
                        Type.NORMAL -> SmartToast.show(msg)
                        Type.INFO -> SmartToast.info(msg)
                        Type.WARN -> SmartToast.warning(msg)
                        Type.SUCCESS -> SmartToast.success(msg)
                        Type.ERROR -> SmartToast.error(msg)
                        Type.FAIL -> SmartToast.fail(msg)
                        Type.FORBID -> SmartToast.forbid(msg)
                        Type.WAITING -> SmartToast.waiting(msg)
                        Type.COMPLETE -> SmartToast.complete(msg)
                    }
                } else {
                    when (type) {
                        Type.NORMAL -> SmartToast.showLong(msg)
                        Type.INFO -> SmartToast.infoLong(msg)
                        Type.WARN -> SmartToast.warningLong(msg)
                        Type.SUCCESS -> SmartToast.successLong(msg)
                        Type.ERROR -> SmartToast.errorLong(msg)
                        Type.FAIL -> SmartToast.failLong(msg)
                        Type.FORBID -> SmartToast.forbidLong(msg)
                        Type.WAITING -> SmartToast.waitingLong(msg)
                        Type.COMPLETE -> SmartToast.completeLong(msg)
                    }
                }
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
