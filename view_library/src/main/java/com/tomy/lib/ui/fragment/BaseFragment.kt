package com.tomy.lib.ui.fragment

import android.app.Activity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import autodispose2.ScopeProvider
import butterknife.ButterKnife
import butterknife.Unbinder
import com.tomy.lib.ui.utils.bindLifecycle
import com.zzx.utils.TTSToast
import com.zzx.utils.rxjava.toComposeSubscribe
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import timber.log.Timber
import java.util.concurrent.TimeUnit

abstract class BaseFragment: Fragment(), KeyEvent.Callback {

    @JvmField
    var mContext: FragmentActivity? = null
    var mUnBinder: Unbinder? = null

    protected var mRootView: View? = null

    fun showToast(msg: String, needTTS: Boolean = false, show: Boolean = true, showTime: Int = Toast.LENGTH_SHORT) {
        TTSToast.showToast(msg, needTTS, show, showTime)
    }

    fun speakTTS(msg: String) {
        TTSToast.speakTTS(msg)
    }

    fun speakTTS(msg: Int) {
        TTSToast.speakTTS(msg)
    }

    fun showToast(msg: Int, needTTS: Boolean = false, show: Boolean = true, showTime: Int = Toast.LENGTH_SHORT) {
        TTSToast.showToast(msg, needTTS, show, showTime)
    }

    protected lateinit var mScopeProvider: ScopeProvider

    override fun onAttach(context: Activity) {
        super.onAttach(context)
        Timber.v("${this.javaClass.simpleName} onAttach()")
        mContext = context as FragmentActivity
        initMember()
        mScopeProvider = bindLifecycle()
    }

    override fun onDetach() {
        super.onDetach()
        Timber.v("${this.javaClass.simpleName} onDetach()")
        releaseMember()
        mContext = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isBindView()) {
//            Timber.d("${this.javaClass.simpleName} bindView()")
            mUnBinder = ButterKnife.bind(this, view)
        }
        initView(view)
        initData()
        initListener()
    }

    override fun onResume() {
        super.onResume()
        resumeView()
    }

    override fun onPause() {
        super.onPause()
        pauseView()
    }

    /**
      * fragment中的返回键
      *
      * 默认返回false，交给Activity处理
      * 返回true：执行fragment中需要执行的逻辑
      * 返回false：执行activity中的 onBackPressed
      **/
    open fun onBackPressed(): Boolean {
        return false
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.repeatCount < 1) {
            Timber.v("keyCode = $keyCode")
        }
        return false
    }

    override fun onKeyLongPress(keyCode: Int, event: KeyEvent): Boolean {
        return false
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        return false
    }

    override fun onKeyMultiple(keyCode: Int, count: Int, event: KeyEvent): Boolean {
        return false
    }

    protected fun quite() {
        Timber.d("${this.javaClass.simpleName} quite()")
        lifecycleScope.launchWhenResumed {
            Observable.just(Unit)
                .delay(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .toComposeSubscribe ({
                    mContext?.supportFragmentManager?.popBackStack()
                }, {
                    it.printStackTrace()
                }, this@BaseFragment)
        }
    }

    fun backToLauncherFragment() {
        lifecycleScope.launchWhenResumed {
            mContext?.runOnUiThread {
                mContext?.supportFragmentManager?.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
        }
    }

    /**
     * @param needFinishIfIsTop Boolean 是否在当前Fragment处于顶部时退出当前Activity
     */
    fun popToBack(needFinishIfIsTop: Boolean = true) {
        lifecycleScope.launchWhenResumed {
            mContext?.runOnUiThread {
                if (!parentFragmentManager.popBackStackImmediate() && needFinishIfIsTop) {
                    mContext!!.finish()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        destroyView()
        mUnBinder?.unbind()
        mUnBinder = null
        mRootView?.apply {
            parent?.let {
                (it as ViewGroup).removeView(this)
            }
            /**
             * 加了会导致[BaseFragmentDataBind]/[BaseFragmentViewBind]里的HeadBind或者BottomBind在onDestroyView()后再重新执行onCreateView()两者不绑定而添加失败
             */
//            mRootView = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.v("${javaClass.simpleName} onDestroy()")
        mRootView = null
    }
    /**
     * 在onResume()中调用.
     * */
    open fun resumeView() {
        Timber.v("${this.javaClass.simpleName} resumeView()")
    }

    /**
     * 在[onViewCreated]中调用,已BindView,初始化填充视图
     * @param root View
     */
    open fun initView(root: View) {
//        Timber.v("${this.javaClass.simpleName} initView()")
    }

    /**
     * 在[onCreateView]中调用,往Container中增加需要另加的view
     * @param root View
     */
    open fun modifyView(root: View) {
        Timber.v("${this.javaClass.simpleName} modifyView()")
    }

    /**
     * 初始化数据.执行在[initView]之后
     */
    open fun initData() {
//        Timber.v("${this.javaClass.simpleName} initData()")
    }

    /**
     * 在[onDestroyView]中调用.
     */
    open fun destroyView() {
        Timber.d("${this.javaClass.simpleName} destroyView()")
    }

    /**
     * 在[onAttach]中调用
     * 创建时初始化成员变量
     * */
    open fun initMember() {
//        Timber.v("${this.javaClass.simpleName} initMember()")
    }

    /**
     * [onPause]中调用
     * 布局销毁
     * */
    open fun pauseView() {
        Timber.v("${this.javaClass.simpleName} pauseView()")
    }

    /**
     * [onDetach]中调用
     * 从Activity上分离时释放成员变量
     * */
    open fun releaseMember() {
//        Timber.v("${this.javaClass.simpleName} releaseMember()")
    }

    open fun initListener() {
//        Timber.v("${this.javaClass.simpleName} initListener()")
    }

    @Deprecated("已废弃.转换为使用ViewBinding跟ViewDataBinding", replaceWith = ReplaceWith("0"))
    open fun bindLayout(): Int = 0

    /**
     * 设置是否使用[ButterKnife]绑定View.
     * */
    open fun isBindView(): Boolean {
        return false
    }

}