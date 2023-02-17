package com.zzx.camera.component

import com.zzx.camera.MainActivity
import com.zzx.camera.h9.controller.HViewController
import com.zzx.camera.h9.module.HCameraModule
import com.zzx.camera.module.CameraModule
import com.zzx.camera.service.CameraService
import dagger.Component
import javax.inject.Singleton

/**@author Tomy
 * Created by Tomy on 2018/5/31.
 */
@Singleton
@Component(modules = [(CameraModule::class), (HCameraModule::class)])
interface CameraComponent {

    fun inject(cameraService: CameraService)

    fun inject(viewController: HViewController)

    fun inject(activity: MainActivity)
//    fun inject(viewController: RViewController)
}