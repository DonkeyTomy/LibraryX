package com.tomy.component.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.permissionx.guolindev.PermissionX

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
            PermissionX.init(this).permissions(request).request { allGranted, _, _ ->
                if (allGranted) {
                    granted()
                } else {
                    denied()
                }
            }
        } else {
            granted()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermission({ init() }, { denied() })
    }

    /**
     * 初始化方法.拥有/获取到权限后执行的方法
     */
    open fun init() {}

    /**
     * 未获取权限的方法
     */
    open fun denied() {
        finish()
    }

}