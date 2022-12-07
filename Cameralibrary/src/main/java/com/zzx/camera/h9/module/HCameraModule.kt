package com.zzx.camera.h9.module

import android.content.Context
import android.hardware.Camera
import android.view.SurfaceHolder
import android.view.View
import com.zzx.camera.h9.presenter.HCameraPresenter
import com.zzx.camera.h9.view.HRecordView
import com.zzx.camera.qualifier.FloatWinContainer
import com.zzx.media.camera.v1.manager.Camera1Manager
import com.zzx.media.custom.view.surface.MySurfaceView
import com.zzx.utils.StorageListener
import dagger.Module
import dagger.Provides

/**@author Tomy
 * Created by Tomy on 2018/10/8.
 */
@Module
class HCameraModule {

    @Provides
    fun provideRecordView(context: Context,@FloatWinContainer rootView: View): HRecordView {
        return HRecordView(context, rootView)
    }

    @Provides
    fun provideCameraPresenter(context: Context, cameraManager: Camera1Manager, surfaceTexture: MySurfaceView,
                               recordView: HRecordView, storageListener: StorageListener): HCameraPresenter<SurfaceHolder, Camera> {
        return HCameraPresenter(context, cameraManager, surfaceTexture, recordView, storageListener)
    }

}