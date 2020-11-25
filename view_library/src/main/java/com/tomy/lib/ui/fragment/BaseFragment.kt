package com.tomy.lib.ui.fragment

import android.app.Activity
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import autodispose2.ScopeProvider
import autodispose2.autoDispose
import butterknife.ButterKnife
import butterknife.Unbinder
import com.tomy.lib.ui.utils.bindLifecycle
import com.zzx.utils.TTSToast
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import timber.log.Timber
import java.util.concurrent.TimeUnit

abstract class BaseFragment: Fragment(), KeyEvent.Callback {

    @JvmField
    var mContext: FragmentActivity? = null
    var mUnBinder: Unbinder? = null

    private var mRootView: View? = null

    fun showToast(msg: String) {
        TTSToast.showToast(msg)
    }

    fun showToast(msg: Int) {
        TTSToast.showToast(msg)
    }

    protected lateinit var mScopeProvider: ScopeProvider

    override fun onAttach(context: Activity) {
        super.onAttach(context)
        Timber.v("${this.javaClass.simpleName} onAttach()")
        mContext = context as FragmentActivity
        initMember()
        mScopeProvider = bindLifecycle()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.v("${this.javaClass.simpleName} onCreate()")
    }

    override fun onDetach() {
        super.onDetach()
        Timber.v("${this.javaClass.simpleName} onDetach()")
        releaseMember()
        mContext = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        if (mRootView == null) {
            mRootView = inflater.inflate(bindLayout(), container, false)
        }
        Timber.d("${this.javaClass.simpleName} onCreateView")
        modifyView(mRootView!!)
        return mRootView!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isBindView()) {
            Timber.d("${this.javaClass.simpleName} bindView()")
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
        Timber.i("keyCode = $keyCode")
        return false
    }

    override fun onKeyLongPress(keyCode: Int, event: KeyEvent?): Boolean {
        return false
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        return false
    }

    override fun onKeyMultiple(keyCode: Int, count: Int, event: KeyEvent?): Boolean {
        return false
    }

    protected fun quite() {
        Timber.d("${this.javaClass.simpleName} quite()")
        mContext?.runOnUiThread {
            Observable.just(Unit)
                    .delay(500, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .autoDispose(mScopeProvider)
                    .subscribe ({
                        mContext?.supportFragmentManager?.popBackStack()
                    }, {
                        it.printStackTrace()
                    })
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        destroyView()
        mUnBinder?.unbind()
        mUnBinder = null
        with(mRootView) {
            this?.parent?.let {
                (it as ViewGroup).removeView(this)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.v("${this.javaClass.simpleName} onDestroy()")
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
        Timber.v("${this.javaClass.simpleName} initView()")
    }

    /**
     * 在[onCreateView]中调用,往Container中增加需要另加的view
     * @param root View
     */
    open fun modifyView(root: View) {
        Timber.v("${this.javaClass.simpleName} modifyView()")
    }

    open fun initData() {
        Timber.v("${this.javaClass.simpleName} initData()")
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
        Timber.v("${this.javaClass.simpleName} initMember()")
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
        Timber.v("${this.javaClass.simpleName} releaseMember()")
    }

    open fun initListener() {
        Timber.v("${this.javaClass.simpleName} initListener()")
    }

    /**
     * 获得LayoutId.
     * */
    abstract fun bindLayout(): Int

    /**
     * 设置是否使用[ButterKnife]绑定View.
     * */
    open fun isBindView(): Boolean {
        return true
    }

}