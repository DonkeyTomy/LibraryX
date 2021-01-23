package com.zzx.utils

import android.app.Application
import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.widget.Toast
import com.coder.zzq.smartshow.core.SmartShow
import com.coder.zzq.smartshow.toast.SmartToast
import com.zzx.utils.rxjava.FlowableUtil

/**@author Tomy
 * Created by Tomy on 2014/6/13.
 */
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

    @JvmStatic
    @JvmOverloads
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
                mTTS?.speak(msg, TextToSpeech.QUEUE_FLUSH, null, null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    @JvmOverloads
    fun showToast(msgId: Int, needTTS: Boolean = false, show: Boolean = true, show_time: Int = Toast.LENGTH_SHORT) {
        showToast(mContext!!.getString(msgId), needTTS, show, show_time)
    }

    @JvmStatic
    fun speakTTS(msgId: Int) {
        speakTTS(mContext!!.getString(msgId))
    }

    @JvmStatic
    fun speakTTS(msg: String) {
        try {
            mTTS?.speak(msg, TextToSpeech.QUEUE_FLUSH, null, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
