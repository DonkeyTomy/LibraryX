package com.tomy.lib.ui.preference.widget

import android.content.Context
import timber.log.Timber

/**@author Tomy
 * Created by Tomy on 2018/11/23.
 * Need override [resume],[pause]
 */
abstract class BaseEnabler(protected var mContext: Context, protected open var mWidgetController: WidgetController): IBaseEnabler {

    protected var mStartListening = false

    /**
     * 非用户手动设置的不调用状态变化回调.如监听广播变化的不做操作，免得重复操作.
     */
    protected var mStateMachineEvent = false

    /**
     * 初始化Switch,需要刷新下Switch的状态.
     */
    override fun setupController() {
        Timber.e("setupSwitchController()")
        if (!mStartListening) {
            mWidgetController.startListening()
            mStartListening = true
        }
        mWidgetController.setupView()
        refreshState()
    }

    override fun tearDownController() {
        Timber.e("tearDownSwitchController()")
        if (mStartListening) {
            mWidgetController.stopListening()
            mStartListening = false
        }
        mWidgetController.teardownView()
    }

    /**
     * 需要处理事件的监听及刷新状态
     */
    override fun resume() {
        Timber.e("${this.javaClass.simpleName} resume()")
        refreshState()
        startListening()
        if (!mStartListening) {
            mWidgetController.startListening()
            mStartListening = true
        }
    }

    abstract fun refreshState()

    /**
     * 需要停止事件的监听
     */
    override fun pause() {
        Timber.e("${this.javaClass.simpleName} pause()")
        stopListening()
        if (mStartListening) {
            mWidgetController.stopListening()
            mStartListening = false
        }
    }

    /**
     * 处理WiFi状态变化.
     * @param state Int当前的WiFi状态
     */
    override fun handleStateChanged(state: Int) {
        Timber.e("${this.javaClass.simpleName} handleStateChanged().state = $state")
    }

}