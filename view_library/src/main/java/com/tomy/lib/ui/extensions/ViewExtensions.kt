package com.tomy.lib.ui.extensions

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.coder.vincent.smart_snackbar.SmartSnackBar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlin.math.min

/**
 * @author kuky.
 * @description
 */
fun TabLayout.setupWithViewPager2(viewPager2: ViewPager2, titles: Array<String>): TabLayoutMediator =
    TabLayoutMediator(this, viewPager2) { tab, position ->
        tab.text = titles[position]
    }.apply { attach() }

fun TabLayout.setupWithViewPager2(viewPager2: ViewPager2, titles: IntArray): TabLayoutMediator =
    TabLayoutMediator(this, viewPager2) { tab, position ->
        tab.setText(titles[position])
    }.apply { attach() }

@OptIn(DelicateCoroutinesApi::class)
fun RecyclerView.scrollToTop(sizeOneLine: Int = 2, threshold: Int = 10) {
    when (val manager = layoutManager) {
        is GridLayoutManager -> {
            manager.let {
                val first = it.findFirstCompletelyVisibleItemPosition()
                if (first == 0) return@let

                manager.scrollToPositionWithOffset(min(first, threshold), 0)
                GlobalScope.launch(Dispatchers.Main) {
                    delay(10)
                    manager.smoothScrollToPosition(this@scrollToTop, RecyclerView.State(), 0)
                }
            }
        }

        is LinearLayoutManager -> {
            manager.let {
                val first = it.findFirstCompletelyVisibleItemPosition()
                if (first == 0) return@let

                manager.scrollToPositionWithOffset(min(first, threshold), 0)
                GlobalScope.launch(Dispatchers.Main) {
                    delay(10)
                    manager.smoothScrollToPosition(this@scrollToTop, RecyclerView.State(), 0)
                }
            }
        }

        is StaggeredGridLayoutManager -> {
            manager.let {
                val first = IntArray(sizeOneLine)
                it.findFirstCompletelyVisibleItemPositions(first)
                if (first[0] == 0) return@let

                manager.scrollToPositionWithOffset(min(first[0], threshold), 0)
                GlobalScope.launch(Dispatchers.Main) {
                    delay(10)
                    manager.smoothScrollToPosition(this@scrollToTop, RecyclerView.State(), 0)
                }
            }
        }
    }
}

fun EditText.hideSoftInput() {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
        ?.hideSoftInputFromWindow(windowToken, 0)
}

fun EditText.showSoftInput() {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
        ?.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun EditText.clearText() {
    setText("")
}

/**
 * 将文本框里的添加的文字添加到flow里,再从Flow里转换获取别的信息.
 * [callbackFlow]里包含一个Channel可以用来传输数据.
 * 使用:
 * EditText().textWatcher().collect{}
 */
fun TextView.textWatcher() = callbackFlow {
    val watcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            trySend(s.toString()).isSuccess
        }
    }
    addTextChangedListener(watcher)
    awaitClose {
        removeTextChangedListener(watcher)
    }
}

fun ViewPager.onChange(
    stateChange: ((Int) -> Unit)? = null, scrolled: ((Int, Float, Int) -> Unit)? = null,
    selected: ((Int) -> Unit)? = null
) {
    addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {
            stateChange?.invoke(state)
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            scrolled?.invoke(position, positionOffset, positionOffsetPixels)
        }

        override fun onPageSelected(position: Int) {
            selected?.invoke(position)
        }
    })
}

fun FragmentActivity.showSnackBar(@StringRes msg: Int) {
    if (this is AppCompatActivity) {
        SmartSnackBar.bottom(this).show(msg)
    }
}

fun FragmentActivity.showSnackBar(msg: String) {
    if (this is AppCompatActivity) {
        SmartSnackBar.bottom(this).show(msg)
    }
}

fun FragmentActivity.showLongSnackBar(@StringRes msg: Int) {
    if (this is AppCompatActivity) {
        SmartSnackBar.bottom(this).showLong(msg)
    }
}

fun FragmentActivity.showLongSnackBar(msg: String) {
    if (this is AppCompatActivity) {
        SmartSnackBar.bottom(this).showLong(msg)
    }
}