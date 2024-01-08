package com.tomy.lib.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hjq.permissions.XXPermissions

/**@author Tomy
 * Created by Tomy on 19/11/2020.
 * layout must need id R.id.container
 */
open class BaseActivity: AppCompatActivity() {
    /**
     * 需要申请的权限组
     * @return Array<String>
     */
    open fun getRequestPermission() = emptyArray<String>()

    fun checkPermission(granted:() -> Unit, denied:() -> Unit): Boolean {
        val request = getRequestPermission()
        return if (request.isNotEmpty()) {
            if (!XXPermissions.isGranted(this, request)) {
                XXPermissions.with(this).permission(request).request { _, allGranted ->
                    if (allGranted) {
                        granted()
                    } else {
                        denied()
                    }
                }
                false
            } else {
                true
            }
        } else {
            true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (checkPermission({ init(savedInstanceState) }, { denied() })) {
            init(savedInstanceState)
        }
    }

    /**
     * 初始化方法.拥有/获取到权限后执行的方法
     */
    open fun init(savedInstanceState: Bundle?) {}

    /**
     * 未获取权限的方法
     */
    open fun denied() {
        finish()
    }

}