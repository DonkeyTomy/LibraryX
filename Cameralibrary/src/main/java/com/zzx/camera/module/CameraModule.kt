package com.zzx.camera.module

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.tomy.lib.ui.manager.FloatWinManager
import com.zzx.camera.R
import com.zzx.camera.qualifier.FloatWinContainer
import com.zzx.media.camera.v1.manager.Camera1Manager
import com.zzx.media.custom.view.surface.MySurfaceView
import com.zzx.utils.StorageListener
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**@author Tomy
 * Created by Tomy on 2018/5/31.
 */
@Module
class CameraModule(var mContext: Context) {

    @Provides
    fun provideContext(): Context {
        return mContext
    }

    /*@Provides
    fun provideCameraManager(mContext: Context): Camera2Manager {
        return Camera2Manager(mContext)
    }*/

    @Provides
    fun provideCamera1Manager(): Camera1Manager {
        return Camera1Manager()
    }

    @FloatWinContainer
    @Singleton
    @Provides
    fun provideCameraLayoutId(context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.container_camera, null, false)
    }


    @Singleton
    @Provides
    fun provideSurfaceView(@FloatWinContainer view: View): MySurfaceView {
        return view.findViewById(R.id.camera_view)
    }

    @FloatWinContainer
    @Provides
    fun provideFloatCameraManager(context: Context, @FloatWinContainer rootView: View): FloatWinManager {
        return FloatWinManager(context, rootView)
    }



    @Provides
    fun provideStorageListener(context: Context): StorageListener {
        return StorageListener(context)
    }



}