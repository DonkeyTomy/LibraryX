package com.tomy.component.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import timber.log.Timber

/**@author Tomy
 * Created by Tomy on 19/11/2020.
 * layout must need id R.id.container
 */
open class BasePermissionActivity: AppCompatActivity() {
    /**
     * 需要申请的权限组
     * @return Array<String>
     */
    open fun getRequestPermission() = emptyList<String>()

    fun checkPermission(granted:() -> Unit, denied:() -> Unit) {
        val request = getRequestPermission()
        return if (request.isNotEmpty()) {
            XXPermissions.with(this).permission(request).request(object : OnPermissionCallback {
                override fun onGranted(grantedList: MutableList<String>, allGranted: Boolean) {
                    Timber.v("allGranted: $allGranted")
                    grantedList.forEach {
                        Timber.d(it)
                    }
                    if (allGranted) {
                        granted()
                    } else {
                        denied()
                    }
                }

                override fun onDenied(deniedList: MutableList<String>, doNotAskAgain: Boolean) {
                    deniedList.forEach {
                        Timber.e(it)
                    }
                    if (doNotAskAgain) {
                        XXPermissions.startPermissionActivity(
                            this@BasePermissionActivity,
                            deniedList
                        )
                    }
                }
            })
        } else {
            granted()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermission({ init(savedInstanceState) }, { denied() })
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