package com.tomy.compose.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import autodispose2.ScopeProvider
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import timber.log.Timber
import java.util.concurrent.TimeUnit

abstract class BaseComposeFragment: Fragment(), KeyEvent.Callback {

    @JvmField
    var mContext: FragmentActivity? = null

    protected lateinit var mScopeProvider: ScopeProvider

    override fun onAttach(context: Activity) {
        super.onAttach(context)
//        Timber.v("${this.javaClass.simpleName} onAttach()")
        mContext = context as FragmentActivity
        initMember()
    }

    override fun onDetach() {
        super.onDetach()
//        Timber.v("${this.javaClass.simpleName} onDetach()")
        releaseMember()
        mContext = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(inflater.context).apply {
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        // Create a ViewWindowInsetObserver using this view, and call start() to
        // start listening now. The WindowInsets instance is returned, allowing us to
        // provide it to AmbientWindowInsets in our content below.
//        val windowInsets = ViewWindowInsetObserver(this)
            // We use the `windowInsetsAnimationsEnabled` parameter to enable animated
            // insets support. This allows our `ConversationContent` to animate with the
            // on-screen keyboard (IME) as it enters/exits the screen.
//            .start(windowInsetsAnimationsEnabled = true)

        setContent {
            CompositionLocalProvider {
                CreateContent()
            }
        }
    }

    /*@Composable
    fun CreateContent1() {
        Surface(
            color = Color.Blue,
        ) {
            Column(
                modifier = Modifier.background(Color.Red)
            ) {

            }

        }
    }*/

    @Composable
    abstract fun CreateContent()

    override fun onResume() {
        super.onResume()
        resumeView()
    }

    override fun onPause() {
        super.onPause()
        pauseView()
    }

    /**
     * 在onResume()中调用.
     * */
    open fun resumeView() {
        Timber.v("${this.javaClass.simpleName} resumeView()")
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

    @SuppressLint("AutoDispose")
    protected fun quite() {
        Timber.d("${this.javaClass.simpleName} quite()")
        lifecycleScope.launchWhenResumed {
            Observable.just(Unit)
                .delay(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mContext?.supportFragmentManager?.popBackStack()
                }, {
                    it.printStackTrace()
                })
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
}